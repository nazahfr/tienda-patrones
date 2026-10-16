package pe.utp.tienda;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pe.utp.tienda.creational.abstractfactory.PasarelaPagoFactory;
import pe.utp.tienda.creational.factory.ComprobanteFactory;
import pe.utp.tienda.creational.singleton.ConfiguracionApp;
import pe.utp.tienda.creational.singleton.ConexionBD;
import pe.utp.tienda.model.*;
import pe.utp.tienda.service.InventarioService;
import pe.utp.tienda.service.NotificacionService;
import pe.utp.tienda.structural.adapter.ReniecAdapter;
import pe.utp.tienda.structural.adapter.ReniecApiExterna;
import pe.utp.tienda.structural.composite.Categoria;
import pe.utp.tienda.structural.decorator.*;
import pe.utp.tienda.structural.facade.VentaFacade;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Evaluación PC2 – Patrones de Diseño (Semanas 6 a 9)")
public class PatronesTest {

    @Nested
    @DisplayName("B. Patrones Creacionales (Unidad 2)")
    class CreacionalesTest {

        @Test
        @DisplayName("1. Singleton: Única instancia en memoria para configuración y base de datos")
        void testSingleton() {
            ConfiguracionApp conf1 = ConfiguracionApp.getInstancia();
            ConfiguracionApp conf2 = ConfiguracionApp.getInstancia();
            assertSame(conf1, conf2, "ConfiguracionApp debe retornar exactamente la misma instancia");

            ConexionBD bd1 = ConexionBD.getInstancia();
            ConexionBD bd2 = ConexionBD.getInstancia();
            assertSame(bd1, bd2, "ConexionBD debe ser un Singleton global");
        }

        @Test
        @DisplayName("2. Prototype: Clonación profunda de variantes de prendas y pedidos")
        void testPrototype() {
            Polera original = new Polera("POL-1", "Polera Oversize", "M", 60.0, 10);
            Producto clon = original.clonarVariante("POL-2", "XL", 5);

            assertNotSame(original, clon, "El clon debe ser un nuevo objeto en memoria");
            assertEquals("POL-2", clon.getSku());
            assertEquals("XL", clon.getTalla());
            assertEquals(5, clon.getStock());
            assertEquals("M", original.getTalla(), "El objeto original no debe mutar");

            Cliente c = new Cliente("72345678", "Ana", "Torres");
            Pedido p1 = new Pedido.Builder(c).agregarItem(original, 2).build();
            Pedido pCopia = p1.clone();
            assertNotSame(p1.getItems(), pCopia.getItems(), "Los items deben clonarse en profundidad");
        }

        @Test
        @DisplayName("3. Builder: Construcción fluida y validación de reglas de negocio")
        void testBuilder() {
            Cliente c = new Cliente("72345678", "Ana", "Torres");
            Polera pol = new Polera("POL-1", "Polera", "M", 50.0, 10);

            Pedido pedido = new Pedido.Builder(c)
                    .agregarItem(pol, 2)
                    .build();

            assertNotNull(pedido);
            assertEquals(1, pedido.getItems().size());
            assertEquals(100.0, pedido.getSubtotal(), 0.001);

            // Regla: no se puede construir sin ítems
            assertThrows(IllegalStateException.class, () -> new Pedido.Builder(c).build(),
                    "El Builder debe lanzar excepción si el pedido no tiene ítems");
        }

        @Test
        @DisplayName("4. Factory Method: Creación desacoplada de Boletas y Facturas")
        void testFactoryMethod() {
            Cliente c = new Cliente("72345678", "Ana", "Torres");
            Polera pol = new Polera("POL-1", "Polera", "M", 50.0, 10);
            Pedido pedido = new Pedido.Builder(c).agregarItem(pol, 1).build();

            String boleta = ComprobanteFactory.de("BOLETA").emitir(pedido, 50.0);
            assertTrue(boleta.startsWith("BOLETA"), "La fábrica debe emitir Boleta");

            String factura = ComprobanteFactory.de("FACTURA").emitir(pedido, 50.0);
            assertTrue(factura.startsWith("FACTURA"), "La fábrica debe emitir Factura con cálculo de IGV");
        }

        @Test
        @DisplayName("5. Abstract Factory: Familias de pasarelas de pago (Yape vs Tarjeta vs Plin)")
        void testAbstractFactory() {
            PasarelaPagoFactory yapeFactory = PasarelaPagoFactory.para("YAPE");
            assertNotNull(yapeFactory.crearMetodoPago());
            assertNotNull(yapeFactory.crearRecibo());
            // Yape tiene límite de 500
            assertFalse(yapeFactory.crearMetodoPago().cobrar(800.0), "Yape debe rechazar montos sobre 500");

            PasarelaPagoFactory tarjetaFactory = PasarelaPagoFactory.para("TARJETA");
            assertTrue(tarjetaFactory.crearMetodoPago().cobrar(800.0), "Tarjeta debe procesar montos mayores");
        }
    }

    @Nested
    @DisplayName("C. Patrones Estructurales (Unidad 3)")
    class EstructuralesTest {

        @Test
        @DisplayName("1. Adapter: Adaptación de la API legacy de RENIEC")
        void testAdapter() {
            ReniecAdapter adapter = new ReniecAdapter(new ReniecApiExterna());
            assertTrue(adapter.validarDni("72345678"), "DNI válido de 8 dígitos debe retornar true");
            assertFalse(adapter.validarDni("1234"), "DNI menor a 8 dígitos debe ser rechazado");
            assertFalse(adapter.validarDni("letras!!"), "DNI alfanumérico debe ser rechazado");
        }

        @Test
        @DisplayName("2. Facade: Orquestación integral del flujo de venta")
        void testFacade() {
            VentaFacade facade = new VentaFacade(
                    new ReniecAdapter(new ReniecApiExterna()),
                    new InventarioService(),
                    new NotificacionService());

            Cliente cliente = new Cliente("72345678", "Ana", "Torres");
            Polera polera = new Polera("POL-1", "Polera Oversize", "M", 80.0, 10);
            Pedido pedido = new Pedido.Builder(cliente).agregarItem(polera, 1).build();

            assertDoesNotThrow(() -> facade.registrarVenta(pedido, "TARJETA", "BOLETA"));
            assertEquals(9, polera.getStock(), "El stock debe descontarse tras el proceso de la fachada");
        }

        @Test
        @DisplayName("3. Decorator: Composición dinámica de precios (descuentos, IGV, envío)")
        void testDecorator() {
            CalculoPrecio base = new PrecioBase(100.0);
            CalculoPrecio conDesc = new ConDescuento(base, 10.0);       // 100 - 10 = 90
            CalculoPrecio conIgv = new ConIgv(conDesc);                 // 90 * 1.18 = 106.20
            CalculoPrecio conEnvio = new ConEnvio(conIgv, 15.0);        // 106.20 + 15 = 121.20

            assertEquals(121.20, conEnvio.calcular(), 0.001,
                    "La cadena de decoradores debe calcular acumulativamente el precio");
        }

        @Test
        @DisplayName("4. Composite: Tratamiento uniforme de categorías y productos en árbol")
        void testComposite() {
            Categoria catalogo = new Categoria("Tienda");
            Categoria poleras = new Categoria("Poleras");
            Polera p1 = new Polera("P-1", "Polera Roja", "S", 50.0, 10);
            Polera p2 = new Polera("P-2", "Polera Azul", "M", 50.0, 5);

            poleras.agregar(p1);
            poleras.agregar(p2);
            catalogo.agregar(poleras);

            assertEquals(15, catalogo.getStockTotal(), "El stock total del árbol debe ser recursivo (10 + 5)");
            assertEquals(750.0, catalogo.getPrecioTotal(), 0.001, "La valorización total debe ser 15 * 50 = 750");
        }
    }
}
