package pe.utp.tienda.app;

import pe.utp.tienda.behavioral.command.InvocadorComandos;
import pe.utp.tienda.behavioral.observer.AuditoriaObserver;
import pe.utp.tienda.behavioral.observer.ComprasStockObserver;
import pe.utp.tienda.behavioral.observer.DashboardObserver;
import pe.utp.tienda.behavioral.observer.NotificadorClienteObserver;
import pe.utp.tienda.controller.VentaController;
import pe.utp.tienda.model.*;
import pe.utp.tienda.persistence.*;
import pe.utp.tienda.service.InventarioService;
import pe.utp.tienda.service.NotificacionService;
import pe.utp.tienda.structural.adapter.ReniecAdapter;
import pe.utp.tienda.structural.adapter.ReniecApiExterna;
import pe.utp.tienda.structural.facade.VentaFacade;
import pe.utp.tienda.structural.proxy.*;

/**
 * Raiz de composicion: el UNICO lugar que hace "new" de servicios y los conecta entre si (inyeccion de dependencias
 * por constructor). Las demas clases reciben sus colaboradores y solo conocen interfaces.
 * Persistencia: variable de entorno TIENDA_PERSISTENCIA = memoria (por defecto) | mysql.
 */
public class ContextoTienda {
    public static final Usuario ADMIN = new Usuario("Rosa", Rol.ADMIN);
    public static final Usuario VENDEDOR = new Usuario("Luis", Rol.VENDEDOR);

    private final ProductoDAO productoDAO;
    private final PedidoDAO pedidoDAO;
    private final NotificacionService notificaciones = new NotificacionService();
    private final InventarioService inventario;
    private final VentaFacade facade;
    private final VentaController ventas;
    private final InvocadorComandos comandos = new InvocadorComandos();
    private final AuditoriaObserver auditoria = new AuditoriaObserver();
    private final DashboardObserver dashboard = new DashboardObserver();
    private final ComprasStockObserver compras = new ComprasStockObserver();
    private final CatalogoRepositorio catalogoReal;
    private final CatalogoCacheProxy catalogo;

    public ContextoTienda(ProductoDAO productoDAO, PedidoDAO pedidoDAO) {
        this.productoDAO = productoDAO;
        this.pedidoDAO = pedidoDAO;
        this.inventario = new InventarioService(productoDAO);
        this.facade = new VentaFacade(new ReniecAdapter(new ReniecApiExterna()), inventario, notificaciones, pedidoDAO);
        this.ventas = new VentaController(facade);
        this.catalogoReal = new CatalogoRepositorio(productoDAO);
        this.catalogo = new CatalogoCacheProxy(catalogoReal);

        // Observer: quien escucha a quien
        facade.agregarObservadorPedido(new NotificadorClienteObserver(notificaciones));
        facade.agregarObservadorPedido(auditoria);
        facade.agregarObservadorPedido(dashboard);
        inventario.suscribir(compras);
    }

    /** Contexto por defecto: datos en memoria con tres prendas de ejemplo (o MySQL si TIENDA_PERSISTENCIA=mysql). */
    public static ContextoTienda crear() {
        String modo = System.getenv().getOrDefault("TIENDA_PERSISTENCIA", "memoria");
        if ("mysql".equalsIgnoreCase(modo)) {
            ProductoDAO productos = new ProductoDAOJdbc();
            return new ContextoTienda(productos, new PedidoDAOJdbc(productos));
        }
        return enMemoria();
    }

    public static ContextoTienda enMemoria() {
        ProductoDAO productos = new ProductoDAOMemoria();
        productos.guardar(new Polera("POL-OVR-001", "Polera Oversize Negra", "M", 59.0, 25));
        productos.guardar(new Pantalon("PAN-CRG-001", "Pantalon Cargo Beige", "32", 79.0, 15));
        productos.guardar(new Zapatilla("ZAP-URB-001", "Zapatillas Urbanas Blancas", "40", 148.0, 10));
        return new ContextoTienda(productos, new PedidoDAOMemoria());
    }

    /** El proxy de seguridad es por usuario: cada sesion recibe el suyo. */
    public ServicioReportesProxy reportesPara(Usuario usuario) {
        return new ServicioReportesProxy(new ServicioReportesReal(pedidoDAO, productoDAO), usuario);
    }

    public Producto producto(String sku) {
        return productoDAO.buscarPorSku(sku).orElseThrow(() -> new IllegalArgumentException("No existe el SKU " + sku));
    }

    public ProductoDAO productos() { return productoDAO; }
    public PedidoDAO pedidos() { return pedidoDAO; }
    public NotificacionService notificaciones() { return notificaciones; }
    public InventarioService inventario() { return inventario; }
    public VentaFacade facade() { return facade; }
    public VentaController ventas() { return ventas; }
    public InvocadorComandos comandos() { return comandos; }
    public AuditoriaObserver auditoria() { return auditoria; }
    public DashboardObserver dashboard() { return dashboard; }
    public ComprasStockObserver compras() { return compras; }
    public CatalogoRepositorio catalogoReal() { return catalogoReal; }
    public CatalogoCacheProxy catalogo() { return catalogo; }
}
