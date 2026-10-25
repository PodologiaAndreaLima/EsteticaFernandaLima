package lima.fernanda.esteticaFernandaLima.mensagem.service;

import lima.fernanda.esteticaFernandaLima.mensagem.dto.*;
import lima.fernanda.esteticaFernandaLima.mensagem.exception.*;
import lima.fernanda.esteticaFernandaLima.mensagem.mapper.MensagemMapper;
import lima.fernanda.esteticaFernandaLima.mensagem.model.*;
import lima.fernanda.esteticaFernandaLima.mensagem.provider.*;
import lima.fernanda.esteticaFernandaLima.mensagem.generator.MessageGenerator;
import lima.fernanda.esteticaFernandaLima.mensagem.generator.MessageGenerationContext;
import lima.fernanda.esteticaFernandaLima.mensagem.repository.*;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.OrdemServicoMsClient;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsResponse;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.repository.ClienteRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LembreteService {
    private final LembreteRepository lembreteRepository;
    private final MensagemRepository mensagemRepository;
    private final ClienteRepository clienteRepository;
    private final MensagemMapper mapper;
    private final MessageGenerator generator;
    private final MessageSender sender;
    private final OrdemServicoMsClient ordemServicoClient;
    private final LembreteOrdemServicoSync ordemServicoSync;

    public LembreteService(LembreteRepository lembreteRepository, MensagemRepository mensagemRepository,
                           ClienteRepository clienteRepository, MensagemMapper mapper,
                           MessageGenerator generator, MessageSender sender) {
        this(lembreteRepository, mensagemRepository, clienteRepository, mapper, generator, sender, null, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public LembreteService(LembreteRepository lembreteRepository, MensagemRepository mensagemRepository,
                           ClienteRepository clienteRepository, MensagemMapper mapper,
                           MessageGenerator generator, MessageSender sender,
                           OrdemServicoMsClient ordemServicoClient,
                           LembreteOrdemServicoSync ordemServicoSync) {
        this.lembreteRepository = lembreteRepository;
        this.mensagemRepository = mensagemRepository;
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
        this.generator = generator;
        this.sender = sender;
        this.ordemServicoClient = ordemServicoClient;
        this.ordemServicoSync = ordemServicoSync;
    }

    @Transactional
    public LembreteResponse criar(CriarLembreteRequest request) {
        String clinica = normalizarClinica(request.clinicaId());
        if (request.tipo() != TipoMensagem.RETORNO && !request.dataHora().isAfter(LocalDateTime.now())) {
            throw new ValidacaoMensagemException("dataHora: o atendimento precisa estar no futuro");
        }
        if (lembreteRepository.existsByClinicaIdAndClienteIdAndDataHoraAndTipo(
                clinica, request.clienteId(), request.dataHora(), request.tipo())) {
            throw new RegraMensagemException("Já existe um lembrete igual para este cliente e horário");
        }
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
        Lembrete lembrete = new Lembrete();
        lembrete.setClinicaId(clinica);
        lembrete.setCliente(cliente);
        lembrete.setAgendamentoId(request.agendamentoId());
        lembrete.setDataHora(request.dataHora());
        lembrete.setTipo(request.tipo());
        lembrete.setObservacao(request.observacao());
        lembrete.setDataUltimoAtendimento(request.dataUltimoAtendimento());
        lembrete.setDataRetornoPrevisto(request.dataRetornoPrevisto());
        lembrete.setProcedimento(request.procedimento());
        return mapper.toResponse(lembreteRepository.save(lembrete));
    }

    @Transactional
    public PageResponse<LembreteResponse> listar(String clinicaId, int page, int size) {
        if (ordemServicoSync != null) {
            ordemServicoSync.sincronizar(normalizarClinica(clinicaId)); // lembretes vêm das ordens de serviço
        }
        Page<Lembrete> result = lembreteRepository.findByClinicaId(normalizarClinica(clinicaId),
                PageRequest.of(page, size, Sort.by("dataHora").ascending()));
        return PageResponse.from(result.map(mapper::toResponse));
    }

    @Transactional(readOnly = true)
    public Lembrete buscar(Long id, String clinicaId) {
        return lembreteRepository.findByIdAndClinicaId(id, normalizarClinica(clinicaId))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lembrete não encontrado"));
    }

    @Transactional(readOnly = true)
    public LembreteResponse mapperResponse(Lembrete lembrete) {
        return mapper.toResponse(lembrete);
    }

    @Transactional
    public LembreteResponse cancelar(Long id, String clinicaId) {
        Lembrete lembrete = buscar(id, clinicaId);
        if (lembrete.getStatus() == StatusLembrete.ENVIADO)
            throw new RegraMensagemException("Lembrete enviado não pode ser cancelado");
        if (lembrete.getStatus() == StatusLembrete.CANCELADO)
            throw new RegraMensagemException("Lembrete já está cancelado");
        lembrete.setStatus(StatusLembrete.CANCELADO);
        return mapper.toResponse(lembreteRepository.save(lembrete));
    }

    @Transactional(noRollbackFor = RegraMensagemException.class)
    public MensagemResponse enviar(Long id, String clinicaId, CanalMensagem canal) {
        return enviar(id, clinicaId, new EnviarMensagemRequest(canal, null));
    }

    @Transactional
    public GerarMensagemResponse gerar(Long id, String clinicaId) {
        Lembrete lembrete = buscar(id, clinicaId);
        if (lembrete.getStatus() == StatusLembrete.CANCELADO) {
            throw new RegraMensagemException("Não é permitido gerar mensagem para lembrete cancelado");
        }
        String conteudo = generator.generate(lembrete, contexto(lembrete));
        if (conteudo == null || conteudo.isBlank() || conteudo.length() > 2000) {
            throw new GeracaoMensagemException("O gerador retornou uma mensagem inválida");
        }
        return new GerarMensagemResponse(lembrete.getId(), lembrete.getTipo(), conteudo.trim(), true);
    }

    /**
     * Registra o envio no histórico. Com o provedor padrão (whatsapp-link) nada é
     * disparado: a resposta traz o whatsappUrl para o app abrir o WhatsApp com o
     * número e a mensagem prontos. Como o envio final é manual, o mesmo lembrete
     * pode ser reenviado (cada tentativa vira um registro no histórico).
     * noRollbackFor mantém o registro de FALHA quando o cliente não tem telefone.
     */
    @Transactional(noRollbackFor = RegraMensagemException.class)
    public MensagemResponse enviar(Long id, String clinicaId, EnviarMensagemRequest request) {
        Lembrete lembrete = buscar(id, clinicaId);
        if (lembrete.getStatus() == StatusLembrete.CANCELADO)
            throw new RegraMensagemException("Não é permitido enviar lembrete cancelado");
        String telefone = lembrete.getCliente().getTelefone();
        if (telefone == null || telefone.isBlank()) {
            Mensagem mensagemSemTelefone = new Mensagem();
            mensagemSemTelefone.setClinicaId(lembrete.getClinicaId());
            mensagemSemTelefone.setLembrete(lembrete);
            mensagemSemTelefone.setCanal(request == null || request.canal() == null
                    ? CanalMensagem.WHATSAPP : request.canal());
            mensagemSemTelefone.setTelefoneDestino("");
            String conteudoInformado = request == null ? null : request.conteudo();
            mensagemSemTelefone.setConteudo(conteudoInformado == null || conteudoInformado.isBlank()
                    ? "(mensagem não enviada: cliente sem telefone)" : conteudoInformado.trim());
            mensagemSemTelefone.setStatus(StatusMensagem.FALHA);
            mensagemSemTelefone.setErroProvedor("Cliente não possui telefone para envio");
            mensagemRepository.save(mensagemSemTelefone);
            throw new RegraMensagemException("Cliente não possui telefone para envio");
        }
        Mensagem mensagem = new Mensagem();
        mensagem.setClinicaId(lembrete.getClinicaId());
        mensagem.setLembrete(lembrete);
        mensagem.setCanal(request == null || request.canal() == null ? CanalMensagem.WHATSAPP : request.canal());
        mensagem.setTelefoneDestino(telefone == null ? "" : telefone);
        String conteudo = request == null ? null : request.conteudo();
        mensagem.setConteudo(conteudo == null || conteudo.isBlank()
                ? generator.generate(lembrete, contexto(lembrete)) : conteudo.trim());
        mensagem.setStatus(StatusMensagem.GERADA);
        mensagem = mensagemRepository.save(mensagem);
        try {
            sender.send(mensagem.getCanal(), telefone, mensagem.getConteudo());
            mensagem.setStatus(StatusMensagem.ENVIADA);
            mensagem.setEnviadoEm(LocalDateTime.now());
            lembrete.setStatus(StatusLembrete.ENVIADO);
            lembreteRepository.save(lembrete);
        } catch (MessageProviderException exception) {
            mensagem.setStatus(StatusMensagem.FALHA);
            mensagem.setErroProvedor(exception.getMessage());
        }
        return mapper.toResponse(mensagemRepository.save(mensagem));
    }

    private MessageGenerationContext contexto(Lembrete lembrete) {
        String procedimento = lembrete.getProcedimento();
        java.time.LocalDate ultimo = lembrete.getDataUltimoAtendimento();
        if (lembrete.getAgendamentoId() != null && ordemServicoClient != null) {
            try {
                OrdemServicoMsResponse ordem = ordemServicoClient.buscarPorId(lembrete.getAgendamentoId().intValue());
                if (ordem == null) throw new GeracaoMensagemException("Ordem de serviço não encontrada");
                if (ultimo == null && lembrete.getTipo() != TipoMensagem.RETORNO) ultimo = ordem.dataCriacao();
            } catch (GeracaoMensagemException ex) {
                throw ex;
            } catch (RuntimeException ex) {
                throw new GeracaoMensagemException("Não foi possível consultar os dados da ordem de serviço", ex);
            }
        }
        return new MessageGenerationContext(lembrete.getCliente().getNomeCompleto(), procedimento,
                lembrete.getDataHora(), ultimo, lembrete.getDataRetornoPrevisto(),
                lembrete.getObservacao(), lembrete.getAgendamentoId() != null);
    }

    @Transactional(readOnly = true)
    public PageResponse<MensagemResponse> historico(String clinicaId, Long lembreteId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("criadoEm").descending());
        Page<Mensagem> result = lembreteId == null
                ? mensagemRepository.findByClinicaId(normalizarClinica(clinicaId), pageable)
                : mensagemRepository.findByClinicaIdAndLembreteId(normalizarClinica(clinicaId), lembreteId, pageable);
        return PageResponse.from(result.map(mapper::toResponse));
    }

    private String normalizarClinica(String clinicaId) {
        return clinicaId == null || clinicaId.isBlank() ? "default" : clinicaId.trim();
    }
}
