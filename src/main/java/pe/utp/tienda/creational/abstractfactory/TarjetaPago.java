package pe.utp.tienda.creational.abstractfactory;

public class TarjetaPago implements IMetodoPago {
    @Override public String nombre() { return "Tarjeta"; }

    @Override
    public boolean cobrar(double monto) {
        return monto > 0 && monto <= 5000; // simulacion: limite de la pasarela
    }
}
