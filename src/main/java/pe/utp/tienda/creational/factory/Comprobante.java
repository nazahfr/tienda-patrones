package pe.utp.tienda.creational.factory;

import pe.utp.tienda.model.Pedido;

/** Producto del Factory Method. */
public interface Comprobante {
    String generar(Pedido pedido, double total);
}
