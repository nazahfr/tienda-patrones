package pe.utp.tienda.creational.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import pe.utp.tienda.model.Pedido;

/**
 * Template Method: el esqueleto "TIPO SERIE-correlativo | Cliente | Total" se escribe una vez;
 * cada comprobante solo define su titulo, su serie y como se describe al cliente.
 * El correlativo se lleva por serie (compartido entre instancias), igual que antes.
 */
public abstract class ComprobanteBase implements Comprobante {
    private static final Map<String, AtomicInteger> CORRELATIVOS = new ConcurrentHashMap<>();

    protected abstract String titulo();
    protected abstract String serie();
    protected abstract String descripcionCliente(Pedido pedido);

    @Override
    public String generar(Pedido pedido, double total) {
        int numero = CORRELATIVOS.computeIfAbsent(serie(), s -> new AtomicInteger(1)).getAndIncrement();
        return String.format("%s %s-%06d | Cliente: %s | Total: S/ %.2f",
                titulo(), serie(), numero, descripcionCliente(pedido), total);
    }
}
