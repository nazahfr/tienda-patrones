package pe.utp.tienda.model;

public class Pantalon extends Producto {
    public Pantalon(String sku, String nombre, String talla, double precio, int stock) {
        super(sku, nombre, talla, precio, stock);
    }

    @Override
    public String tipo() { return "Pantalon"; }
}
