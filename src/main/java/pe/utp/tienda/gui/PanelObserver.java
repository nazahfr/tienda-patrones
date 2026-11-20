package pe.utp.tienda.gui;

import javax.swing.*;
import java.awt.*;
import pe.utp.tienda.app.ContextoTienda;
import pe.utp.tienda.behavioral.state.TransicionInvalidaException;
import pe.utp.tienda.controller.ResultadoVenta;
import pe.utp.tienda.controller.SolicitudVenta;
import pe.utp.tienda.model.Pedido;
import pe.utp.tienda.model.Producto;

/** Observer: una venta o un cambio de estado se publica una vez y lo reciben cliente, auditoria, dashboard y compras. */
class PanelObserver extends PanelPatron {
    private Pedido ultimo;
    private final JTextArea auditoria = salida(5, DemoGUI.CYAN);
    private final JTextArea cliente = salida(5, DemoGUI.GREEN);
    private final JTextArea compras = salida(3, DemoGUI.YELLOW);
    private final JLabel dashboard = new JLabel();

    PanelObserver(DemoGUI gui, ContextoTienda ctx) { super(gui, ctx); }

    @Override
    JPanel build() {
        JPanel p = base("📡", "Observer", "Pedido e InventarioService publican; los suscriptores reaccionan solos", DemoGUI.GREEN);
        JComboBox<String> prod = gui.combo("POL-OVR-001", "PAN-CRG-001", "ZAP-URB-001");
        JTextField cant = gui.field("1");
        cant.setPreferredSize(new Dimension(60, 32));
        JComboBox<String> pago = gui.combo("YAPE", "PLIN", "TARJETA");

        JButton vender = gui.btn("🛒  Vender", DemoGUI.ACCENT);
        vender.addActionListener(e -> {
            try {
                Producto producto = ctx.producto(new String[]{"POL-OVR-001", "PAN-CRG-001", "ZAP-URB-001"}[prod.getSelectedIndex()]);
                ResultadoVenta r = ctx.ventas().registrar(new SolicitudVenta("72345678", "Ana Torres", producto,
                        Integer.parseInt(cant.getText().trim()), 0, 0, (String) pago.getSelectedItem(), "BOLETA"));
                ultimo = r.pedido();
                gui.logSep("OBSERVER – venta");
                gui.logOk("Pedido #" + ultimo.getId() + " " + ultimo.getEstado() + " | stock " + producto.getSku() + ": " + producto.getStock());
            } catch (Exception ex) {
                gui.logFail("OBSERVER – " + ex.getMessage());
            }
            refrescar();
        });

        JButton avanzar = gui.btn("⏭  Avanzar pedido", DemoGUI.CYAN);
        avanzar.addActionListener(e -> {
            if (ultimo == null) { gui.logFail("Primero registra una venta"); return; }
            Runnable[] pasos = { ultimo::preparar, ultimo::enviar, ultimo::entregar };
            boolean avanzo = false;
            for (Runnable paso : pasos) {
                try { paso.run(); avanzo = true; break; } catch (TransicionInvalidaException ignorar) { /* ese paso no aplica en este estado */ }
            }
            gui.logOk(avanzo ? "Pedido #" + ultimo.getId() + " → " + ultimo.getEstado() : "El pedido ya esta en un estado final");
            refrescar();
        });

        dashboard.setFont(DemoGUI.FONT_BOLD);
        dashboard.setForeground(DemoGUI.TEXT);

        p.add(gui.row(gui.label("Producto:"), prod, gui.label("Cant:"), cant, gui.label("Pago:"), pago, vender, avanzar));
        separador(p);
        p.add(gui.card("Observador 1 · Notificador al cliente (bandeja de salida)", scroll(cliente)));
        separador(p);
        p.add(gui.card("Observador 2 · Auditoria de transiciones", scroll(auditoria)));
        separador(p);
        p.add(gui.card("Observador 3 · Dashboard (pedidos que llegaron a cada estado)", dashboard));
        separador(p);
        p.add(gui.card("Observador 4 · Compras (alerta de stock minimo)", scroll(compras)));
        refrescar();
        return p;
    }

    private void refrescar() {
        mostrar(cliente, String.join("\n", ctx.notificaciones().getEnviados()), DemoGUI.GREEN);
        mostrar(auditoria, String.join("\n", ctx.auditoria().getRegistros()), DemoGUI.CYAN);
        mostrar(compras, ctx.compras().getSolicitudes().isEmpty() ? "(sin alertas)" : String.join("\n", ctx.compras().getSolicitudes()), DemoGUI.YELLOW);
        dashboard.setText(ctx.dashboard().resumen());
    }
}
