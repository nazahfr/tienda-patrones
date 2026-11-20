package pe.utp.tienda.controller;

import pe.utp.tienda.model.Pedido;

public record ResultadoVenta(Pedido pedido, String detalle) { }
