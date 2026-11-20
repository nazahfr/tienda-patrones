package pe.utp.tienda.behavioral.state;

import pe.utp.tienda.model.Pedido;

public class EstadoPendiente extends EstadoBase {
    @Override public String nombre() { return "Pendiente"; }
    @Override public void pagar(Pedido pedido) { pedido.cambiarEstado(new EstadoPagado()); }
    @Override public void cancelar(Pedido pedido) { pedido.cambiarEstado(new EstadoCancelado()); }
}
