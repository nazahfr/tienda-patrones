package pe.utp.tienda.behavioral.state;

import pe.utp.tienda.model.Pedido;

/** Comportamiento por defecto: toda accion es invalida. Cada estado concreto sobreescribe solo lo que permite. */
public abstract class EstadoBase implements EstadoPedido {
    private void rechazar(String accion) { throw new TransicionInvalidaException(accion, nombre()); }

    @Override public void pagar(Pedido pedido)     { rechazar("pagar"); }
    @Override public void preparar(Pedido pedido)  { rechazar("preparar"); }
    @Override public void enviar(Pedido pedido)    { rechazar("enviar"); }
    @Override public void entregar(Pedido pedido)  { rechazar("entregar"); }
    @Override public void cancelar(Pedido pedido)  { rechazar("cancelar"); }
}
