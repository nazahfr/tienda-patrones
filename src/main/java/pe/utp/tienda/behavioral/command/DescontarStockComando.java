package pe.utp.tienda.behavioral.command;

import pe.utp.tienda.model.Producto;
import pe.utp.tienda.service.InventarioService;

/** Comando concreto: retirar unidades del inventario (receptor: InventarioService). Se deshace reponiendo. */
public class DescontarStockComando implements Comando {
    private final InventarioService inventario;
    private final Producto producto;
    private final int cantidad;

    public DescontarStockComando(InventarioService inventario, Producto producto, int cantidad) {
        this.inventario = inventario;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    @Override public void ejecutar() { inventario.retirar(producto, cantidad); }
    @Override public void deshacer() { inventario.reponer(producto, cantidad); }
    @Override public String descripcion() { return "Retirar " + cantidad + " x " + producto.getSku(); }
}
