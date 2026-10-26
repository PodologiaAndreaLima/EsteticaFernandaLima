package lima.fernanda.esteticaFernandaLima.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.*;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioDetalhesDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;

public class NotaFiscalProtecaoFilter extends OncePerRequestFilter {
    private final ConcurrentHashMap<String, Janela> janelas = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;
    private final Clock clock;
    private final NotaProperties propriedades;

    public NotaFiscalProtecaoFilter(ObjectMapper mapper, Clock clock) {
        this(mapper, clock, null);
    }

    public NotaFiscalProtecaoFilter(ObjectMapper mapper, Clock clock, NotaProperties propriedades) {
        this.mapper = mapper;
        this.clock = clock;
        this.propriedades = propriedades;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!request.getRequestURI().startsWith(request.getContextPath() + "/estoque/notas")) {
            chain.doFilter(request, response);
            return;
        }
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Content-Security-Policy", "default-src 'none'");
        response.setHeader("Referrer-Policy", "no-referrer");
        long minuto = clock.millis() / 60000;
        if (janelas.size() > 10000) janelas.entrySet().removeIf(e -> e.getValue().minuto() < minuto);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var usuario = auth != null && auth.getPrincipal() instanceof UsuarioDetalhesDto p ? p : null;
        String origem = usuario != null && usuario.getId() != null ? "u:" + usuario.getId()
                : "ip:" + request.getRemoteAddr();
        boolean upload = request.getMethod().equals("POST")
                && request.getRequestURI().equals(request.getContextPath() + "/estoque/notas");
        int geral = usuario != null && propriedades != null ? propriedades.rateLimit().requisicoesPorMinuto() : 120;
        int uploads = usuario != null && propriedades != null ? propriedades.rateLimit().uploadsPorMinuto() : 120;
        if (origem.equals("ip:null") || (!janelas.containsKey(origem) && janelas.size() >= 10000)
                || excedeu(origem, minuto, upload, geral, uploads)) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            mapper.writeValue(response.getWriter(), new Erro(CodigoErro.LIMITE_EXCEDIDO,
                    "Limite de solicitações excedido.", null, null));
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean excedeu(String origem, long minuto, boolean upload, int geral, int uploads) {
        Janela atual = janelas.compute(origem, (key, anterior) -> anterior == null || anterior.minuto() != minuto
                ? new Janela(minuto, 1, upload ? 1 : 0)
                : new Janela(minuto, anterior.contagem() + 1, anterior.uploads() + (upload ? 1 : 0)));
        return atual.contagem() > geral || atual.uploads() > uploads;
    }

    private record Janela(long minuto, int contagem, int uploads) { }
}
