package pe.utp.tienda.creational.factory;

import pe.utp.tienda.model.Pedido;

public class FacturaElectronica extends ComprobanteBase {
    @Override protected String titulo() { return "FACTURA ELECTRONICA"; }
    @Override protected String serie() { return "F001"; }

    @Override
    protected String descripcionCliente(Pedido pedido) {
        return pedido.getCliente().getNombreCompleto() + " (DNI " + pedido.getCliente().getDni() + ")";
    }
}
