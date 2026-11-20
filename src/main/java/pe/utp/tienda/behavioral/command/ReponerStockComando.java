package pe.utp.tienda.behavioral.command;

import pe.utp.tienda.model.Producto;
import pe.utp.tienda.service.InventarioService;

/** Comando concreto: ingreso de mercaderia. Se deshace retirando lo repuesto. */
public class ReponerStockComando implements Comando {
    private final InventarioService inventario;
    private final Producto producto;
    private final int cantidad;

    public ReponerStockComando(InventarioService inventario, Producto producto, int cantidad) {
        this.inventario = inventario;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    @Override public void ejecutar() { inventario.reponer(producto, cantidad); }
    @Override public void deshacer() { inventario.retirar(producto, cantidad); }
    @Override public String descripcion() { return "Reponer " + cantidad + " x " + producto.getSku(); }
}
