package pe.utp.tienda.behavioral.observer;

import pe.utp.tienda.model.Pedido;

/** Evento que publica el Pedido cuando cambia su estado. */
public record CambioEstadoPedido(Pedido pedido, String estadoAnterior, String estadoNuevo) { }
