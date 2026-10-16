package pe.utp.tienda.structural.decorator;

public class ConEnvio extends DecoradorPrecio {
    private final double costo;

    public ConEnvio(CalculoPrecio envuelto, double costo) {
        super(envuelto);
        this.costo = costo;
    }

    @Override public double calcular() { return super.calcular() + costo; }
    @Override public String detalle() { return super.detalle() + String.format(" -> Envio S/ %.2f", costo); }
}
