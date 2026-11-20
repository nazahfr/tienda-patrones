package pe.utp.tienda.structural.bridge;

import java.util.List;

/** Implementor (GoF): el formato de salida. Puede variar (HTML, CSV, JSON...) sin tocar los reportes. */
public interface ExportadorReporte {
    String nombre();
    String extension();
    String exportar(String titulo, List<String> columnas, List<List<String>> filas);

    static ExportadorReporte para(String formato) {
        return switch (formato.toUpperCase()) {
            case "HTML" -> new ExportadorHtml();
            case "CSV" -> new ExportadorCsv();
            case "JSON" -> new ExportadorJson();
            default -> throw new IllegalArgumentException("Formato no soportado: " + formato);
        };
    }
}
