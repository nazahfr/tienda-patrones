package pe.utp.tienda.model;

public class Zapatilla extends Producto {
    public Zapatilla(String sku, String nombre, String talla, double precio, int stock) {
        super(sku, nombre, talla, precio, stock);
    }

    @Override
    public String tipo() { return "Zapatilla"; }
}
