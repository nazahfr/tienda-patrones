package pe.utp.tienda.structural.bridge;

import java.util.List;

/** JSON escrito a mano (sin librerias) para integraciones con otros sistemas o una API. */
public class ExportadorJson implements ExportadorReporte {
    @Override public String nombre() { return "JSON"; }
    @Override public String extension() { return "json"; }

    @Override
    public String exportar(String titulo, List<String> columnas, List<List<String>> filas) {
        StringBuilder sb = new StringBuilder("{\"reporte\":\"").append(esc(titulo)).append("\",\"filas\":[");
        for (int i = 0; i < filas.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("{");
            for (int j = 0; j < columnas.size(); j++) {
                if (j > 0) sb.append(",");
                sb.append("\"").append(esc(columnas.get(j))).append("\":\"").append(esc(filas.get(i).get(j))).append("\"");
            }
            sb.append("}");
        }
        return sb.append("]}").toString();
    }

    private static String esc(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\""); }
}
