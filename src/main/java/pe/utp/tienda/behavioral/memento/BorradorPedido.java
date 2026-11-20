package pe.utp.tienda.behavioral.memento;

import java.util.ArrayList;
import java.util.List;
import pe.utp.tienda.model.Cliente;
import pe.utp.tienda.model.ItemPedido;
import pe.utp.tienda.model.Pedido;
import pe.utp.tienda.model.Producto;

/**
 * Originator (GoF): el pedido en edicion (el carrito). Crea un Memento con su estado interno
 * y puede restaurarse desde el. Al confirmar, construye el Pedido definitivo con el Builder.
 */
public class BorradorPedido {
    private final Cliente cliente;
    private List<ItemPedido> items = new ArrayList<>();
    private double descuentoPct;
    private double costoEnvio;
    private String notas = "";

    public BorradorPedido(Cliente cliente) { this.cliente = cliente; }

    public void agregarItem(Producto producto, int cantidad) { items.add(new ItemPedido(producto, cantidad)); }

    public void quitarItem(String sku) {
        if (!items.removeIf(i -> i.getProducto().getSku().equals(sku))) {
            throw new IllegalArgumentException("El borrador no contiene el SKU " + sku);
        }
    }

    public void descuento(double porcentaje) { this.descuentoPct = porcentaje; }
    public void envio(double costo) { this.costoEnvio = costo; }
    public void notas(String notas) { this.notas = notas; }

    public Pedido confirmar() {
        Pedido.Builder b = new Pedido.Builder(cliente).descuento(descuentoPct).envio(costoEnvio).notas(notas);
        for (ItemPedido i : items) b.agregarItem(i.getProducto(), i.getCantidad());
        return b.build();
    }

    public String resumen() {
        StringBuilder sb = new StringBuilder();
        for (ItemPedido i : items) {
            sb.append(i.getCantidad()).append(" x ").append(i.getProducto().getSku()).append("  ");
        }
        return (items.isEmpty() ? "(vacio)" : sb.toString().trim())
                + String.format(" | dto %.0f%% | envio S/ %.2f", descuentoPct, costoEnvio);
    }

    public int cantidadItems() { return items.size(); }
    public double getDescuentoPct() { return descuentoPct; }
    public double getCostoEnvio() { return costoEnvio; }

    // ---- Memento ----
    public Memento guardar() { return new Memento(items, descuentoPct, costoEnvio, notas); }

    public void restaurar(Memento m) {
        this.items = new ArrayList<>(m.items);
        this.descuentoPct = m.descuentoPct;
        this.costoEnvio = m.costoEnvio;
        this.notas = m.notas;
    }

    /**
     * Memento (GoF): foto inmutable del estado. Sus campos son privados: solo el Originator (la clase que lo
     * contiene) puede leerlos; el Caretaker solo lo guarda y lo devuelve, sin poder inspeccionarlo ni modificarlo.
     */
    public static final class Memento {
        private final List<ItemPedido> items;
        private final double descuentoPct;
        private final double costoEnvio;
        private final String notas;

        private Memento(List<ItemPedido> items, double descuentoPct, double costoEnvio, String notas) {
            this.items = new ArrayList<>(items);   // ItemPedido es inmutable: copiar la lista basta
            this.descuentoPct = descuentoPct;
            this.costoEnvio = costoEnvio;
            this.notas = notas;
        }
    }
}
