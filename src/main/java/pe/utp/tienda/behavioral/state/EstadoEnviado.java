package pe.utp.tienda.behavioral.state;

import pe.utp.tienda.model.Pedido;

public class EstadoEnviado extends EstadoBase {
    @Override public String nombre() { return "Enviado"; }
    @Override public void entregar(Pedido pedido) { pedido.cambiarEstado(new EstadoEntregado()); }
}
