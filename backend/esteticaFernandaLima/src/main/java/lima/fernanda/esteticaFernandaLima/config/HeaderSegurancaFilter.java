package lima.fernanda.esteticaFernandaLima.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class HeaderSegurancaFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // X-XSS-Protection - Ativa proteção XSS do navegador
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // X-Content-Type-Options - Previne MIME sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");

        //  X-Frame-Options - Previne clickjacking
        response.setHeader("X-Frame-Options", "DENY");

        // Content-Security-Policy - Restrige o que pode ser executado
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; " +
                "script-src 'self'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data:; " +
                "font-src 'self'; " +
                "connect-src 'self'; " +
                "frame-ancestors 'none'; " +
                "base-uri 'self'; " +
                "form-action 'self'");

        //  Referrer-Policy - Controla qual referrer é enviado
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        //Permissions-Policy - Restringe APIs do navegador
        response.setHeader("Permissions-Policy",
                "geolocation=(), " +
                "microphone=(), " +
                "camera=(), " +
                "payment=(), " +
                "usb=(), " +
                "magnetometer=(), " +
                "gyroscope=(), " +
                "accelerometer=()");

        // Strict-Transport-Security - Force HTTPS
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");

        // Desativa cache para dados sensíveis
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        filterChain.doFilter(request, response);
    }
}

