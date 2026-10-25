package lima.fernanda.esteticaFernandaLima.mensagem.service;

import lima.fernanda.esteticaFernandaLima.adapter.Adapter;
import lima.fernanda.esteticaFernandaLima.dto.ClienteResponse;
import lima.fernanda.esteticaFernandaLima.mensagem.dto.CriarLembreteRequest;
import lima.fernanda.esteticaFernandaLima.mensagem.dto.EnviarMensagemRequest;
import lima.fernanda.esteticaFernandaLima.mensagem.dto.GerarMensagemResponse;
import lima.fernanda.esteticaFernandaLima.mensagem.exception.RegraMensagemException;
import lima.fernanda.esteticaFernandaLima.mensagem.generator.MessageGenerator;
import lima.fernanda.esteticaFernandaLima.mensagem.mapper.MensagemMapper;
import lima.fernanda.esteticaFernandaLima.mensagem.model.*;
import lima.fernanda.esteticaFernandaLima.mensagem.provider.MessageSender;
import lima.fernanda.esteticaFernandaLima.mensagem.repository.*;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.repository.ClienteRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LembreteServiceTest {
    @Mock LembreteRepository lembreteRepository;
    @Mock MensagemRepository mensagemRepository;
    @Mock ClienteRepository clienteRepository;
    @Mock MensagemMapper mapper;
    @Mock MessageGenerator generator;
    @Mock MessageSender sender;
    private LembreteService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new LembreteService(lembreteRepository, mensagemRepository, clienteRepository,
                mapper, generator, sender);
    }

    @Test
    void impedeDuplicidadeNoMesmoTenant() {
        LocalDateTime data = LocalDateTime.now().plusDays(1);
        when(lembreteRepository.existsByClinicaIdAndClienteIdAndDataHoraAndTipo(
                "clinica-a", 1, data, TipoMensagem.LEMBRETE_AGENDAMENTO)).thenReturn(true);

        assertThrows(RegraMensagemException.class, () -> service.criar(new CriarLembreteRequest(
                1, null, data, TipoMensagem.LEMBRETE_AGENDAMENTO, null, "clinica-a")));
        verifyNoInteractions(clienteRepository);
    }

    @Test
    void naoEnviaSemTelefone() {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNomeCompleto("Cliente");
        cliente.setTelefone(" ");
        Lembrete lembrete = new Lembrete();
        lembrete.setId(9L);
        lembrete.setCliente(cliente);
        lembrete.setStatus(StatusLembrete.PENDENTE);
        when(lembreteRepository.findByIdAndClinicaId(9L, "default")).thenReturn(Optional.of(lembrete));

        assertThrows(RegraMensagemException.class,
                () -> service.enviar(9L, null, CanalMensagem.WHATSAPP));
        verifyNoInteractions(sender);
    }

    @Test
    void geraSemEnviar() {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNomeCompleto("Mariana");
        Lembrete lembrete = new Lembrete();
        lembrete.setId(10L);
        lembrete.setCliente(cliente);
        lembrete.setTipo(TipoMensagem.LEMBRETE_AGENDAMENTO);
        lembrete.setDataHora(LocalDateTime.now().plusDays(1));
        when(lembreteRepository.findByIdAndClinicaId(10L, "default")).thenReturn(Optional.of(lembrete));
        when(generator.generate(eq(lembrete), any())).thenReturn("Sugestão para revisão");

        GerarMensagemResponse response = service.gerar(10L, null);

        assertEquals("Sugestão para revisão", response.conteudo());
        verifyNoInteractions(sender);
        verify(mensagemRepository, never()).save(any());
    }

    @Test
    void enviaTextoRevisadoSemRegenerar() {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNomeCompleto("Mariana");
        cliente.setTelefone("5511999999999");
        Lembrete lembrete = new Lembrete();
        lembrete.setId(11L);
        lembrete.setCliente(cliente);
        lembrete.setClinicaId("default");
        lembrete.setStatus(StatusLembrete.PENDENTE);
        lembrete.setTipo(TipoMensagem.LEMBRETE_AGENDAMENTO);
        lembrete.setDataHora(LocalDateTime.now().plusDays(1));
        when(lembreteRepository.findByIdAndClinicaId(11L, "default")).thenReturn(Optional.of(lembrete));
        when(mensagemRepository.save(any(Mensagem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.enviar(11L, null, new EnviarMensagemRequest(CanalMensagem.WHATSAPP, "Texto revisado"));

        verify(sender).send(CanalMensagem.WHATSAPP, "5511999999999", "Texto revisado");
        verify(generator, never()).generate(any(), any());
    }
}
