package pe.utp.tienda;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pe.utp.tienda.app.ContextoTienda;
import pe.utp.tienda.behavioral.command.*;
import pe.utp.tienda.behavioral.memento.BorradorPedido;
import pe.utp.tienda.behavioral.memento.HistorialBorrador;
import pe.utp.tienda.behavioral.observer.CambioEstadoPedido;
import pe.utp.tienda.behavioral.state.TransicionInvalidaException;
import pe.utp.tienda.controller.ResultadoVenta;
import pe.utp.tienda.controller.SolicitudVenta;
import pe.utp.tienda.creational.abstractfactory.PasarelaPagoFactory;
import pe.utp.tienda.creational.factory.ComprobanteFactory;
import pe.utp.tienda.model.*;
import pe.utp.tienda.structural.bridge.*;
import pe.utp.tienda.structural.proxy.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Evaluación PC3 – Patrones estructurales complementarios, de comportamiento y antipatrones")
public class PatronesPC3Test {

    private static final Cliente ANA = new Cliente("72345678", "Ana", "Torres");

    private static Pedido pedido(Producto p, int cantidad) {
        return new Pedido.Builder(ANA).agregarItem(p, cantidad).build();
    }

    @Nested
    @DisplayName("A. State: ciclo de vida del pedido")
    class StateTest {
        @Test
        @DisplayName("Recorre Pendiente -> Pagado -> En Preparacion -> Enviado -> Entregado")
        void flujoCompleto() {
            Pedido p = pedido(new Polera("P-1", "Polera", "M", 50, 10), 1);
            assertEquals("Pendiente", p.getEstado());
            p.pagar();      assertEquals("Pagado", p.getEstado());
            p.preparar();   assertEquals("En Preparacion", p.getEstado());
            p.enviar();     assertEquals("Enviado", p.getEstado());
            p.entregar();   assertEquals("Entregado", p.getEstado());
        }

        @Test
        @DisplayName("Rechaza transiciones invalidas con TransicionInvalidaException")
        void transicionesInvalidas() {
            Pedido p = pedido(new Polera("P-1", "Polera", "M", 50, 10), 1);
            assertThrows(TransicionInvalidaException.class, p::enviar, "No se envia un pedido sin pagar");
            p.pagar();
            p.preparar();
            p.enviar();
            assertThrows(TransicionInvalidaException.class, p::cancelar, "No se cancela un pedido ya enviado");
            p.entregar();
            assertThrows(TransicionInvalidaException.class, p::pagar, "Entregado es un estado final");
        }

        @Test
        @DisplayName("Cancelar es posible hasta antes del envio y Cancelado es final")
        void cancelar() {
            Pedido p = pedido(new Polera("P-1", "Polera", "M", 50, 10), 1);
            p.pagar();
            p.cancelar();
            assertEquals("Cancelado", p.getEstado());
            assertThrows(TransicionInvalidaException.class, p::pagar);
        }

        @Test
        @DisplayName("El clon (repetir pedido) siempre nace Pendiente")
        void clonNacePendiente() {
            Pedido p = pedido(new Polera("P-1", "Polera", "M", 50, 10), 1);
            p.pagar();
            assertEquals("Pendiente", p.clone().getEstado());
        }
    }

    @Nested
    @DisplayName("B. Observer: suscriptores notificados automaticamente")
    class ObserverTest {
        @Test
        @DisplayName("Una venta notifica a cliente, auditoria y dashboard")
        void unaVentaNotificaATodos() {
            ContextoTienda ctx = ContextoTienda.enMemoria();
            ctx.ventas().registrar(new SolicitudVenta("72345678", "Ana Torres", ctx.producto("POL-OVR-001"),
                    1, 0, 0, "YAPE", "BOLETA"));
            assertTrue(ctx.auditoria().getRegistros().stream().anyMatch(r -> r.contains("Pendiente -> Pagado")));
            assertEquals(1, ctx.dashboard().getPorEstado().get("Pagado"));
            assertTrue(ctx.notificaciones().getEnviados().stream().anyMatch(m -> m.contains("paso de 'Pendiente' a 'Pagado'")));
        }

        @Test
        @DisplayName("Desuscribir detiene las notificaciones")
        void desuscribir() {
            Pedido p = pedido(new Polera("P-1", "Polera", "M", 50, 10), 1);
            List<String> recibidos = new ArrayList<>();
            pe.utp.tienda.behavioral.observer.Observador<CambioEstadoPedido> o = e -> recibidos.add(e.estadoNuevo());
            p.suscribir(o);
            p.pagar();
            p.desuscribir(o);
            p.preparar();
            assertEquals(List.of("Pagado"), recibidos);
        }

        @Test
        @DisplayName("El clon no hereda los suscriptores del original")
        void clonSinObservadores() {
            Pedido p = pedido(new Polera("P-1", "Polera", "M", 50, 10), 1);
            p.suscribir(e -> { });
            assertEquals(1, p.cantidadObservadores());
            assertEquals(0, p.clone().cantidadObservadores());
        }

        @Test
        @DisplayName("Alerta de stock minimo llega a compras al vender")
        void alertaStock() {
            ContextoTienda ctx = ContextoTienda.enMemoria();
            Producto zap = ctx.producto("ZAP-URB-001");           // stock 10, minimo 5
            ctx.ventas().registrar(new SolicitudVenta("72345678", "Ana Torres", zap, 4, 0, 0, "TARJETA", "BOLETA"));
            assertTrue(ctx.compras().getSolicitudes().isEmpty(), "Stock 6: aun sobre el minimo");
            ctx.ventas().registrar(new SolicitudVenta("72345678", "Ana Torres", zap, 1, 0, 0, "TARJETA", "BOLETA"));
            assertEquals(1, ctx.compras().getSolicitudes().size(), "Stock 5: llega al minimo");
            assertTrue(ctx.compras().getSolicitudes().get(0).contains("ZAP-URB-001"));
        }
    }

    @Nested
    @DisplayName("C. Command: operaciones reversibles, encoladas y auditables")
    class CommandTest {
        @Test
        @DisplayName("Ejecutar, deshacer y rehacer cambian el stock y quedan en la bitacora")
        void deshacerRehacer() {
            ContextoTienda ctx = ContextoTienda.enMemoria();
            Producto p = ctx.producto("POL-OVR-001");             // 25
            InvocadorComandos inv = ctx.comandos();
            inv.ejecutar(new DescontarStockComando(ctx.inventario(), p, 5));
            assertEquals(20, p.getStock());
            assertTrue(inv.deshacer());
            assertEquals(25, p.getStock());
            assertTrue(inv.rehacer());
            assertEquals(20, p.getStock());
            assertEquals(3, inv.getBitacora().size());
            assertTrue(inv.getBitacora().get(1).startsWith("DESHECHO"));
        }

        @Test
        @DisplayName("Un comando fallido no entra al historial de deshacer pero si a la bitacora")
        void comandoFallido() {
            ContextoTienda ctx = ContextoTienda.enMemoria();
            Producto p = ctx.producto("ZAP-URB-001");             // 10
            InvocadorComandos inv = ctx.comandos();
            assertThrows(IllegalStateException.class, () -> inv.ejecutar(new DescontarStockComando(ctx.inventario(), p, 99)));
            assertEquals(0, inv.cantidadDeshacer());
            assertEquals(10, p.getStock());
            assertTrue(inv.getBitacora().get(0).startsWith("FALLIDO"));
        }

        @Test
        @DisplayName("El macro-comando es atomico: si uno falla se revierten los anteriores")
        void macroAtomico() {
            ContextoTienda ctx = ContextoTienda.enMemoria();
            Producto pol = ctx.producto("POL-OVR-001");           // 25
            Producto zap = ctx.producto("ZAP-URB-001");           // 10
            Comando lote = new ComandoCompuesto("Salida de almacen", List.of(
                    new DescontarStockComando(ctx.inventario(), pol, 5),
                    new DescontarStockComando(ctx.inventario(), zap, 99)));   // este falla
            assertThrows(IllegalStateException.class, () -> ctx.comandos().ejecutar(lote));
            assertEquals(25, pol.getStock(), "El primer descuento debio revertirse");
        }

        @Test
        @DisplayName("La cola de tareas difiere la ejecucion hasta procesarla")
        void colaDeTareas() {
            ContextoTienda ctx = ContextoTienda.enMemoria();
            Producto p = ctx.producto("PAN-CRG-001");             // 15
            ctx.comandos().encolar(new ReponerStockComando(ctx.inventario(), p, 10));
            ctx.comandos().encolar(new DescontarStockComando(ctx.inventario(), p, 3));
            assertEquals(15, p.getStock());
            assertEquals(2, ctx.comandos().pendientesEnCola());
            assertEquals(2, ctx.comandos().procesarCola());
            assertEquals(22, p.getStock());
        }
    }

    @Nested
    @DisplayName("D. Memento: deshacer/rehacer en la edicion del pedido")
    class MementoTest {
        @Test
        @DisplayName("Restaura el estado anterior del borrador sin romper el encapsulamiento")
        void deshacerYRehacer() {
            Producto pol = new Polera("P-1", "Polera", "M", 50, 10);
            Producto pan = new Pantalon("P-2", "Pantalon", "32", 80, 10);
            BorradorPedido b = new BorradorPedido(ANA);
            HistorialBorrador h = new HistorialBorrador();

            h.respaldar(b); b.agregarItem(pol, 2);
            h.respaldar(b); b.agregarItem(pan, 1);
            h.respaldar(b); b.descuento(15);
            assertEquals(2, b.cantidadItems());
            assertEquals(15.0, b.getDescuentoPct(), 0.001);

            assertTrue(h.deshacer(b));
            assertEquals(0.0, b.getDescuentoPct(), 0.001);
            assertTrue(h.deshacer(b));
            assertEquals(1, b.cantidadItems());
            assertTrue(h.rehacer(b));
            assertEquals(2, b.cantidadItems());
        }

        @Test
        @DisplayName("Una edicion nueva invalida el rehacer")
        void edicionNuevaInvalidaRehacer() {
            BorradorPedido b = new BorradorPedido(ANA);
            HistorialBorrador h = new HistorialBorrador();
            h.respaldar(b); b.envio(10);
            h.deshacer(b);
            h.respaldar(b); b.envio(20);
            assertFalse(h.rehacer(b));
        }

        @Test
        @DisplayName("El memento es opaco: no expone ningun metodo publico")
        void mementoOpaco() {
            int publicos = 0;
            for (Method m : BorradorPedido.Memento.class.getDeclaredMethods()) {
                if (Modifier.isPublic(m.getModifiers())) publicos++;
            }
            assertEquals(0, publicos);
            assertTrue(Modifier.isFinal(BorradorPedido.Memento.class.getModifiers()));
        }

        @Test
        @DisplayName("Confirmar el borrador construye el Pedido con el Builder")
        void confirmar() {
            BorradorPedido b = new BorradorPedido(ANA);
            assertThrows(IllegalStateException.class, b::confirmar, "Sin items no hay pedido");
            b.agregarItem(new Polera("P-1", "Polera", "M", 50, 10), 2);
            b.descuento(10);
            Pedido p = b.confirmar();
            assertEquals(100.0, p.getSubtotal(), 0.001);
            assertEquals(10.0, p.getDescuentoPct(), 0.001);
        }
    }

    @Nested
    @DisplayName("E. Proxy: proteccion, auditoria y cache")
    class ProxyTest {
        @Test
        @DisplayName("Proxy de seguridad: el ADMIN ve ventas, el VENDEDOR solo inventario, y todo queda auditado")
        void seguridad() {
            ContextoTienda ctx = ContextoTienda.enMemoria();
            ServicioReportesProxy admin = ctx.reportesPara(new Usuario("Rosa", Rol.ADMIN));
            ServicioReportesProxy vendedor = ctx.reportesPara(new Usuario("Luis", Rol.VENDEDOR));

            assertNotNull(admin.generar("VENTAS", "CSV"));
            assertNotNull(vendedor.generar("INVENTARIO", "HTML"));
            assertThrows(AccesoDenegadoException.class, () -> vendedor.generar("VENTAS", "CSV"));

            assertTrue(admin.getAuditoria().get(0).startsWith("PERMITIDO"));
            assertEquals(2, vendedor.getAuditoria().size());
            assertTrue(vendedor.getAuditoria().get(1).startsWith("DENEGADO"));
        }

        @Test
        @DisplayName("Proxy de cache: las consultas repetidas no vuelven a la base de datos")
        void cache() {
            ContextoTienda ctx = ContextoTienda.enMemoria();
            for (int i = 0; i < 3; i++) assertTrue(ctx.catalogo().buscarPorSku("POL-OVR-001").isPresent());
            assertEquals(1, ctx.catalogoReal().getAccesos());
            assertEquals(1, ctx.catalogo().getFallos());
            assertEquals(2, ctx.catalogo().getAciertos());
        }

        @Test
        @DisplayName("Un SKU inexistente no se guarda en cache")
        void skuInexistente() {
            ContextoTienda ctx = ContextoTienda.enMemoria();
            assertFalse(ctx.catalogo().buscarPorSku("NO-EXISTE").isPresent());
            assertFalse(ctx.catalogo().buscarPorSku("NO-EXISTE").isPresent());
            assertEquals(2, ctx.catalogoReal().getAccesos());
        }
    }

    @Nested
    @DisplayName("F. Bridge: reporte x formato varian de forma independiente")
    class BridgeTest {
        private final List<Producto> catalogo = List.of(
                new Polera("P-1", "Polera", "M", 59.0, 25), new Zapatilla("Z-1", "Zapatilla", "40", 148.0, 10));

        @Test
        @DisplayName("Un mismo reporte se exporta en HTML, CSV y JSON")
        void mismoReporteTresFormatos() {
            Reporte r = new ReporteInventario(catalogo, new ExportadorCsv());
            assertTrue(r.exportar().startsWith("SKU,Tipo,Talla,Precio,Stock,Minimo"));
            r.setExportador(new ExportadorHtml());
            assertTrue(r.exportar().contains("<table"));
            r.setExportador(new ExportadorJson());
            assertTrue(r.exportar().startsWith("{\"reporte\":\"Reporte de Inventario\""));
        }

        @Test
        @DisplayName("Dos reportes x tres exportadores = seis combinaciones sin clases nuevas")
        void seisCombinaciones() {
            ContextoTienda ctx = ContextoTienda.enMemoria();
            ServicioReportesProxy admin = ctx.reportesPara(new Usuario("Rosa", Rol.ADMIN));
            for (String tipo : List.of("VENTAS", "INVENTARIO")) {
                for (String formato : List.of("HTML", "CSV", "JSON")) {
                    assertFalse(admin.generar(tipo, formato).isBlank(), tipo + " en " + formato);
                }
            }
        }

        @Test
        @DisplayName("Los exportadores escapan caracteres especiales")
        void escape() {
            Pedido p = new Pedido.Builder(new Cliente("72345678", "A<b>", "Perez, Jr")).agregarItem(catalogo.get(0), 1).build();
            assertTrue(new ReporteVentas(List.of(p), new ExportadorHtml()).exportar().contains("A&lt;b&gt;"));
            assertTrue(new ReporteVentas(List.of(p), new ExportadorCsv()).exportar().contains("\"A<b> Perez, Jr\""));
        }

        @Test
        @DisplayName("Un formato desconocido se rechaza")
        void formatoDesconocido() {
            assertThrows(IllegalArgumentException.class, () -> ExportadorReporte.para("PDF"));
        }
    }

    @Nested
    @DisplayName("G. Integracion extremo a extremo (capas + patrones PC2 y PC3)")
    class IntegracionTest {
        @Test
        @DisplayName("Venta persistida, pedido recorre su ciclo y los reportes lo reflejan")
        void flujoCompleto() {
            ContextoTienda ctx = ContextoTienda.enMemoria();
            Producto pol = ctx.producto("POL-OVR-001");
            ResultadoVenta r = ctx.ventas().registrar(new SolicitudVenta("72345678", "Ana Torres", pol, 2, 10, 12, "YAPE", "BOLETA"));

            assertEquals("Pagado", r.pedido().getEstado());
            assertTrue(r.pedido().getId() > 0, "El pedido quedo persistido");
            assertEquals(23, pol.getStock());
            assertTrue(r.detalle().contains("BOLETA"));

            r.pedido().preparar(); r.pedido().enviar(); r.pedido().entregar();
            assertEquals("Entregado", ctx.pedidos().listar().get(0).getEstado());
            assertEquals(1, ctx.dashboard().getPorEstado().get("Entregado"));

            String csv = ctx.reportesPara(new Usuario("Rosa", Rol.ADMIN)).generar("VENTAS", "CSV");
            assertTrue(csv.contains("Ana Torres") && csv.contains("Entregado"));
        }

        @Test
        @DisplayName("Un pago rechazado deja el pedido Cancelado y sin descontar stock")
        void pagoRechazado() {
            ContextoTienda ctx = ContextoTienda.enMemoria();
            Producto zap = ctx.producto("ZAP-URB-001");
            Pedido[] creado = new Pedido[1];
            assertThrows(IllegalStateException.class, () ->
                    creado[0] = ctx.ventas().registrar(new SolicitudVenta("72345678", "Ana Torres", zap, 5, 0, 0, "YAPE", "BOLETA")).pedido());
            assertEquals(10, zap.getStock());
            assertEquals("Cancelado", ctx.pedidos().listar().get(0).getEstado());
        }
    }

    @Nested
    @DisplayName("H. Refactorizacion de antipatrones (el comportamiento no cambio)")
    class AntipatronesTest {
        @Test
        @DisplayName("Copy-Paste: los pagos comparten la regla del limite y siguen distinguiendose por pasarela")
        void pagosConLimite() {
            assertFalse(PasarelaPagoFactory.para("YAPE").crearMetodoPago().cobrar(500.01));
            assertTrue(PasarelaPagoFactory.para("YAPE").crearMetodoPago().cobrar(500));
            assertFalse(PasarelaPagoFactory.para("PLIN").crearMetodoPago().cobrar(0));
            assertTrue(PasarelaPagoFactory.para("TARJETA").crearMetodoPago().cobrar(5000));
            assertEquals("Voucher POS tarjeta por S/ 10.00", PasarelaPagoFactory.para("TARJETA").crearRecibo().generar(10));
        }

        @Test
        @DisplayName("Copy-Paste: el correlativo de cada serie sigue siendo compartido y creciente")
        void correlativos() {
            Pedido p = pedido(new Polera("P-1", "Polera", "M", 50, 10), 1);
            String a = ComprobanteFactory.de("BOLETA").emitir(p, 10);
            String b = ComprobanteFactory.de("BOLETA").emitir(p, 10);
            int na = Integer.parseInt(a.substring(a.indexOf("B001-") + 5, a.indexOf("B001-") + 11));
            int nb = Integer.parseInt(b.substring(b.indexOf("B001-") + 5, b.indexOf("B001-") + 11));
            assertEquals(na + 1, nb);
            assertTrue(ComprobanteFactory.de("FACTURA").emitir(p, 10).contains("(DNI 72345678)"));
        }

        @Test
        @DisplayName("God Object: la GUI delega la venta en el controlador (sin armar Builder ni Cliente)")
        void controlador() {
            ContextoTienda ctx = ContextoTienda.enMemoria();
            ResultadoVenta r = ctx.ventas().registrar(new SolicitudVenta(" 72345678 ", "Ana", ctx.producto("PAN-CRG-001"), 1, 0, 0, "PLIN", "FACTURA"));
            assertEquals("Ana ", r.pedido().getCliente().getNombreCompleto());
            assertEquals("72345678", r.pedido().getCliente().getDni());
        }
    }
}
