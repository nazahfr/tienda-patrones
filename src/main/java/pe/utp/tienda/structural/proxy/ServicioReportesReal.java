package pe.utp.tienda.structural.proxy;

import pe.utp.tienda.persistence.PedidoDAO;
import pe.utp.tienda.persistence.ProductoDAO;
import pe.utp.tienda.structural.bridge.ExportadorReporte;
import pe.utp.tienda.structural.bridge.Reporte;
import pe.utp.tienda.structural.bridge.ReporteInventario;
import pe.utp.tienda.structural.bridge.ReporteVentas;

/** RealSubject (GoF): arma el reporte pedido (Bridge: reporte x exportador) leyendo la base de datos. No sabe nada de permisos. */
public class ServicioReportesReal implements ServicioReportes {
    private final PedidoDAO pedidos;
    private final ProductoDAO productos;

    public ServicioReportesReal(PedidoDAO pedidos, ProductoDAO productos) {
        this.pedidos = pedidos;
        this.productos = productos;
    }

    @Override
    public String generar(String tipo, String formato) {
        ExportadorReporte exportador = ExportadorReporte.para(formato);
        Reporte reporte = switch (tipo.toUpperCase()) {
            case "VENTAS" -> new ReporteVentas(pedidos.listar(), exportador);
            case "INVENTARIO" -> new ReporteInventario(productos.listar(), exportador);
            default -> throw new IllegalArgumentException("Tipo de reporte no soportado: " + tipo);
        };
        return reporte.exportar();
    }
}
