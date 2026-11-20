package pe.utp.tienda.structural.bridge;

import java.util.List;

/**
 * Abstraction (GoF) del Bridge: define QUE reporte es (titulo, columnas, filas) y delega el COMO se escribe
 * (HTML, CSV, JSON) en el ExportadorReporte. Las dos jerarquias crecen por separado.
 */
public abstract class Reporte {
    protected ExportadorReporte exportador;

    protected Reporte(ExportadorReporte exportador) { this.exportador = exportador; }

    public abstract String titulo();
    public abstract List<String> columnas();
    public abstract List<List<String>> filas();

    public void setExportador(ExportadorReporte exportador) { this.exportador = exportador; }

    public String exportar() { return exportador.exportar(titulo(), columnas(), filas()); }
}
