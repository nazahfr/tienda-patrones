package pe.utp.tienda.structural.bridge;

import java.util.ArrayList;
import java.util.List;

/** CSV: Excel lo abre directamente. */
public class ExportadorCsv implements ExportadorReporte {
    @Override public String nombre() { return "CSV (Excel)"; }
    @Override public String extension() { return "csv"; }

    @Override
    public String exportar(String titulo, List<String> columnas, List<List<String>> filas) {
        StringBuilder sb = new StringBuilder(linea(columnas)).append("\n");
        for (List<String> fila : filas) sb.append(linea(fila)).append("\n");
        return sb.toString().stripTrailing();
    }

    private static String linea(List<String> celdas) {
        List<String> salida = new ArrayList<>();
        for (String c : celdas) {
            boolean necesitaComillas = c.contains(",") || c.contains("\"") || c.contains("\n");
            salida.add(necesitaComillas ? "\"" + c.replace("\"", "\"\"") + "\"" : c);
        }
        return String.join(",", salida);
    }
}
