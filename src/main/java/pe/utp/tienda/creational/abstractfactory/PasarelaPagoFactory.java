package pe.utp.tienda.creational.abstractfactory;

/** Abstract Factory: cada pasarela crea SU familia coherente (metodo de pago + recibo). */
public interface PasarelaPagoFactory {
    IMetodoPago crearMetodoPago();
    ReciboPago crearRecibo();

    static PasarelaPagoFactory para(String metodo) {
        return switch (metodo.toUpperCase()) {
            case "YAPE" -> new YapeFactory();
            case "PLIN" -> new PlinFactory();
            case "TARJETA" -> new TarjetaFactory();
            default -> throw new IllegalArgumentException("Metodo de pago no soportado: " + metodo);
        };
    }
}
