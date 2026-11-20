package pe.utp.tienda.gui;

import javax.swing.*;
import java.awt.*;
import pe.utp.tienda.app.ContextoTienda;
import pe.utp.tienda.behavioral.command.*;
import pe.utp.tienda.model.Producto;

/** Command: cada operacion de inventario es un objeto que se ejecuta, se deshace, se rehace, se encola y queda auditada. */
class PanelCommand extends PanelPatron {
    private static final String[] SKUS = {"POL-OVR-001", "PAN-CRG-001", "ZAP-URB-001"};
    private final JTextArea bitacora = salida(9, DemoGUI.CYAN);
    private final JLabel stock = new JLabel();

    PanelCommand(DemoGUI gui, ContextoTienda ctx) { super(gui, ctx); }

    @Override
    JPanel build() {
        JPanel p = base("🎮", "Command", "Operaciones de inventario reversibles, encolables y auditadas", DemoGUI.GREEN);
        JComboBox<String> prod = gui.combo("POL-OVR-001", "PAN-CRG-001", "ZAP-URB-001");
        JTextField cant = gui.field("5");
        cant.setPreferredSize(new Dimension(60, 32));
        stock.setFont(DemoGUI.FONT_BOLD);
        stock.setForeground(DemoGUI.TEXT);

        JButton retirar = gui.btn("➖  Retirar", DemoGUI.RED);
        retirar.addActionListener(e -> ejecutar(() -> new DescontarStockComando(ctx.inventario(), actual(prod), n(cant))));
        JButton reponer = gui.btn("➕  Reponer", DemoGUI.GREEN);
        reponer.addActionListener(e -> ejecutar(() -> new ReponerStockComando(ctx.inventario(), actual(prod), n(cant))));
        JButton deshacer = gui.btn("↶  Deshacer", DemoGUI.YELLOW);
        deshacer.addActionListener(e -> { gui.logOk(ctx.comandos().deshacer() ? "COMMAND – deshacer" : "Nada que deshacer"); refrescar(prod); });
        JButton rehacer = gui.btn("↷  Rehacer", DemoGUI.CYAN);
        rehacer.addActionListener(e -> { gui.logOk(ctx.comandos().rehacer() ? "COMMAND – rehacer" : "Nada que rehacer"); refrescar(prod); });
        JButton encolar = gui.btn("📥  Encolar", DemoGUI.ACCENT);
        encolar.addActionListener(e -> {
            ctx.comandos().encolar(new DescontarStockComando(ctx.inventario(), actual(prod), n(cant)));
            gui.logOk("COMMAND – encolado (pendientes: " + ctx.comandos().pendientesEnCola() + ")");
            refrescar(prod);
        });
        JButton procesar = gui.btn("▶  Procesar cola", DemoGUI.ACCENT2);
        procesar.addActionListener(e -> {
            try { gui.logOk("COMMAND – cola procesada: " + ctx.comandos().procesarCola() + " operaciones"); }
            catch (RuntimeException ex) { gui.logFail("COMMAND – " + ex.getMessage()); }
            refrescar(prod);
        });
        prod.addActionListener(e -> refrescar(prod));

        p.add(gui.row(gui.label("Producto:"), prod, gui.label("Cantidad:"), cant, stock));
        separador(p);
        p.add(gui.row(retirar, reponer, deshacer, rehacer, encolar, procesar));
        separador(p);
        p.add(gui.card("Bitacora de auditoria (InvocadorComandos)", scroll(bitacora)));
        refrescar(prod);
        return p;
    }

    private void ejecutar(java.util.function.Supplier<Comando> fabrica) {
        try {
            Comando c = fabrica.get();
            ctx.comandos().ejecutar(c);
            gui.logOk("COMMAND – " + c.descripcion());
        } catch (RuntimeException ex) {
            gui.logFail("COMMAND – " + ex.getMessage());
        }
        refrescar(null);
    }

    private Producto actual(JComboBox<String> prod) { return ctx.producto(SKUS[Math.max(0, prod.getSelectedIndex())]); }
    private int n(JTextField f) { return Integer.parseInt(f.getText().trim()); }

    private int ultimoIndice = 0;

    private void refrescar(JComboBox<String> prod) {
        if (prod != null) ultimoIndice = Math.max(0, prod.getSelectedIndex());
        Producto p = ctx.producto(SKUS[ultimoIndice]);
        stock.setText("   Stock actual: " + p.getStock() + "   |   en cola: " + ctx.comandos().pendientesEnCola());
        java.util.List<String> b = ctx.comandos().getBitacora();
        mostrar(bitacora, b.isEmpty() ? "(sin operaciones)" : String.join("\n", b), DemoGUI.CYAN);
    }
}
