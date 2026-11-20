package pe.utp.tienda.structural.bridge;

import java.util.ArrayList;
import java.util.List;
import pe.utp.tienda.model.Pedido;

/** Refined Abstraction: ventas por pedido. Es informacion sensible (por eso se entrega a traves del Proxy). */
public class ReporteVentas extends Reporte {
    private final List<Pedido> pedidos;

    public ReporteVentas(List<Pedido> pedidos, ExportadorReporte exportador) {
        super(exportador);
        this.pedidos = pedidos;
    }

    @Override public String titulo() { return "Reporte de Ventas"; }
    @Override public List<String> columnas() { return List.of("Pedido", "Cliente", "Items", "Subtotal", "Estado"); }

    @Override
    public List<List<String>> filas() {
        List<List<String>> filas = new ArrayList<>();
        for (Pedido p : pedidos) {
            filas.add(List.of(String.valueOf(p.getId()), p.getCliente().getNombreCompleto(),
                    String.valueOf(p.getItems().size()), String.format("%.2f", p.getSubtotal()), p.getEstado()));
        }
        return filas;
    }
}
