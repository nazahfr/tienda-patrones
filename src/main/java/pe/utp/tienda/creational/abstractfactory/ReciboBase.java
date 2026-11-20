package pe.utp.tienda.creational.abstractfactory;

/** Base comun de los recibos: solo cambia la plantilla del texto, el formateo del monto es uno solo. */
public abstract class ReciboBase implements ReciboPago {
    private final String plantilla;

    protected ReciboBase(String plantilla) { this.plantilla = plantilla; }

    @Override public String generar(double monto) { return String.format(plantilla, monto); }
}
