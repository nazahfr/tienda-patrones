package pe.utp.tienda.structural.decorator;

public class ConDescuento extends DecoradorPrecio {
    private final double porcentaje;

    public ConDescuento(CalculoPrecio envuelto, double porcentaje) {
        super(envuelto);
        this.porcentaje = porcentaje;
    }

    @Override public double calcular() { return super.calcular() * (1 - porcentaje / 100.0); }
    @Override public String detalle() { return super.detalle() + String.format(" -> Descuento %.0f%%", porcentaje); }
}
