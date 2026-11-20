package pe.utp.tienda.behavioral.observer;

import pe.utp.tienda.service.NotificacionService;

/** Observador concreto: avisa al cliente cada vez que cambia el estado de su pedido. */
public class NotificadorClienteObserver implements Observador<CambioEstadoPedido> {
    private final NotificacionService notificaciones;

    public NotificadorClienteObserver(NotificacionService notificaciones) { this.notificaciones = notificaciones; }

    @Override
    public void actualizar(CambioEstadoPedido e) {
        notificaciones.enviar(e.pedido().getCliente().getNombreCompleto(),
                "Tu pedido paso de '" + e.estadoAnterior() + "' a '" + e.estadoNuevo() + "'");
    }
}
