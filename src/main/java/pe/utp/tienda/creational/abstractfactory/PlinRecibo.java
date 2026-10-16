package pe.utp.tienda.creational.abstractfactory;

public class PlinRecibo implements ReciboPago {
    @Override
    public String generar(double monto) {
        return String.format("Operacion Plin aprobada por S/ %.2f", monto);
    }
}
