package pe.utp.tienda.gui;

import javax.swing.*;
import pe.utp.tienda.app.ContextoTienda;
import pe.utp.tienda.structural.bridge.*;

/** Bridge: la jerarquia de reportes (que se muestra) y la de exportadores (como se escribe) combinan sin multiplicar clases. */
class PanelBridge extends PanelPatron {
    private final JTextArea salida = salida(11, DemoGUI.CYAN);

    PanelBridge(DemoGUI gui, ContextoTienda ctx) { super(gui, ctx); }

    @Override
    JPanel build() {
        JPanel p = base("🌉", "Bridge", "Reporte x Exportador: 2 reportes por 3 formatos con solo 5 clases", DemoGUI.CYAN);
        JComboBox<String> tipo = gui.combo("INVENTARIO", "VENTAS");
        JComboBox<String> formato = gui.combo("CSV", "JSON", "HTML");
        JButton exportar = gui.btn("📤  exportar()", DemoGUI.ACCENT);
        exportar.addActionListener(e -> {
            ExportadorReporte exp = ExportadorReporte.para((String) formato.getSelectedItem());
            Reporte r = "INVENTARIO".equals(tipo.getSelectedItem())
                    ? new ReporteInventario(ctx.productos().listar(), exp)
                    : new ReporteVentas(ctx.pedidos().listar(), exp);
            gui.logSep("BRIDGE – " + r.getClass().getSimpleName() + " × " + exp.getClass().getSimpleName());
            String texto = r.exportar();
            gui.logOk(texto.split("\n").length + " linea(s) en formato " + exp.nombre());
            mostrar(salida, texto, DemoGUI.GREEN);
        });
        p.add(gui.row(gui.label("Reporte:"), tipo, gui.label("Formato:"), formato, exportar));
        separador(p);
        p.add(gui.card("Resultado de reporte.exportar()", scroll(salida)));
        return p;
    }
}
