package pe.utp.tienda.creational.abstractfactory;

public class PlinFactory implements PasarelaPagoFactory {
    @Override public IMetodoPago crearMetodoPago() { return new PlinPago(); }
    @Override public ReciboPago crearRecibo() { return new PlinRecibo(); }
}
