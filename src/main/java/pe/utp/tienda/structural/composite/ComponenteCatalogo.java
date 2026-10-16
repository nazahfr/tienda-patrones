package pe.utp.tienda.structural.composite;

/** Component: interfaz comun para productos (hojas) y categorias (compuestos). */
public interface ComponenteCatalogo {
    String getNombre();
    double getPrecioTotal();   // valor del inventario
    int getStockTotal();
    void mostrar(String sangria);
}
