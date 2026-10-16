package pe.utp.tienda.creational.singleton;

/** Singleton (Initialization-on-demand holder): perezoso y seguro entre hilos sin sincronizacion explicita. */
public final class ConfiguracionApp {
    private final String nombreTienda = "Tienda de Ropa";
    private final String moneda = "PEN";
    private final double igv = 0.18;

    private ConfiguracionApp() { }

    private static class Holder {
        private static final ConfiguracionApp INSTANCIA = new ConfiguracionApp();
    }

    public static ConfiguracionApp getInstancia() { return Holder.INSTANCIA; }

    public String getNombreTienda() { return nombreTienda; }
    public String getMoneda() { return moneda; }
    public double getIgv() { return igv; }
}
