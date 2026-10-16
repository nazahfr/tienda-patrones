package pe.utp.tienda.creational.factory;

import java.util.concurrent.atomic.AtomicInteger;
import pe.utp.tienda.model.Pedido;

public class FacturaElectronica implements Comprobante {
    private static final AtomicInteger CORRELATIVO = new AtomicInteger(1);

    @Override
    public String generar(Pedido pedido, double total) {
        return String.format("FACTURA ELECTRONICA F001-%06d | Cliente: %s (DNI %s) | Total: S/ %.2f",
                CORRELATIVO.getAndIncrement(), pedido.getCliente().getNombreCompleto(),
                pedido.getCliente().getDni(), total);
    }
}
