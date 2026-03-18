package lima.fernanda.esteticaFernandaLima.config;

import lima.fernanda.esteticaFernandaLima.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Autowired
    private LogService logService;

    @Pointcut("execution(* lima.fernanda.esteticaFernandaLima.service.*.*(..)) && !execution(* lima.fernanda.esteticaFernandaLima.service.LogService.*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        String classe = joinPoint.getTarget().getClass().getSimpleName();
        String metodo = joinPoint.getSignature().getName();

        long inicio = System.currentTimeMillis();

        log.info("Iniciando: {}.{}", classe, metodo);

        logService.registrarLog(
                "INFO",
                "Iniciando método " + metodo,
                classe,
                "sistema"
        );

        try {

            Object resultado = joinPoint.proceed();

            long tempoExecucao = System.currentTimeMillis() - inicio;

            log.info("Finalizado: {}.{} em {} ms", classe, metodo, tempoExecucao);

            logService.registrarLog(
                    "INFO",
                    "Finalizado método " + metodo + " em " + tempoExecucao + " ms",
                    classe,
                    "sistema"
            );

            return resultado;

        } catch (Exception exception) {

            log.error("Erro em {}.{}: {}", classe, metodo, exception.getMessage());

            logService.registrarLog(
                    "ERROR",
                    "Erro no método " + metodo,
                    classe,
                    "sistema",
                    exception
            );

            throw exception;
        }
    }
}