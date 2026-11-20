package pe.utp.tienda.structural.bridge;

import java.util.ArrayList;
import java.util.List;
import pe.utp.tienda.model.Producto;

/** Refined Abstraction: existencias del catalogo. */
public class ReporteInventario extends Reporte {
    private final List<Producto> productos;

    public ReporteInventario(List<Producto> productos, ExportadorReporte exportador) {
        super(exportador);
        this.productos = productos;
    }

    @Override public String titulo() { return "Reporte de Inventario"; }
    @Override public List<String> columnas() { return List.of("SKU", "Tipo", "Talla", "Precio", "Stock", "Minimo"); }

    @Override
    public List<List<String>> filas() {
        List<List<String>> filas = new ArrayList<>();
        for (Producto p : productos) {
            filas.add(List.of(p.getSku(), p.tipo(), p.getTalla(), String.format("%.2f", p.getPrecio()),
                    String.valueOf(p.getStock()), String.valueOf(p.getStockMinimo())));
        }
        return filas;
    }
}
