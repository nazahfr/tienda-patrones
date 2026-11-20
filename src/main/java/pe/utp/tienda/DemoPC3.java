package pe.utp.tienda;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import pe.utp.tienda.app.ContextoTienda;
import pe.utp.tienda.behavioral.command.*;
import pe.utp.tienda.behavioral.memento.BorradorPedido;
import pe.utp.tienda.behavioral.memento.HistorialBorrador;
import pe.utp.tienda.behavioral.state.TransicionInvalidaException;
import pe.utp.tienda.controller.ResultadoVenta;
import pe.utp.tienda.controller.SolicitudVenta;
import pe.utp.tienda.model.*;
import pe.utp.tienda.structural.bridge.*;
import pe.utp.tienda.structural.proxy.AccesoDenegadoException;
import pe.utp.tienda.structural.proxy.ServicioReportesProxy;

/** Recorrido en consola de los patrones de la PC3 (State, Observer, Command, Memento, Proxy y Bridge) sobre el sistema integrado. */
public class DemoPC3 {
    public static void main(String[] args) {
        for (Handler h : Logger.getLogger("").getHandlers()) h.setLevel(Level.WARNING); // la bandeja de notificaciones ya se imprime abajo

        ContextoTienda ctx = ContextoTienda.enMemoria();
        Producto polera = ctx.producto("POL-OVR-001");
        Producto zapatilla = ctx.producto("ZAP-URB-001");

        System.out.println("=== 1. STATE + OBSERVER: ciclo de vida del pedido ===");
        ResultadoVenta venta = ctx.ventas().registrar(
                new SolicitudVenta("72345678", "Ana Torres", polera, 2, 10, 12, "YAPE", "BOLETA"));
        Pedido pedido = venta.pedido();
        System.out.println(venta.detalle());
        System.out.println("Estado tras el cobro : " + pedido.getEstado() + " (pedido #" + pedido.getId() + " guardado)");
        pedido.preparar();
        pedido.enviar();
        try {
            pedido.cancelar();
        } catch (TransicionInvalidaException e) {
            System.out.println("Transicion rechazada : " + e.getMessage());
        }
        pedido.entregar();
        System.out.println("Estado final         : " + pedido.getEstado());
        System.out.println("-- Auditoria (Observer)");
        ctx.auditoria().getRegistros().forEach(r -> System.out.println("   " + r));
        System.out.println("-- Notificaciones al cliente (Observer)");
        ctx.notificaciones().getEnviados().forEach(m -> System.out.println("   " + m));
        System.out.println("-- Dashboard (Observer): " + ctx.dashboard().resumen());

        System.out.println("\n=== 2. OBSERVER: alerta de stock minimo a Compras ===");
        System.out.println("Zapatilla: stock " + zapatilla.getStock() + ", minimo " + zapatilla.getStockMinimo());
        ctx.ventas().registrar(new SolicitudVenta("72345678", "Ana Torres", zapatilla, 5, 0, 0, "TARJETA", "FACTURA"));
        System.out.println("Tras vender 5, stock " + zapatilla.getStock());
        ctx.compras().getSolicitudes().forEach(s -> System.out.println("   " + s));

        System.out.println("\n=== 3. COMMAND: inventario reversible, cola y bitacora ===");
        InvocadorComandos inv = ctx.comandos();
        System.out.println("Stock inicial polera: " + polera.getStock());
        inv.ejecutar(new ReponerStockComando(ctx.inventario(), polera, 30));
        System.out.println("Tras reponer 30     : " + polera.getStock());
        inv.deshacer();
        System.out.println("Tras deshacer       : " + polera.getStock());
        inv.rehacer();
        System.out.println("Tras rehacer        : " + polera.getStock());
        inv.encolar(new DescontarStockComando(ctx.inventario(), polera, 4));
        System.out.println("Encolado (sin ejecutar): " + polera.getStock() + " | pendientes: " + inv.pendientesEnCola());
        inv.procesarCola();
        System.out.println("Tras procesar la cola  : " + polera.getStock());
        try {
            inv.ejecutar(new DescontarStockComando(ctx.inventario(), zapatilla, 99));
        } catch (IllegalStateException e) {
            System.out.println("Comando fallido     : " + e.getMessage());
        }
        System.out.println("-- Bitacora de auditoria");
        inv.getBitacora().forEach(b -> System.out.println("   " + b));

        System.out.println("\n=== 4. MEMENTO: deshacer en la edicion del pedido ===");
        Cliente ana = new Cliente("72345678", "Ana", "Torres");
        BorradorPedido borrador = new BorradorPedido(ana);
        HistorialBorrador historial = new HistorialBorrador();
        historial.respaldar(borrador); borrador.agregarItem(polera, 2);
        historial.respaldar(borrador); borrador.agregarItem(ctx.producto("PAN-CRG-001"), 1);
        historial.respaldar(borrador); borrador.descuento(15);
        System.out.println("Borrador editado    : " + borrador.resumen());
        historial.deshacer(borrador);
        System.out.println("Deshacer (descuento): " + borrador.resumen());
        historial.deshacer(borrador);
        System.out.println("Deshacer (pantalon) : " + borrador.resumen());
        historial.rehacer(borrador);
        System.out.println("Rehacer             : " + borrador.resumen());
        System.out.println("Confirmado como Pedido, subtotal S/ " + borrador.confirmar().getSubtotal());

        System.out.println("\n=== 5. PROXY: seguridad y auditoria de reportes + cache de catalogo ===");
        ServicioReportesProxy vendedor = ctx.reportesPara(new Usuario("Luis", Rol.VENDEDOR));
        ServicioReportesProxy admin = ctx.reportesPara(new Usuario("Rosa", Rol.ADMIN));
        vendedor.generar("INVENTARIO", "CSV");
        try {
            vendedor.generar("VENTAS", "CSV");
        } catch (AccesoDenegadoException e) {
            System.out.println("Acceso denegado     : " + e.getMessage());
        }
        admin.generar("VENTAS", "JSON");
        vendedor.getAuditoria().forEach(a -> System.out.println("   " + a));
        admin.getAuditoria().forEach(a -> System.out.println("   " + a));
        for (int i = 0; i < 4; i++) ctx.catalogo().buscarPorSku("PAN-CRG-001");
        System.out.println("Cache de catalogo   : 4 consultas -> " + ctx.catalogoReal().getAccesos() + " acceso a la BD, "
                + ctx.catalogo().getAciertos() + " aciertos de cache");

        System.out.println("\n=== 6. BRIDGE: el mismo reporte en tres formatos ===");
        List<Producto> catalogo = ctx.productos().listar();
        Reporte inventario = new ReporteInventario(catalogo, new ExportadorCsv());
        for (ExportadorReporte e : List.of(new ExportadorCsv(), new ExportadorJson(), new ExportadorHtml())) {
            inventario.setExportador(e);
            System.out.println("-- " + e.nombre() + "\n" + inventario.exportar());
        }
    }
}
