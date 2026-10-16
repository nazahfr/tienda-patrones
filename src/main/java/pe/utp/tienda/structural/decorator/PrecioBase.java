package pe.utp.tienda.structural.decorator;

/** ConcreteComponent: el subtotal del pedido sin ajustes. */
public class PrecioBase implements CalculoPrecio {
    private final double monto;

    public PrecioBase(double monto) { this.monto = monto; }

    @Override public double calcular() { return monto; }
    @Override public String detalle() { return String.format("Subtotal S/ %.2f", monto); }
}
