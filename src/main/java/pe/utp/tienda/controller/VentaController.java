package pe.utp.tienda.controller;

import pe.utp.tienda.behavioral.memento.BorradorPedido;
import pe.utp.tienda.model.Cliente;
import pe.utp.tienda.model.Pedido;
import pe.utp.tienda.structural.facade.VentaFacade;

/**
 * Capa de Controladores (arquitectura en capas de la PC1): traduce lo que captura la pantalla
 * a objetos de dominio y llama a la Facade. La GUI ya no conoce Builder, Cliente ni Pedido.
 */
public class VentaController {
    private final VentaFacade facade;

    public VentaController(VentaFacade facade) { this.facade = facade; }

    /** Cobra y registra la venta completa a traves de la Facade. */
    public ResultadoVenta registrar(SolicitudVenta s) {
        Pedido pedido = crearPedido(s);
        String detalle = facade.registrarVenta(pedido, s.metodoPago(), s.tipoComprobante());
        return new ResultadoVenta(pedido, detalle);
    }

    /** Arma el pedido (estado Pendiente) sin cobrarlo: lo usan las pantallas que recorren su ciclo de vida. */
    public Pedido crearPedido(SolicitudVenta s) {
        return new Pedido.Builder(cliente(s.dni(), s.nombreCompleto()))
                .agregarItem(s.producto(), s.cantidad())
                .descuento(s.descuentoPct())
                .envio(s.costoEnvio())
                .build();
    }

    /** Pedido en edicion (carrito) para la pantalla con deshacer/rehacer (Memento). */
    public BorradorPedido nuevoBorrador(String dni, String nombreCompleto) {
        return new BorradorPedido(cliente(dni, nombreCompleto));
    }

    private Cliente cliente(String dni, String nombreCompleto) {
        String[] partes = nombreCompleto.trim().split(" ", 2);
        return new Cliente(dni.trim(), partes[0], partes.length > 1 ? partes[1] : "");
    }
}
