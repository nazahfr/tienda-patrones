package pe.utp.tienda.creational.factory;

import pe.utp.tienda.model.Pedido;

/** Creator del Factory Method: las subclases deciden QUE comprobante se instancia. */
public abstract class ComprobanteFactory {

    protected abstract Comprobante crearComprobante(); // <-- factory method

    public String emitir(Pedido pedido, double total) {
        return crearComprobante().generar(pedido, total);
    }

    /** Selecciona el creador segun el tipo solicitado por la caja. */
    public static ComprobanteFactory de(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "BOLETA" -> new BoletaFactory();
            case "FACTURA" -> new FacturaFactory();
            default -> throw new IllegalArgumentException("Tipo de comprobante no soportado: " + tipo);
        };
    }
}
