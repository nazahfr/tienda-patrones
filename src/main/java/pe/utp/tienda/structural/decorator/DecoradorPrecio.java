package pe.utp.tienda.structural.decorator;

/** Decorator base: envuelve otro CalculoPrecio y delega en el. */
public abstract class DecoradorPrecio implements CalculoPrecio {
    protected final CalculoPrecio envuelto;

    protected DecoradorPrecio(CalculoPrecio envuelto) { this.envuelto = envuelto; }

    @Override public double calcular() { return envuelto.calcular(); }
    @Override public String detalle() { return envuelto.detalle(); }
}
