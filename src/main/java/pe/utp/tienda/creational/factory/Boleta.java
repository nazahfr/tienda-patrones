package pe.utp.tienda.creational.factory;

import pe.utp.tienda.model.Pedido;

public class Boleta extends ComprobanteBase {
    @Override protected String titulo() { return "BOLETA"; }
    @Override protected String serie() { return "B001"; }
    @Override protected String descripcionCliente(Pedido pedido) { return pedido.getCliente().getNombreCompleto(); }
}
