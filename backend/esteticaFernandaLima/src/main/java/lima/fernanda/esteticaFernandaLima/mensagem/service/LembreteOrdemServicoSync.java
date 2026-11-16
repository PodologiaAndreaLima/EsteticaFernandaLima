package lima.fernanda.esteticaFernandaLima.mensagem.service;

import lima.fernanda.esteticaFernandaLima.integration.ordemservico.OrdemServicoMsClient;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsItemResponse;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsResponse;
import lima.fernanda.esteticaFernandaLima.mensagem.model.Lembrete;
import lima.fernanda.esteticaFernandaLima.mensagem.model.StatusLembrete;
import lima.fernanda.esteticaFernandaLima.mensagem.model.TipoMensagem;
import lima.fernanda.esteticaFernandaLima.mensagem.repository.LembreteRepository;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.model.Combo;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import lima.fernanda.esteticaFernandaLima.repository.ClienteRepository;
import lima.fernanda.esteticaFernandaLima.repository.ComboRepository;
import lima.fernanda.esteticaFernandaLima.repository.ServicoProdutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Monta os lembretes a partir das ordens de serviço do microsserviço de OS.
 *
 * Regras:
 * - Lembrete de horário: OS com status PENDENTE e atendimento no futuro.
 * - Lembrete de retorno: OS com data de retorno prevista ("Precisa de retorno?") e que não foi
 *   cancelada. O atendimento da própria OS é o último atendimento e os itens dela são o
 *   procedimento. Uma OS pode gerar os dois lembretes.
 * - OS antigas marcadas como retorno (sem data prevista): status PENDENTE; a data do retorno é
 *   o dt_hora dela e o procedimento e a data do último atendimento vêm da última OS CONCLUIDA
 *   da mesma cliente antes do retorno.
 * - OS que deixa de se encaixar (concluída, cancelada, excluída, horário já passou)
 *   cancela o lembrete se ele ainda estiver PENDENTE. Lembretes já enviados não mudam.
 * - Lembretes criados manualmente (sem OS) não são tocados.
 * O id da OS fica em Lembrete.agendamentoId.
 */
@Service
public class LembreteOrdemServicoSync {
    private static final Logger log = LoggerFactory.getLogger(LembreteOrdemServicoSync.class);

    private final OrdemServicoMsClient ordemServicoClient;
    private final LembreteRepository lembreteRepository;
    private final ClienteRepository clienteRepository;
    private final ServicoProdutoRepository servicoProdutoRepository;
    private final ComboRepository comboRepository;

    public LembreteOrdemServicoSync(OrdemServicoMsClient ordemServicoClient, LembreteRepository lembreteRepository,
                                    ClienteRepository clienteRepository,
                                    ServicoProdutoRepository servicoProdutoRepository,
                                    ComboRepository comboRepository) {
        this.ordemServicoClient = ordemServicoClient;
        this.lembreteRepository = lembreteRepository;
        this.clienteRepository = clienteRepository;
        this.servicoProdutoRepository = servicoProdutoRepository;
        this.comboRepository = comboRepository;
    }

    /** Retorna false quando o microsserviço de OS não respondeu (a lista segue com o que já existe). */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sincronizar(String clinicaId) {
        List<OrdemServicoMsResponse> ordens;
        try {
            ordens = ordemServicoClient.listarTodas();
        } catch (RuntimeException ex) {
            log.warn("Não foi possível consultar as ordens de serviço para montar os lembretes: {}", ex.getMessage());
            return false;
        }
        LocalDateTime agora = LocalDateTime.now();
        Map<String, Lembrete> existentes = new HashMap<>();
        for (Lembrete l : lembreteRepository.findByClinicaIdAndAgendamentoIdIsNotNull(clinicaId)) {
            existentes.put(chave(l.getAgendamentoId(), l.getTipo()), l);
        }
        Set<String> vistos = new HashSet<>();

        for (OrdemServicoMsResponse os : ordens) {
            if (os == null || os.id() == null || os.dtHora() == null) continue;
            if (os.dataRetorno() != null) {
                // OS com "Precisa de retorno?": lembrete do horário (se ainda vai acontecer) e do retorno
                sincronizarHorario(clinicaId, os, existentes, vistos, agora);
                sincronizarRetornoPrevisto(clinicaId, os, existentes, vistos);
            } else if (os.ehRetorno()) {
                sincronizarRetornoLegado(clinicaId, os, ordens, existentes, vistos);
            } else {
                sincronizarHorario(clinicaId, os, existentes, vistos, agora);
            }
        }

        for (Map.Entry<String, Lembrete> entry : existentes.entrySet()) {
            if (!vistos.contains(entry.getKey())) cancelarSePendente(entry.getValue()); // OS excluída
        }
        return true;
    }

    /** Lembrete de horário: OS PENDENTE com atendimento no futuro. */
    private void sincronizarHorario(String clinicaId, OrdemServicoMsResponse os, Map<String, Lembrete> existentes,
                                    Set<String> vistos, LocalDateTime agora) {
        boolean elegivel = os.statusIgual("PENDENTE") && os.dtHora().isAfter(agora);
        Lembrete lembrete = preparar(clinicaId, os, TipoMensagem.LEMBRETE_AGENDAMENTO, os.dtHora(), elegivel, existentes, vistos);
        if (lembrete == null) return;
        lembrete.setProcedimento(limitar(nomesDosItens(os), 300));
        lembrete.setDataRetornoPrevisto(null);
        lembrete.setDataUltimoAtendimento(null);
        lembreteRepository.save(lembrete);
    }

    /**
     * Lembrete de retorno a partir da data prevista na OS. O atendimento desta OS é o último
     * atendimento e os itens dela são o procedimento. Só deixa de valer se a OS for cancelada.
     */
    private void sincronizarRetornoPrevisto(String clinicaId, OrdemServicoMsResponse os, Map<String, Lembrete> existentes,
                                            Set<String> vistos) {
        boolean elegivel = !os.statusIgual("CANCELADA");
        Lembrete lembrete = preparar(clinicaId, os, TipoMensagem.RETORNO, os.dataRetorno().atStartOfDay(), elegivel, existentes, vistos);
        if (lembrete == null) return;
        lembrete.setDataRetornoPrevisto(os.dataRetorno());
        lembrete.setDataUltimoAtendimento(os.dtHora().toLocalDate());
        lembrete.setProcedimento(limitar(nomesDosItens(os), 300));
        lembreteRepository.save(lembrete);
    }

    /** OS antigas marcadas como "é um retorno": a data do retorno é o dt_hora dela. */
    private void sincronizarRetornoLegado(String clinicaId, OrdemServicoMsResponse os, List<OrdemServicoMsResponse> ordens,
                                          Map<String, Lembrete> existentes, Set<String> vistos) {
        Lembrete lembrete = preparar(clinicaId, os, TipoMensagem.RETORNO, os.dtHora(), os.statusIgual("PENDENTE"), existentes, vistos);
        if (lembrete == null) return;
        OrdemServicoMsResponse anterior = ultimoAtendimentoConcluido(ordens, os, this::nomesDosItens);
        lembrete.setDataRetornoPrevisto(os.dtHora().toLocalDate());
        lembrete.setDataUltimoAtendimento(anterior == null ? null : anterior.dtHora().toLocalDate());
        String procedimento = anterior == null ? null : nomesDosItens(anterior);
        lembrete.setProcedimento(limitar(procedimento != null ? procedimento : nomesDosItens(os), 300));
        lembreteRepository.save(lembrete);
    }

    /**
     * Devolve o lembrete (novo ou existente) já com cliente, data e observação, ou null quando não
     * há o que salvar. Lembrete pendente de OS que deixou de ser elegível é cancelado.
     */
    private Lembrete preparar(String clinicaId, OrdemServicoMsResponse os, TipoMensagem tipo, LocalDateTime dataHora,
                              boolean elegivel, Map<String, Lembrete> existentes, Set<String> vistos) {
        String chave = chave(os.id().longValue(), tipo);
        vistos.add(chave);
        Lembrete lembrete = existentes.get(chave);

        Cliente cliente = elegivel && os.clienteId() != null
                ? clienteRepository.findById(os.clienteId()).orElse(null) : null;
        if (cliente != null && Boolean.FALSE.equals(cliente.getAtivo())) cliente = null;
        if (cliente == null) {
            cancelarSePendente(lembrete);
            return null;
        }
        if (lembrete == null) {
            if (lembreteRepository.existsByClinicaIdAndClienteIdAndDataHoraAndTipo(
                    clinicaId, cliente.getId(), dataHora, tipo)) {
                return null; // já existe um lembrete manual igual para esta cliente e data
            }
            lembrete = new Lembrete();
            lembrete.setClinicaId(clinicaId);
            lembrete.setAgendamentoId(os.id().longValue());
            lembrete.setTipo(tipo);
            lembrete.setStatus(StatusLembrete.PENDENTE);
        } else if (lembrete.getStatus() == StatusLembrete.CANCELADO) {
            lembrete.setStatus(StatusLembrete.PENDENTE); // OS voltou a valer
        }
        lembrete.setCliente(cliente);
        lembrete.setDataHora(dataHora);
        lembrete.setObservacao(limitar(os.observacao(), 500));
        return lembrete;
    }

    /**
     * Última OS CONCLUIDA da mesma cliente antes do retorno. Dá preferência à mais recente que
     * tenha procedimento identificável, para que data e procedimento venham do mesmo atendimento.
     */
    static OrdemServicoMsResponse ultimoAtendimentoConcluido(List<OrdemServicoMsResponse> ordens,
                                                            OrdemServicoMsResponse retorno,
                                                            java.util.function.Function<OrdemServicoMsResponse, String> nomes) {
        List<OrdemServicoMsResponse> concluidas = ordens.stream()
                .filter(o -> o != null && o.dtHora() != null && !Objects.equals(o.id(), retorno.id()))
                .filter(o -> Objects.equals(o.clienteId(), retorno.clienteId()))
                .filter(o -> o.statusIgual("CONCLUIDA"))
                .filter(o -> o.dtHora().isBefore(retorno.dtHora()))
                .sorted(Comparator.comparing(OrdemServicoMsResponse::dtHora).reversed())
                .toList();
        return concluidas.stream()
                .filter(o -> nomes.apply(o) != null)
                .findFirst()
                .orElse(concluidas.isEmpty() ? null : concluidas.get(0));
    }

    private String nomesDosItens(OrdemServicoMsResponse os) {
        if (os.itens() == null) return null;
        String nomes = os.itens().stream()
                .map(this::nomeDoItem)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));
        return nomes.isBlank() ? null : nomes;
    }

    private String nomeDoItem(OrdemServicoMsItemResponse item) {
        if (item == null) return null;
        if (item.comboId() != null) {
            return comboRepository.findById(item.comboId()).map(Combo::getNome).orElse(null);
        }
        Integer id = item.servicoProdutoId() != null ? item.servicoProdutoId() : item.produtoId();
        return id == null ? null : servicoProdutoRepository.findById(id).map(ServicoProduto::getNome).orElse(null);
    }

    private void cancelarSePendente(Lembrete lembrete) {
        if (lembrete != null && lembrete.getStatus() == StatusLembrete.PENDENTE) {
            lembrete.setStatus(StatusLembrete.CANCELADO);
            lembreteRepository.save(lembrete);
        }
    }

    private static String chave(Long osId, TipoMensagem tipo) {
        return osId + ":" + tipo;
    }

    private static String limitar(String valor, int max) {
        if (valor == null) return null;
        String limpo = valor.trim();
        return limpo.length() <= max ? limpo : limpo.substring(0, max);
    }
}
