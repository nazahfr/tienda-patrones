package pe.utp.tienda.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pe.utp.tienda.behavioral.observer.CambioEstadoPedido;
import pe.utp.tienda.behavioral.observer.Sujeto;
import pe.utp.tienda.behavioral.state.EstadoPedido;
import pe.utp.tienda.behavioral.state.EstadoPendiente;

/**
 * Producto complejo del Builder y objeto clonable (Prototype con copia profunda).
 * PC3: es el CONTEXTO del State (su ciclo de vida) y el SUJETO del Observer (publica cada cambio de estado).
 */
public class Pedido extends Sujeto<CambioEstadoPedido> implements Cloneable {
    private int id;                                   // 0 = aun no persistido
    private final Cliente cliente;
    private List<ItemPedido> items;
    private final double descuentoPct;
    private final double costoEnvio;
    private final String notas;
    private EstadoPedido estado = new EstadoPendiente();

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
        Pedido copia = (Pedido) cloneBase();
        List<ItemPedido> nuevos = new ArrayList<>();
        for (ItemPedido i : items) nuevos.add(new ItemPedido(i.getProducto(), i.getCantidad()));
        copia.items = Collections.unmodifiableList(nuevos);
        copia.estado = new EstadoPendiente();
        copia.id = 0;
        copia.reiniciarObservadores();                // la copia no hereda los suscriptores del original
        return copia;
    }

    private Object cloneBase() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    // ================= State (contexto): delega en el estado actual =================
    public void pagar()     { estado.pagar(this); }
    public void preparar()  { estado.preparar(this); }
    public void enviar()    { estado.enviar(this); }
    public void entregar()  { estado.entregar(this); }
    public void cancelar()  { estado.cancelar(this); }

    /** Lo invocan los estados concretos al hacer una transicion. Publica el cambio a los observadores. */
    public void cambiarEstado(EstadoPedido nuevo) {
        String anterior = estado.nombre();
        this.estado = nuevo;
        notificar(new CambioEstadoPedido(this, anterior, nuevo.nombre()));
    }

    /** Solo para reconstruir un pedido leido de la base de datos: no es una transicion, no notifica. */
    public void restaurarEstado(EstadoPedido guardado) { this.estado = guardado; }

    public Cliente getCliente() { return cliente; }
    public List<ItemPedido> getItems() { return items; }
    public double getDescuentoPct() { return descuentoPct; }
    public double getCostoEnvio() { return costoEnvio; }
    public String getNotas() { return notas; }
    public String getEstado() { return estado.nombre(); }
    public EstadoPedido getEstadoActual() { return estado; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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
