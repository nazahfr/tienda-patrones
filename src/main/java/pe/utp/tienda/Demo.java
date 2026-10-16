package pe.utp.tienda;

import pe.utp.tienda.creational.abstractfactory.PasarelaPagoFactory;
import pe.utp.tienda.creational.prototype.CatalogoPrototipos;
import pe.utp.tienda.creational.singleton.ConexionBD;
import pe.utp.tienda.creational.singleton.ConfiguracionApp;
import pe.utp.tienda.model.*;
import pe.utp.tienda.service.InventarioService;
import pe.utp.tienda.service.NotificacionService;
import pe.utp.tienda.structural.adapter.ReniecAdapter;
import pe.utp.tienda.structural.adapter.ReniecApiExterna;
import pe.utp.tienda.structural.composite.Categoria;
import pe.utp.tienda.structural.facade.VentaFacade;

public class Demo {
    public static void main(String[] args) {
        System.out.println("=== 1. SINGLETON ===");
        System.out.println("Misma instancia ConfiguracionApp? " + (ConfiguracionApp.getInstancia() == ConfiguracionApp.getInstancia()));
        System.out.println("Misma instancia ConexionBD? " + (ConexionBD.getInstancia() == ConexionBD.getInstancia()));
        System.out.println("IGV configurado: " + ConfiguracionApp.getInstancia().getIgv() + " | URL BD: " + ConexionBD.getInstancia().getUrl());

        System.out.println("\n=== 2. PROTOTYPE ===");
        Producto polera = new Polera("POL-OVR-001", "Polera Oversize Negra", "M", 59.00, 25);
        CatalogoPrototipos catalogoProto = new CatalogoPrototipos();
        catalogoProto.registrar("polera-oversize", polera);
        Producto poleraL = catalogoProto.crearVariante("polera-oversize", "POL-OVR-002", "L", 12);
        System.out.println("Original: " + polera.getSku() + " talla " + polera.getTalla() + " stock " + polera.getStock());
        System.out.println("Clon    : " + poleraL.getSku() + " talla " + poleraL.getTalla() + " stock " + poleraL.getStock());

        Producto pantalon = new Pantalon("PAN-CRG-002", "Pantalon Cargo Beige", "32", 79.00, 15);
        Producto zapatilla = new Zapatilla("ZAP-URB-003", "Zapatillas Urbanas Blancas", "40", 148.00, 10);
        Cliente ana = new Cliente("72345678", "Ana", "Torres");

        System.out.println("\n=== 3. BUILDER ===");
        Pedido pedido = new Pedido.Builder(ana)
                .agregarItem(polera, 2)
                .agregarItem(pantalon, 1)
                .descuento(10)
                .envio(12.00)
                .notas("Entrega en horario de tarde")
                .build();
        System.out.println("Pedido construido: " + pedido.getItems().size() + " items, subtotal S/ " + pedido.getSubtotal()
                + ", descuento " + pedido.getDescuentoPct() + "%, envio S/ " + pedido.getCostoEnvio());

        System.out.println("\n=== 4. PROTOTYPE (repetir pedido, copia profunda) ===");
        Pedido repetido = pedido.clone();
        System.out.println("Items distintos objetos? " + (pedido.getItems() != repetido.getItems())
                + " | estado del clon: " + repetido.getEstado());

        System.out.println("\n=== 5. COMPOSITE ===");
        Categoria ropa = new Categoria("Tienda");
        Categoria superior = new Categoria("Poleras");
        Categoria inferior = new Categoria("Pantalones");
        Categoria calzado = new Categoria("Zapatillas");
        superior.agregar(polera); superior.agregar(poleraL);
        inferior.agregar(pantalon);
        calzado.agregar(zapatilla);
        ropa.agregar(superior); ropa.agregar(inferior); ropa.agregar(calzado);
        ropa.mostrar("");
        System.out.println("Valor total del inventario: S/ " + ropa.getPrecioTotal());

        System.out.println("\n=== 6. ABSTRACT FACTORY ===");
        PasarelaPagoFactory yape = PasarelaPagoFactory.para("YAPE");
        System.out.println("Familia Yape  -> " + yape.crearMetodoPago().nombre() + " / " + yape.crearRecibo().generar(100));
        PasarelaPagoFactory tarjeta = PasarelaPagoFactory.para("TARJETA");
        System.out.println("Familia Tarjeta -> " + tarjeta.crearMetodoPago().nombre() + " / " + tarjeta.crearRecibo().generar(100));

        System.out.println("\n=== 7. FACADE + ADAPTER + DECORATOR + FACTORY METHOD ===");
        VentaFacade facade = new VentaFacade(new ReniecAdapter(new ReniecApiExterna()),
                new InventarioService(), new NotificacionService());
        System.out.println(facade.registrarVenta(pedido, "YAPE", "BOLETA"));
        System.out.println("Estado del pedido: " + pedido.getEstado());
        System.out.println("Stock polera M tras la venta: " + polera.getStock() + " | pantalon: " + pantalon.getStock());

        System.out.println("\n=== 8. CASO DE ERROR (DNI invalido) ===");
        try {
            Pedido malo = new Pedido.Builder(new Cliente("123", "Pedro", "Gomez")).agregarItem(zapatilla, 1).build();
            facade.registrarVenta(malo, "TARJETA", "FACTURA");
        } catch (IllegalArgumentException e) {
            System.out.println("Rechazado correctamente: " + e.getMessage());
        }
    }
}
