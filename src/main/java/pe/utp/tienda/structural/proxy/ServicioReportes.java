package pe.utp.tienda.structural.proxy;

/** Subject (GoF): interfaz comun del servicio real y de su proxy. tipo = VENTAS | INVENTARIO, formato = HTML | CSV | JSON. */
public interface ServicioReportes {
    String generar(String tipo, String formato);
}
