package pe.utp.tienda.creational.abstractfactory;

/** Producto abstracto A de la familia (OCP / DIP: el resto del sistema solo conoce esta interfaz). */
public interface IMetodoPago {
    String nombre();
    boolean cobrar(double monto);
}
