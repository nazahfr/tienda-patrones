package pe.utp.tienda.service;

import pe.utp.tienda.behavioral.observer.AlertaStock;
import pe.utp.tienda.behavioral.observer.Sujeto;
import pe.utp.tienda.model.ItemPedido;
import pe.utp.tienda.model.Pedido;
import pe.utp.tienda.model.Producto;
import pe.utp.tienda.persistence.ProductoDAO;

/**
 * Logica de inventario. PC3: es el SUJETO del Observer para el stock minimo
 * y la unica clase que modifica el stock (la usan la Facade y los Command).
 */
public class InventarioService extends Sujeto<AlertaStock> {
    private final ProductoDAO productos;   // puede ser null: sin persistencia (pruebas unitarias sueltas)

    public InventarioService() { this(null); }

    public InventarioService(ProductoDAO productos) { this.productos = productos; }

    public void verificarStock(Pedido pedido) {
        for (ItemPedido item : pedido.getItems()) {
            if (item.getCantidad() > item.getProducto().getStock()) {
                throw new IllegalStateException("Stock insuficiente para " + item.getProducto().getSku());
            }
        }
    }

    public void descontarStock(Pedido pedido) {
        verificarStock(pedido);
        for (ItemPedido item : pedido.getItems()) {
            retirar(item.getProducto(), item.getCantidad());
        }
    }

    /** Descuenta stock, lo persiste y avisa a compras si la prenda llego al minimo. */
    public void retirar(Producto producto, int cantidad) {
        producto.descontarStock(cantidad);
        persistir(producto);
        if (producto.tieneStockBajo()) {
            notificar(new AlertaStock(producto, producto.getStock(), producto.getStockMinimo()));
        }
    }

    /** Ingreso de mercaderia (y operacion inversa de retirar). */
    public void reponer(Producto producto, int cantidad) {
        producto.reponerStock(cantidad);
        persistir(producto);
    }

    private void persistir(Producto producto) {
        if (productos != null) productos.actualizarStock(producto.getSku(), producto.getStock());
    }
}
