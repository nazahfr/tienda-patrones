package pe.utp.tienda.creational.abstractfactory;

/** Base comun de los pagos simulados: la regla "monto positivo y dentro del limite" se escribe una sola vez. */
public abstract class PagoConLimite implements IMetodoPago {
    private final String nombre;
    private final double limite;

    protected PagoConLimite(String nombre, double limite) {
        this.nombre = nombre;
        this.limite = limite;
    }

    @Override public String nombre() { return nombre; }

    @Override
    public boolean cobrar(double monto) {
        return monto > 0 && monto <= limite; // simulacion: limite de la pasarela
    }
}
