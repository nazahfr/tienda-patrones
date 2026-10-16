package pe.utp.tienda.structural.facade;

import pe.utp.tienda.creational.abstractfactory.IMetodoPago;
import pe.utp.tienda.creational.abstractfactory.PasarelaPagoFactory;
import pe.utp.tienda.creational.abstractfactory.ReciboPago;
import pe.utp.tienda.creational.factory.ComprobanteFactory;
import pe.utp.tienda.model.Pedido;
import pe.utp.tienda.service.InventarioService;
import pe.utp.tienda.service.NotificacionService;
import pe.utp.tienda.structural.adapter.ValidadorDocumento;
import pe.utp.tienda.structural.decorator.*;

/** Facade: un solo metodo orquesta validacion, inventario, precio, pago, comprobante y notificacion. */
public class VentaFacade {
    private final ValidadorDocumento validador;
    private final InventarioService inventario;
    private final NotificacionService notificaciones;

    public VentaFacade(ValidadorDocumento validador, InventarioService inventario, NotificacionService notificaciones) {
        this.validador = validador;
        this.inventario = inventario;
        this.notificaciones = notificaciones;
    }

    public String registrarVenta(Pedido pedido, String metodoPago, String tipoComprobante) {
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
            pedido.setEstado("Cancelado");
            throw new IllegalStateException("Pago rechazado por " + pago.nombre());
        }
        ReciboPago recibo = pasarela.crearRecibo();
        // 5. Descontar stock (solo si el pago fue aprobado)
        inventario.descontarStock(pedido);
        pedido.setEstado("Pagado");
        // 6. Emitir comprobante (Factory Method)
        String comprobante = ComprobanteFactory.de(tipoComprobante).emitir(pedido, total);
        // 7. Notificar
        notificaciones.enviar(pedido.getCliente().getNombreCompleto(), "Pedido pagado. " + recibo.generar(total));
        return precio.detalle() + String.format(" = S/ %.2f%n%s", total, comprobante);
    }
}
