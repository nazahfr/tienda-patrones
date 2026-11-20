package pe.utp.tienda.behavioral.state;

import pe.utp.tienda.model.Pedido;

public class EstadoEnPreparacion extends EstadoBase {
    @Override public String nombre() { return "En Preparacion"; }
    @Override public void enviar(Pedido pedido) { pedido.cambiarEstado(new EstadoEnviado()); }
    @Override public void cancelar(Pedido pedido) { pedido.cambiarEstado(new EstadoCancelado()); }
}
