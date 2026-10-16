package pe.utp.tienda.model;

public class Polera extends Producto {
    public Polera(String sku, String nombre, String talla, double precio, int stock) {
        super(sku, nombre, talla, precio, stock);
    }

    @Override
    public String tipo() { return "Polera"; }
}
