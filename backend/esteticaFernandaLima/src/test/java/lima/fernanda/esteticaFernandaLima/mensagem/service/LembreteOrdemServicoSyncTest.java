package lima.fernanda.esteticaFernandaLima.mensagem.service;

import lima.fernanda.esteticaFernandaLima.integration.ordemservico.OrdemServicoMsClient;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsItemResponse;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsResponse;
import lima.fernanda.esteticaFernandaLima.mensagem.model.Lembrete;
import lima.fernanda.esteticaFernandaLima.mensagem.model.StatusLembrete;
import lima.fernanda.esteticaFernandaLima.mensagem.model.TipoMensagem;
import lima.fernanda.esteticaFernandaLima.mensagem.repository.LembreteRepository;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import lima.fernanda.esteticaFernandaLima.repository.ClienteRepository;
import lima.fernanda.esteticaFernandaLima.repository.ComboRepository;
import lima.fernanda.esteticaFernandaLima.repository.ServicoProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LembreteOrdemServicoSyncTest {
    @Mock OrdemServicoMsClient client;
    @Mock LembreteRepository lembreteRepository;
    @Mock ClienteRepository clienteRepository;
    @Mock ServicoProdutoRepository servicoProdutoRepository;
    @Mock ComboRepository comboRepository;
    private LembreteOrdemServicoSync sync;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sync = new LembreteOrdemServicoSync(client, lembreteRepository, clienteRepository,
                servicoProdutoRepository, comboRepository);
        Cliente cliente = new Cliente();
        cliente.setId(7);
        cliente.setNomeCompleto("Mariana Souza");
        cliente.setTelefone("(11) 98888-1111");
        when(clienteRepository.findById(7)).thenReturn(Optional.of(cliente));
        ServicoProduto limpeza = new ServicoProduto();
        limpeza.setNome("Limpeza de pele");
        when(servicoProdutoRepository.findById(10)).thenReturn(Optional.of(limpeza));
        ServicoProduto peeling = new ServicoProduto();
        peeling.setNome("Peeling facial");
        when(servicoProdutoRepository.findById(20)).thenReturn(Optional.of(peeling));
        when(lembreteRepository.findByClinicaIdAndAgendamentoIdIsNotNull("default")).thenReturn(List.of());
    }

    private OrdemServicoMsResponse os(int id, int servicoId, LocalDateTime dtHora, String status, boolean retorno) {
        return new OrdemServicoMsResponse(id, 7, 1, 100f, LocalDate.now(), "obs",
                List.of(new OrdemServicoMsItemResponse(servicoId, null, null, 1, 0f)), dtHora, status, retorno);
    }

    @Test
    void criaLembreteDeHorarioParaOsPendenteFutura() {
        LocalDateTime amanha = LocalDateTime.now().plusDays(1).withNano(0);
        when(client.listarTodas()).thenReturn(List.of(os(1, 10, amanha, "PENDENTE", false)));

        assertTrue(sync.sincronizar("default"));

        ArgumentCaptor<Lembrete> captor = ArgumentCaptor.forClass(Lembrete.class);
        verify(lembreteRepository).save(captor.capture());
        Lembrete lembrete = captor.getValue();
        assertEquals(TipoMensagem.LEMBRETE_AGENDAMENTO, lembrete.getTipo());
        assertEquals(1L, lembrete.getAgendamentoId());
        assertEquals(amanha, lembrete.getDataHora());
        assertEquals("Limpeza de pele", lembrete.getProcedimento());
        assertEquals(StatusLembrete.PENDENTE, lembrete.getStatus());
    }

    @Test
    void criaLembreteDeRetornoComOUltimoAtendimentoConcluido() {
        LocalDateTime atendimento = LocalDateTime.of(2026, 9, 20, 10, 0);
        LocalDateTime retorno = LocalDateTime.of(2026, 10, 20, 0, 0);
        when(client.listarTodas()).thenReturn(List.of(
                os(1, 20, atendimento, "CONCLUIDA", false),
                os(2, 10, retorno, "PENDENTE", true)));

        sync.sincronizar("default");

        ArgumentCaptor<Lembrete> captor = ArgumentCaptor.forClass(Lembrete.class);
        verify(lembreteRepository).save(captor.capture());
        Lembrete lembrete = captor.getValue();
        assertEquals(TipoMensagem.RETORNO, lembrete.getTipo());
        assertEquals(2L, lembrete.getAgendamentoId());
        assertEquals("Peeling facial", lembrete.getProcedimento());
        assertEquals(LocalDate.of(2026, 9, 20), lembrete.getDataUltimoAtendimento());
        assertEquals(LocalDate.of(2026, 10, 20), lembrete.getDataRetornoPrevisto());
    }

    @Test
    void ignoraOsConcluidaCanceladaOuNoPassado() {
        when(client.listarTodas()).thenReturn(List.of(
                os(1, 10, LocalDateTime.now().plusDays(2), "CONCLUIDA", false),
                os(2, 10, LocalDateTime.now().plusDays(2), "CANCELADA", false),
                os(3, 10, LocalDateTime.now().minusDays(1), "PENDENTE", false)));

        sync.sincronizar("default");

        verify(lembreteRepository, never()).save(any());
    }

    @Test
    void cancelaLembretePendenteQuandoAOsFoiCanceladaOuExcluida() {
        Lembrete daOsCancelada = new Lembrete();
        daOsCancelada.setAgendamentoId(1L);
        daOsCancelada.setTipo(TipoMensagem.LEMBRETE_AGENDAMENTO);
        daOsCancelada.setStatus(StatusLembrete.PENDENTE);
        Lembrete daOsExcluida = new Lembrete();
        daOsExcluida.setAgendamentoId(99L);
        daOsExcluida.setTipo(TipoMensagem.LEMBRETE_AGENDAMENTO);
        daOsExcluida.setStatus(StatusLembrete.PENDENTE);
        Lembrete jaEnviado = new Lembrete();
        jaEnviado.setAgendamentoId(98L);
        jaEnviado.setTipo(TipoMensagem.LEMBRETE_AGENDAMENTO);
        jaEnviado.setStatus(StatusLembrete.ENVIADO);
        when(lembreteRepository.findByClinicaIdAndAgendamentoIdIsNotNull("default"))
                .thenReturn(List.of(daOsCancelada, daOsExcluida, jaEnviado));
        when(client.listarTodas()).thenReturn(List.of(
                os(1, 10, LocalDateTime.now().plusDays(2), "CANCELADA", false)));

        sync.sincronizar("default");

        assertEquals(StatusLembrete.CANCELADO, daOsCancelada.getStatus());
        assertEquals(StatusLembrete.CANCELADO, daOsExcluida.getStatus());
        assertEquals(StatusLembrete.ENVIADO, jaEnviado.getStatus());
    }

    @Test
    void naoQuebraQuandoOMicrosservicoNaoResponde() {
        when(client.listarTodas()).thenThrow(new RuntimeException("Connection refused"));

        assertFalse(sync.sincronizar("default"));
        verify(lembreteRepository, never()).save(any());
    }
}
