package pe.utp.tienda.behavioral.state;

import pe.utp.tienda.model.Pedido;

/**
 * State (GoF): interfaz comun de los estados del ciclo de vida de un pedido.
 * Cada estado decide que transiciones permite, sin if/switch en el contexto (Pedido).
 */
public interface EstadoPedido {
    String nombre();
    void pagar(Pedido pedido);
    void preparar(Pedido pedido);
    void enviar(Pedido pedido);
    void entregar(Pedido pedido);
    void cancelar(Pedido pedido);

    /** Reconstruye el estado a partir de su nombre (al leer un pedido guardado en la base de datos). */
    static EstadoPedido de(String nombre) {
        return switch (nombre) {
            case "Pendiente" -> new EstadoPendiente();
            case "Pagado" -> new EstadoPagado();
            case "En Preparacion" -> new EstadoEnPreparacion();
            case "Enviado" -> new EstadoEnviado();
            case "Entregado" -> new EstadoEntregado();
            case "Cancelado" -> new EstadoCancelado();
            default -> throw new IllegalArgumentException("Estado de pedido desconocido: " + nombre);
        };
    }
}
