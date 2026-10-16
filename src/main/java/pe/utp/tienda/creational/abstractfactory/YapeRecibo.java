package pe.utp.tienda.creational.abstractfactory;

public class YapeRecibo implements ReciboPago {
    @Override
    public String generar(double monto) {
        return String.format("Operacion Yape aprobada por S/ %.2f", monto);
    }
}
