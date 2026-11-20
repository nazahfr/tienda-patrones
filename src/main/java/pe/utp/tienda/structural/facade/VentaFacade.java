package pe.utp.tienda.structural.facade;

import java.util.ArrayList;
import java.util.List;
import pe.utp.tienda.behavioral.observer.CambioEstadoPedido;
import pe.utp.tienda.behavioral.observer.Observador;
import pe.utp.tienda.creational.abstractfactory.IMetodoPago;
import pe.utp.tienda.creational.abstractfactory.PasarelaPagoFactory;
import pe.utp.tienda.creational.abstractfactory.ReciboPago;
import pe.utp.tienda.creational.factory.ComprobanteFactory;
import pe.utp.tienda.model.Pedido;
import pe.utp.tienda.persistence.PedidoDAO;
import pe.utp.tienda.persistence.PedidoDAOMemoria;
import pe.utp.tienda.service.InventarioService;
import pe.utp.tienda.service.NotificacionService;
import pe.utp.tienda.structural.adapter.ValidadorDocumento;
import pe.utp.tienda.structural.decorator.*;

/** Facade: un solo metodo orquesta validacion, inventario, precio, pago, comprobante, notificacion y persistencia. */
public class VentaFacade {
    private final ValidadorDocumento validador;
    private final InventarioService inventario;
    private final NotificacionService notificaciones;
    private final PedidoDAO pedidos;
    private final List<Observador<CambioEstadoPedido>> observadoresPedido = new ArrayList<>();

    public VentaFacade(ValidadorDocumento validador, InventarioService inventario, NotificacionService notificaciones) {
        this(validador, inventario, notificaciones, new PedidoDAOMemoria());
    }

    public VentaFacade(ValidadorDocumento validador, InventarioService inventario,
                       NotificacionService notificaciones, PedidoDAO pedidos) {
        this.validador = validador;
        this.inventario = inventario;
        this.notificaciones = notificaciones;
        this.pedidos = pedidos;
    }

    /** Observadores que se suscriben a cada pedido que procesa la fachada (Observer). */
    public void agregarObservadorPedido(Observador<CambioEstadoPedido> o) { observadoresPedido.add(o); }

    public String registrarVenta(Pedido pedido, String metodoPago, String tipoComprobante) {
        observadoresPedido.forEach(pedido::suscribir);
        // 1. Validar al cliente (Adapter)
        if (!validador.validarDni(pedido.getCliente().getDni())) {
            throw new IllegalArgumentException("DNI invalido: " + pedido.getCliente().getDni());
        }
        // 2. Verificar stock antes de cobrar
        inventario.verificarStock(pedido);
        // 3. Calcular el total (Decorator)
        CalculoPrecio precio = new ConEnvio(
                new ConIgv(new ConDescuento(new PrecioBase(pedido.getSubtotal()), pedido.getDescuentoPct())),
                pedido.getCostoEnvio());
        double total = precio.calcular();
        // 4. Cobrar con la pasarela elegida (Abstract Factory)
        PasarelaPagoFactory pasarela = PasarelaPagoFactory.para(metodoPago);
        IMetodoPago pago = pasarela.crearMetodoPago();
        if (!pago.cobrar(total)) {
            pedido.cancelar();                                     // State: Pendiente -> Cancelado
            pedidos.guardar(pedido);
            throw new IllegalStateException("Pago rechazado por " + pago.nombre());
        }
        ReciboPago recibo = pasarela.crearRecibo();
        // 5. Descontar stock (solo si el pago fue aprobado); puede disparar la alerta de stock minimo (Observer)
        inventario.descontarStock(pedido);
        pedido.pagar();                                            // State: Pendiente -> Pagado
        // 6. Emitir comprobante (Factory Method)
        String comprobante = ComprobanteFactory.de(tipoComprobante).emitir(pedido, total);
        // 7. Persistir y notificar
        pedidos.guardar(pedido);
        notificaciones.enviar(pedido.getCliente().getNombreCompleto(), "Pedido pagado. " + recibo.generar(total));
        return precio.detalle() + String.format(" = S/ %.2f%n%s", total, comprobante);
    }
}
