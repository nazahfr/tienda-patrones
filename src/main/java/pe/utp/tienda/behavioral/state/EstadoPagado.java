package pe.utp.tienda.behavioral.state;

import pe.utp.tienda.model.Pedido;

public class EstadoPagado extends EstadoBase {
    @Override public String nombre() { return "Pagado"; }
    @Override public void preparar(Pedido pedido) { pedido.cambiarEstado(new EstadoEnPreparacion()); }
    @Override public void cancelar(Pedido pedido) { pedido.cambiarEstado(new EstadoCancelado()); }
}
