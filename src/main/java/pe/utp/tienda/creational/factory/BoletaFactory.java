package pe.utp.tienda.creational.factory;

public class BoletaFactory extends ComprobanteFactory {
    @Override
    protected Comprobante crearComprobante() { return new Boleta(); }
}
