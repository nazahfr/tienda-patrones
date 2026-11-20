package pe.utp.tienda.behavioral.observer;

import pe.utp.tienda.model.Producto;

/** Evento que publica el inventario cuando una prenda llega al stock minimo. */
public record AlertaStock(Producto producto, int stockActual, int stockMinimo) { }
