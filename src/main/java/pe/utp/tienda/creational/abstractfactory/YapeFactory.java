package pe.utp.tienda.creational.abstractfactory;

public class YapeFactory implements PasarelaPagoFactory {
    @Override public IMetodoPago crearMetodoPago() { return new YapePago(); }
    @Override public ReciboPago crearRecibo() { return new YapeRecibo(); }
}
