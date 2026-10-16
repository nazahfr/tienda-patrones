package pe.utp.tienda.creational.factory;

public class FacturaFactory extends ComprobanteFactory {
    @Override
    protected Comprobante crearComprobante() { return new FacturaElectronica(); }
}
