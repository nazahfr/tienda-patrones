package pe.utp.tienda.structural.composite;

import java.util.ArrayList;
import java.util.List;

/** Composite: una categoria contiene productos y/o subcategorias y se trata igual que un producto. */
public class Categoria implements ComponenteCatalogo {
    private final String nombre;
    private final List<ComponenteCatalogo> hijos = new ArrayList<>();

    public Categoria(String nombre) { this.nombre = nombre; }

    public void agregar(ComponenteCatalogo c) { hijos.add(c); }
    public void quitar(ComponenteCatalogo c) { hijos.remove(c); }

    @Override public String getNombre() { return nombre; }

    @Override public double getPrecioTotal() {
        return hijos.stream().mapToDouble(ComponenteCatalogo::getPrecioTotal).sum();
    }

    @Override public int getStockTotal() {
        return hijos.stream().mapToInt(ComponenteCatalogo::getStockTotal).sum();
    }

    @Override public void mostrar(String sangria) {
        System.out.println(sangria + "+ " + nombre + " [stock total: " + getStockTotal() + "]");
        hijos.forEach(h -> h.mostrar(sangria + "    "));
    }
}
