package pe.utp.tienda.structural.bridge;

import java.util.List;

public class ExportadorHtml implements ExportadorReporte {
    @Override public String nombre() { return "HTML"; }
    @Override public String extension() { return "html"; }

    @Override
    public String exportar(String titulo, List<String> columnas, List<List<String>> filas) {
        StringBuilder sb = new StringBuilder("<html><body><h1>").append(esc(titulo)).append("</h1>\n<table border=\"1\">\n<tr>");
        for (String c : columnas) sb.append("<th>").append(esc(c)).append("</th>");
        sb.append("</tr>\n");
        for (List<String> fila : filas) {
            sb.append("<tr>");
            for (String celda : fila) sb.append("<td>").append(esc(celda)).append("</td>");
            sb.append("</tr>\n");
        }
        return sb.append("</table></body></html>").toString();
    }

    private static String esc(String s) { return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"); }
}
