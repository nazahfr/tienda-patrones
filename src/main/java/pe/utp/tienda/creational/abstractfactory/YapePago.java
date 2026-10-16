package pe.utp.tienda.creational.abstractfactory;

public class YapePago implements IMetodoPago {
    @Override public String nombre() { return "Yape"; }

    @Override
    public boolean cobrar(double monto) {
        return monto > 0 && monto <= 500; // simulacion: limite de la pasarela
    }
}
