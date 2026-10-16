package pe.utp.tienda.model;

import pe.utp.tienda.structural.composite.ComponenteCatalogo;

/** Clase base de la jerarquia de prendas (LSP). Es la HOJA del Composite y el PROTOTIPO clonable. */
public abstract class Producto implements ComponenteCatalogo, Cloneable {
    private String sku;
    private final String nombre;
    private String talla;
    private final double precio;
    private int stock;

    protected Producto(String sku, String nombre, String talla, double precio, int stock) {
        this.sku = sku;
        this.nombre = nombre;
        this.talla = talla;
        this.precio = precio;
        this.stock = stock;
    }

    public abstract String tipo();

    // ---- Prototype ----
    @Override
    public Producto clone() {
        try {
            return (Producto) super.clone(); // campos primitivos/String inmutables: copia superficial suficiente
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /** Crea una variante (otra talla) a partir de este producto sin repetir todos sus datos. */
    public Producto clonarVariante(String nuevoSku, String nuevaTalla, int stockInicial) {
        Producto copia = this.clone();
        copia.sku = nuevoSku;
        copia.talla = nuevaTalla;
        copia.stock = stockInicial;
        return copia;
    }

    public void descontarStock(int cantidad) {
        if (cantidad > stock) {
            throw new IllegalStateException("Stock insuficiente para " + sku + " (disponible: " + stock + ")");
        }
        stock -= cantidad;
    }

    public String getSku() { return sku; }
    public String getTalla() { return talla; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }

    // ---- Composite (comportamiento de hoja) ----
    @Override public String getNombre() { return nombre + " (" + talla + ")"; }
    @Override public double getPrecioTotal() { return precio * stock; }
    @Override public int getStockTotal() { return stock; }
    @Override public void mostrar(String sangria) {
        System.out.println(sangria + "- " + getNombre() + " | SKU " + sku + " | S/ " + precio + " | stock " + stock);
    }
}
