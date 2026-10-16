package pe.utp.tienda.model;

public class ItemPedido {
    private final Producto producto;
    private final int cantidad;

    public ItemPedido(Producto producto, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return producto.getPrecio() * cantidad; }
}
