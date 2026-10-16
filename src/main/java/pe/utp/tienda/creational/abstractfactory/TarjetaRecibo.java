package pe.utp.tienda.creational.abstractfactory;

public class TarjetaRecibo implements ReciboPago {
    @Override
    public String generar(double monto) {
        return String.format("Voucher POS tarjeta por S/ %.2f", monto);
    }
}
