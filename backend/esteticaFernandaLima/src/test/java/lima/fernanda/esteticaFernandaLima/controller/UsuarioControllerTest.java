package lima.fernanda.esteticaFernandaLima.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioCriacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioListarDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioLoginDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioTokenDto;
import lima.fernanda.esteticaFernandaLima.model.Usuario;
import lima.fernanda.esteticaFernandaLima.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UsuarioControllerTest {

    @Mock
    private UsuarioService service;

    @InjectMocks
    private UsuarioController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UsuarioCriacaoDto usuarioCriacaoDto;
    private UsuarioLoginDto usuarioLoginDto;
    private UsuarioTokenDto usuarioTokenDto;
    private UsuarioListarDto usuarioListarDto;

    @BeforeEach
    void setUp() throws Exception {
        try (var ignored = MockitoAnnotations.openMocks(this)) {
            mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
            objectMapper = new ObjectMapper();
        }

        usuarioCriacaoDto = new UsuarioCriacaoDto();
        usuarioCriacaoDto.setNomeCompleto("João Silva");
        usuarioCriacaoDto.setEmail("joao@email.com");
        usuarioCriacaoDto.setSenha("senha123");

        usuarioLoginDto = new UsuarioLoginDto();
        usuarioLoginDto.setEmail("joao@email.com");
        usuarioLoginDto.setSenha("senha123");

        usuarioTokenDto = new UsuarioTokenDto();
        usuarioTokenDto.setUserId(1L);
        usuarioTokenDto.setNome("João Silva");
        usuarioTokenDto.setEmail("joao@email.com");
        usuarioTokenDto.setToken("token_jwt_aqui");

        usuarioListarDto = new UsuarioListarDto();
        usuarioListarDto.setId(1L);
        usuarioListarDto.setNome("João Silva");
        usuarioListarDto.setEmail("joao@email.com");
    }

//    @Test
//    void criar() throws Exception {
//        doNothing().when(service).criar(any(Usuario.class));
//
//        mockMvc.perform(post("/usuarios")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(usuarioCriacaoDto)))
//                .andExpect(status().isCreated());
//
//        verify(service).criar(any(Usuario.class));
//    }

    @Test
    void login() throws Exception {
        when(service.autenticar(any(Usuario.class))).thenReturn(usuarioTokenDto);

        mockMvc.perform(post("/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioLoginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.token").value("token_jwt_aqui"));

        verify(service).autenticar(any(Usuario.class));
    }

    @Test
    void listarTodos() throws Exception {
        List<UsuarioListarDto> usuarios = List.of(usuarioListarDto);
        when(service.listarTodos()).thenReturn(usuarios);

        mockMvc.perform(get("/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$[0].email").value("joao@email.com"));

        verify(service).listarTodos();
    }

    @Test
    void listarTodosVazio() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).listarTodos();
    }
}