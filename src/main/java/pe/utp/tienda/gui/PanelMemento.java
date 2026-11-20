package pe.utp.tienda.gui;

import javax.swing.*;
import java.awt.*;
import pe.utp.tienda.app.ContextoTienda;
import pe.utp.tienda.behavioral.memento.BorradorPedido;
import pe.utp.tienda.behavioral.memento.HistorialBorrador;

/** Memento: el borrador de pedido (carrito) guarda fotos de si mismo; el historial las devuelve sin poder abrirlas. */
class PanelMemento extends PanelPatron {
    private static final String[] SKUS = {"POL-OVR-001", "PAN-CRG-001", "ZAP-URB-001"};
    private BorradorPedido borrador;
    private final HistorialBorrador historial = new HistorialBorrador();
    private final JTextArea vista = salida(5, DemoGUI.CYAN);

    PanelMemento(DemoGUI gui, ContextoTienda ctx) { super(gui, ctx); }

    @Override
    JPanel build() {
        borrador = ctx.ventas().nuevoBorrador("72345678", "Ana Torres");
        JPanel p = base("💾", "Memento", "Deshacer/rehacer sobre el borrador del pedido sin romper su encapsulamiento", DemoGUI.GREEN);
        JComboBox<String> prod = gui.combo("POL-OVR-001", "PAN-CRG-001", "ZAP-URB-001");
        JTextField cant = gui.field("1");
        cant.setPreferredSize(new Dimension(60, 32));
        JTextField dto = gui.field("15");
        dto.setPreferredSize(new Dimension(60, 32));

        JButton agregar = gui.btn("➕  Agregar item", DemoGUI.GREEN);
        agregar.addActionListener(e -> editar("agregar item", () ->
                borrador.agregarItem(ctx.producto(SKUS[prod.getSelectedIndex()]), Integer.parseInt(cant.getText().trim()))));
        JButton quitar = gui.btn("➖  Quitar item", DemoGUI.RED);
        quitar.addActionListener(e -> editar("quitar item", () -> borrador.quitarItem(SKUS[prod.getSelectedIndex()])));
        JButton descuento = gui.btn("🏷  Descuento", DemoGUI.ACCENT);
        descuento.addActionListener(e -> editar("descuento", () -> borrador.descuento(Double.parseDouble(dto.getText().trim()))));
        JButton deshacer = gui.btn("↶  Deshacer", DemoGUI.YELLOW);
        deshacer.addActionListener(e -> { gui.logOk(historial.deshacer(borrador) ? "MEMENTO – deshacer" : "Nada que deshacer"); pintar(); });
        JButton rehacer = gui.btn("↷  Rehacer", DemoGUI.CYAN);
        rehacer.addActionListener(e -> { gui.logOk(historial.rehacer(borrador) ? "MEMENTO – rehacer" : "Nada que rehacer"); pintar(); });
        JButton confirmar = gui.btn("✔  Confirmar", DemoGUI.GREEN);
        confirmar.addActionListener(e -> {
            try {
                gui.logOk("MEMENTO – pedido confirmado, subtotal S/ " + borrador.confirmar().getSubtotal());
            } catch (IllegalStateException ex) {
                gui.logFail("MEMENTO – " + ex.getMessage());
            }
        });

        p.add(gui.row(gui.label("Producto:"), prod, gui.label("Cant:"), cant, gui.label("Dto%:"), dto));
        separador(p);
        p.add(gui.row(agregar, quitar, descuento, deshacer, rehacer, confirmar));
        separador(p);
        p.add(gui.card("Borrador actual", scroll(vista)));
        pintar();
        return p;
    }

    private void editar(String nombre, Runnable cambio) {
        try {
            historial.respaldar(borrador);      // foto ANTES de editar
            cambio.run();
            gui.logOk("MEMENTO – " + nombre + " | " + borrador.resumen());
        } catch (RuntimeException ex) {
            gui.logFail("MEMENTO – " + ex.getMessage());
        }
        pintar();
    }

    private void pintar() {
        mostrar(vista, borrador.resumen() + "\n\nVersiones guardadas para deshacer: " + historial.versionesGuardadas(), DemoGUI.CYAN);
    }
}
