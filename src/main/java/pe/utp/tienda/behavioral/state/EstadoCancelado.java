package pe.utp.tienda.behavioral.state;

import pe.utp.tienda.model.Pedido;

public class EstadoCancelado extends EstadoBase {
    @Override public String nombre() { return "Cancelado"; }
}
