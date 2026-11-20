package pe.utp.tienda.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class NotificacionService {
    private static final Logger LOG = Logger.getLogger(NotificacionService.class.getName());
    private final List<String> enviados = new ArrayList<>();   // bandeja de salida (la leen las pruebas y la GUI)

    public void enviar(String destinatario, String mensaje) {
        enviados.add(destinatario + ": " + mensaje);
        LOG.info("Notificacion a " + destinatario + ": " + mensaje);
    }

    public List<String> getEnviados() { return Collections.unmodifiableList(enviados); }
}
