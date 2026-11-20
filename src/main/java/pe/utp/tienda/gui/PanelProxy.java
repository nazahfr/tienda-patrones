package pe.utp.tienda.gui;

import javax.swing.*;
import java.awt.*;
import pe.utp.tienda.app.ContextoTienda;
import pe.utp.tienda.model.Usuario;
import pe.utp.tienda.structural.proxy.AccesoDenegadoException;
import pe.utp.tienda.structural.proxy.ServicioReportesProxy;

/** Proxy: (1) proteccion y auditoria de reportes segun el rol; (2) cache del catalogo para no repetir consultas a la BD. */
class PanelProxy extends PanelPatron {
    private static final String[] SKUS = {"POL-OVR-001", "PAN-CRG-001", "ZAP-URB-001"};
    private final ServicioReportesProxy proxyAdmin;
    private final ServicioReportesProxy proxyVendedor;
    private final JTextArea reporte = salida(6, DemoGUI.CYAN);
    private final JTextArea auditoria = salida(5, DemoGUI.YELLOW);
    private final JLabel contadores = new JLabel();

    PanelProxy(DemoGUI gui, ContextoTienda ctx) {
        super(gui, ctx);
        this.proxyAdmin = ctx.reportesPara(ContextoTienda.ADMIN);
        this.proxyVendedor = ctx.reportesPara(ContextoTienda.VENDEDOR);
    }

    @Override
    JPanel build() {
        JPanel p = base("🛡", "Proxy", "Proxy de seguridad para reportes y proxy de cache para el catalogo", DemoGUI.CYAN);

        JComboBox<String> usuario = gui.combo(ContextoTienda.ADMIN.toString(), ContextoTienda.VENDEDOR.toString());
        JComboBox<String> tipo = gui.combo("VENTAS", "INVENTARIO");
        JComboBox<String> formato = gui.combo("CSV", "JSON", "HTML");
        JButton generar = gui.btn("📄  generar()", DemoGUI.ACCENT);
        generar.addActionListener(e -> {
            ServicioReportesProxy proxy = usuario.getSelectedIndex() == 0 ? proxyAdmin : proxyVendedor;
            Usuario u = usuario.getSelectedIndex() == 0 ? ContextoTienda.ADMIN : ContextoTienda.VENDEDOR;
            gui.logSep("PROXY – seguridad");
            try {
                String r = proxy.generar((String) tipo.getSelectedItem(), (String) formato.getSelectedItem());
                gui.logOk(u + " obtuvo " + tipo.getSelectedItem());
                mostrar(reporte, r, DemoGUI.GREEN);
            } catch (AccesoDenegadoException ex) {
                gui.logFail("PROXY denegó: " + ex.getMessage());
                mostrar(reporte, "ACCESO DENEGADO\n" + ex.getMessage(), DemoGUI.RED);
            }
            mostrar(auditoria, String.join("\n", proxyAdmin.getAuditoria()) + (proxyAdmin.getAuditoria().isEmpty() || proxyVendedor.getAuditoria().isEmpty() ? "" : "\n")
                    + String.join("\n", proxyVendedor.getAuditoria()), DemoGUI.YELLOW);
        });

        p.add(gui.row(gui.label("Usuario:"), usuario, gui.label("Reporte:"), tipo, gui.label("Formato:"), formato, generar));
        separador(p);
        p.add(gui.card("Reporte entregado (o rechazado) por el Proxy de seguridad", scroll(reporte)));
        separador(p);
        p.add(gui.card("Auditoria del proxy (cada intento queda registrado)", scroll(auditoria)));

        // --- cache ---
        JComboBox<String> sku = gui.combo(SKUS);
        JButton consultar = gui.btn("🔎  buscarPorSku()", DemoGUI.GREEN);
        consultar.addActionListener(e -> {
            String s = (String) sku.getSelectedItem();
            ctx.catalogo().buscarPorSku(s);
            gui.logOk("PROXY cache – " + s + " | accesos reales a la BD: " + ctx.catalogoReal().getAccesos()
                    + ", aciertos de cache: " + ctx.catalogo().getAciertos());
            actualizarContadores();
        });
        JButton limpiar = gui.btn("🧹  Vaciar cache", DemoGUI.YELLOW);
        limpiar.addActionListener(e -> { ctx.catalogo().limpiar(); gui.logOk("PROXY cache – vaciada"); });
        contadores.setFont(DemoGUI.FONT_BOLD);
        contadores.setForeground(DemoGUI.TEXT);
        actualizarContadores();

        separador(p);
        p.add(gui.row(gui.label("SKU:"), sku, consultar, limpiar));
        separador(p);
        p.add(gui.card("Proxy de cache del catalogo", contadores));
        return p;
    }

    private void actualizarContadores() {
        contadores.setText("Accesos reales a la BD: " + ctx.catalogoReal().getAccesos()
                + "     ·     fallos de cache: " + ctx.catalogo().getFallos()
                + "     ·     aciertos de cache: " + ctx.catalogo().getAciertos());
    }
}
