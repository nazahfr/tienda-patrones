package pe.utp.tienda.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Producto complejo del Builder y objeto clonable (Prototype con copia profunda). */
public class Pedido implements Cloneable {
    private final Cliente cliente;
    private List<ItemPedido> items;
    private final double descuentoPct;
    private final double costoEnvio;
    private final String notas;
    private String estado = "Pendiente";

    private Pedido(Builder b) {
        this.cliente = b.cliente;
        this.items = b.items;
        this.descuentoPct = b.descuentoPct;
        this.costoEnvio = b.costoEnvio;
        this.notas = b.notas;
    }

    public double getSubtotal() {
        return items.stream().mapToDouble(ItemPedido::getSubtotal).sum();
    }

    /** Prototype: "repetir pedido" copia la lista de items (no la comparte con el original). */
    @Override
    public Pedido clone() {
        try {
            Pedido copia = (Pedido) super.clone();
            List<ItemPedido> nuevos = new ArrayList<>();
            for (ItemPedido i : items) nuevos.add(new ItemPedido(i.getProducto(), i.getCantidad()));
            copia.items = Collections.unmodifiableList(nuevos);
            copia.estado = "Pendiente";
            return copia;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public Cliente getCliente() { return cliente; }
    public List<ItemPedido> getItems() { return items; }
    public double getDescuentoPct() { return descuentoPct; }
    public double getCostoEnvio() { return costoEnvio; }
    public String getNotas() { return notas; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // ================= Builder =================
    public static class Builder {
        private final Cliente cliente;                       // obligatorio
        private final List<ItemPedido> items = new ArrayList<>();
        private double descuentoPct = 0;                     // opcionales
        private double costoEnvio = 0;
        private String notas = "";

        public Builder(Cliente cliente) { this.cliente = cliente; }

        public Builder agregarItem(Producto producto, int cantidad) {
            items.add(new ItemPedido(producto, cantidad));
            return this;
        }
        public Builder descuento(double porcentaje) { this.descuentoPct = porcentaje; return this; }
        public Builder envio(double costo) { this.costoEnvio = costo; return this; }
        public Builder notas(String notas) { this.notas = notas; return this; }

        public Pedido build() {
            if (cliente == null) throw new IllegalStateException("El pedido requiere un cliente");
            if (items.isEmpty()) throw new IllegalStateException("El pedido requiere al menos un item");
            if (descuentoPct < 0 || descuentoPct > 100) throw new IllegalStateException("Descuento invalido");
            return new Pedido(this);
        }
    }
}
