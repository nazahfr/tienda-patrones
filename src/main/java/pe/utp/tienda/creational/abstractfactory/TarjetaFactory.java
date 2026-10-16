package pe.utp.tienda.creational.abstractfactory;

public class TarjetaFactory implements PasarelaPagoFactory {
    @Override public IMetodoPago crearMetodoPago() { return new TarjetaPago(); }
    @Override public ReciboPago crearRecibo() { return new TarjetaRecibo(); }
}
