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
 * - Lembrete de horário: OS que não é retorno, status PENDENTE e atendimento no futuro.
 * - Lembrete de retorno: OS marcada como retorno e status PENDENTE. A data do retorno é
 *   o dt_hora dela; o procedimento e a data do último atendimento vêm da última OS
 *   CONCLUIDA da mesma cliente antes do retorno.
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
            TipoMensagem tipo = os.ehRetorno() ? TipoMensagem.RETORNO : TipoMensagem.LEMBRETE_AGENDAMENTO;
            String chave = chave(os.id().longValue(), tipo);
            vistos.add(chave);
            Lembrete lembrete = existentes.get(chave);

            boolean elegivel = os.statusIgual("PENDENTE") && (os.ehRetorno() || os.dtHora().isAfter(agora));
            Cliente cliente = elegivel && os.clienteId() != null
                    ? clienteRepository.findById(os.clienteId()).orElse(null) : null;
            if (cliente != null && Boolean.FALSE.equals(cliente.getAtivo())) cliente = null;

            if (cliente == null) {
                cancelarSePendente(lembrete);
                continue;
            }
            if (lembrete == null) {
                if (lembreteRepository.existsByClinicaIdAndClienteIdAndDataHoraAndTipo(
                        clinicaId, cliente.getId(), os.dtHora(), tipo)) {
                    continue; // já existe um lembrete manual igual para esta cliente e horário
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
            lembrete.setDataHora(os.dtHora());
            lembrete.setObservacao(limitar(os.observacao(), 500));
            if (tipo == TipoMensagem.RETORNO) {
                OrdemServicoMsResponse anterior = ultimoAtendimentoConcluido(ordens, os, this::nomesDosItens);
                lembrete.setDataRetornoPrevisto(os.dtHora().toLocalDate());
                lembrete.setDataUltimoAtendimento(anterior == null ? null : anterior.dtHora().toLocalDate());
                String procedimento = anterior == null ? null : nomesDosItens(anterior);
                lembrete.setProcedimento(limitar(procedimento != null ? procedimento : nomesDosItens(os), 300));
            } else {
                lembrete.setProcedimento(limitar(nomesDosItens(os), 300));
                lembrete.setDataRetornoPrevisto(null);
                lembrete.setDataUltimoAtendimento(null);
            }
            lembreteRepository.save(lembrete);
        }

        for (Map.Entry<String, Lembrete> entry : existentes.entrySet()) {
            if (!vistos.contains(entry.getKey())) cancelarSePendente(entry.getValue()); // OS excluída
        }
        return true;
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
