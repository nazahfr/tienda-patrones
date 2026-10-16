package pe.utp.tienda;

import pe.utp.tienda.creational.abstractfactory.PasarelaPagoFactory;
import pe.utp.tienda.creational.factory.ComprobanteFactory;
import pe.utp.tienda.creational.singleton.ConfiguracionApp;
import pe.utp.tienda.model.*;
import pe.utp.tienda.service.InventarioService;
import pe.utp.tienda.service.NotificacionService;
import pe.utp.tienda.structural.adapter.ReniecAdapter;
import pe.utp.tienda.structural.adapter.ReniecApiExterna;
import pe.utp.tienda.structural.composite.Categoria;
import pe.utp.tienda.structural.decorator.*;
import pe.utp.tienda.structural.facade.VentaFacade;

/** Pruebas minimas sin dependencias externas (para correr con solo el JDK). */
public class Pruebas {
    private static int ok = 0, fail = 0;

    private static void check(String nombre, boolean condicion) {
        if (condicion) ok++; else fail++;
        System.out.println((condicion ? "[PASS] " : "[FAIL] ") + nombre);
    }

    public static void main(String[] args) {
        Producto polera = new Polera("POL-1", "Polera", "M", 50.0, 10);
        Cliente ana = new Cliente("72345678", "Ana", "Torres");

        check("Singleton devuelve siempre la misma instancia",
                ConfiguracionApp.getInstancia() == ConfiguracionApp.getInstancia());

        Producto clon = polera.clonarVariante("POL-2", "L", 5);
        check("Prototype: el clon es otro objeto con SKU y talla propios",
                clon != polera && clon.getSku().equals("POL-2") && clon.getTalla().equals("L") && polera.getTalla().equals("M"));

        Pedido p = new Pedido.Builder(ana).agregarItem(polera, 2).build();
        Pedido copia = p.clone();
        check("Prototype: copia profunda de items del pedido", p.getItems() != copia.getItems() && copia.getItems().size() == 1);

        boolean excepcion = false;
        try { new Pedido.Builder(ana).build(); } catch (IllegalStateException e) { excepcion = true; }
        check("Builder: rechaza un pedido sin items", excepcion);

        check("Factory Method: crea Boleta", ComprobanteFactory.de("BOLETA").emitir(p, 100).startsWith("BOLETA"));
        check("Factory Method: crea Factura", ComprobanteFactory.de("FACTURA").emitir(p, 100).startsWith("FACTURA"));
        check("Abstract Factory: Yape rechaza montos sobre su limite", !PasarelaPagoFactory.para("YAPE").crearMetodoPago().cobrar(900));
        check("Abstract Factory: Tarjeta acepta ese mismo monto", PasarelaPagoFactory.para("TARJETA").crearMetodoPago().cobrar(900));

        check("Adapter: valida un DNI de 8 digitos", new ReniecAdapter(new ReniecApiExterna()).validarDni("72345678"));
        check("Adapter: rechaza un DNI invalido", !new ReniecAdapter(new ReniecApiExterna()).validarDni("12"));

        double total = new ConIgv(new ConDescuento(new PrecioBase(100), 10)).calcular();
        check("Decorator: 100 -10% +IGV 18% = 106.20", Math.abs(total - 106.20) < 0.001);

        Categoria raiz = new Categoria("Raiz");
        Categoria sub = new Categoria("Sub");
        sub.agregar(polera);
        raiz.agregar(sub);
        raiz.agregar(clon);
        check("Composite: stock total recursivo = 15", raiz.getStockTotal() == 15);

        VentaFacade facade = new VentaFacade(new ReniecAdapter(new ReniecApiExterna()),
                new InventarioService(), new NotificacionService());
        facade.registrarVenta(p, "TARJETA", "BOLETA");
        check("Facade: venta pagada y stock descontado (10 -> 8)", p.getEstado().equals("Pagado") && polera.getStock() == 8);

        Pedido grande = new Pedido.Builder(ana).agregarItem(polera, 50).build();
        boolean sinStock = false;
        try { facade.registrarVenta(grande, "TARJETA", "BOLETA"); } catch (IllegalStateException e) { sinStock = true; }
        check("Facade: rechaza venta sin stock y no descuenta", sinStock && polera.getStock() == 8);

        System.out.println("\nResultado: " + ok + " correctas, " + fail + " fallidas");
        if (fail > 0) System.exit(1);
    }
}
