package pe.utp.tienda.creational.abstractfactory;

public class PlinPago implements IMetodoPago {
    @Override public String nombre() { return "Plin"; }

    @Override
    public boolean cobrar(double monto) {
        return monto > 0 && monto <= 500; // simulacion: limite de la pasarela
    }
}
