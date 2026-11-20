package pe.utp.tienda.controller;

import pe.utp.tienda.model.Producto;

/** Datos que captura la pantalla de venta (la capa de presentacion no arma objetos de dominio). */
public record SolicitudVenta(String dni, String nombreCompleto, Producto producto, int cantidad,
                             double descuentoPct, double costoEnvio, String metodoPago, String tipoComprobante) { }
