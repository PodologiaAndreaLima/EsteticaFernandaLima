package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.model.Log;
import lima.fernanda.esteticaFernandaLima.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @Async
    public void registrarLog(String nivel, String mensagem, String classe, String usuario, Exception exception) {
        Log log = new Log(nivel, mensagem, classe, usuario);

        if (exception != null) {
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            log.setStackTrace(sw.toString());
        }

        logRepository.save(log);
    }

    @Async
    public void registrarLog(String nivel, String mensagem, String classe, String usuario) {
        registrarLog(nivel, mensagem, classe, usuario, null);
    }
}
