package pe.utp.tienda.service;

import java.util.logging.Logger;

public class NotificacionService {
    private static final Logger LOG = Logger.getLogger(NotificacionService.class.getName());

    public void enviar(String destinatario, String mensaje) {
        LOG.info("Notificacion a " + destinatario + ": " + mensaje);
    }
}
