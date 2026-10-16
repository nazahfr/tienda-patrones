package pe.utp.tienda.service;

import pe.utp.tienda.model.ItemPedido;
import pe.utp.tienda.model.Pedido;

public class InventarioService {
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
            item.getProducto().descontarStock(item.getCantidad());
        }
    }
}
