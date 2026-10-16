package pe.utp.tienda.creational.factory;

import java.util.concurrent.atomic.AtomicInteger;
import pe.utp.tienda.model.Pedido;

public class Boleta implements Comprobante {
    private static final AtomicInteger CORRELATIVO = new AtomicInteger(1);

    @Override
    public String generar(Pedido pedido, double total) {
        return String.format("BOLETA B001-%06d | Cliente: %s | Total: S/ %.2f",
                CORRELATIVO.getAndIncrement(), pedido.getCliente().getNombreCompleto(), total);
    }
}
