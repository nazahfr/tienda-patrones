package pe.utp.tienda.behavioral.state;

/** Se lanza cuando se pide una accion que el estado actual del pedido no permite. */
public class TransicionInvalidaException extends IllegalStateException {
    public TransicionInvalidaException(String accion, String estado) {
        super("No se puede " + accion + " un pedido en estado '" + estado + "'");
    }
}
