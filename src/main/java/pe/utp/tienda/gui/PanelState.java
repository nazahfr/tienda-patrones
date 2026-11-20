package pe.utp.tienda.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import pe.utp.tienda.app.ContextoTienda;
import pe.utp.tienda.behavioral.state.TransicionInvalidaException;
import pe.utp.tienda.controller.SolicitudVenta;
import pe.utp.tienda.model.Pedido;

/** State: el pedido cambia de comportamiento segun su estado; las acciones invalidas las rechaza el propio estado. */
class PanelState extends PanelPatron {
    private static final List<String> ESTADOS = List.of("Pendiente", "Pagado", "En Preparacion", "Enviado", "Entregado", "Cancelado");
    private Pedido pedido;
    private final JPanel pildoras = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    private final JTextArea resultado = salida(7, DemoGUI.CYAN);

    PanelState(DemoGUI gui, ContextoTienda ctx) { super(gui, ctx); }

    @Override
    JPanel build() {
        JPanel p = base("🚦", "State", "Cada accion se delega en el EstadoPedido actual, sin if/switch", DemoGUI.GREEN);
        pildoras.setOpaque(false);
        nuevoPedido();

        JButton nuevo = gui.btn("🆕  Nuevo pedido", DemoGUI.ACCENT);
        nuevo.addActionListener(e -> { nuevoPedido(); gui.logSep("STATE – nuevo pedido"); gui.logOk("Estado inicial: " + pedido.getEstado()); });

        JPanel tarjeta = gui.card("Ciclo de vida del pedido (el estado actual esta resaltado)", pildoras);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        p.add(tarjeta);
        separador(p);
        p.add(gui.row(nuevo,
                accion("💳  pagar()", DemoGUI.GREEN, () -> pedido.pagar(), "pagar"),
                accion("📦  preparar()", DemoGUI.CYAN, () -> pedido.preparar(), "preparar"),
                accion("🚚  enviar()", DemoGUI.CYAN, () -> pedido.enviar(), "enviar"),
                accion("🏠  entregar()", DemoGUI.GREEN, () -> pedido.entregar(), "entregar"),
                accion("✖  cancelar()", DemoGUI.RED, () -> pedido.cancelar(), "cancelar")));
        separador(p);
        p.add(gui.card("Resultado de la ultima accion", scroll(resultado)));
        return p;
    }

    private JButton accion(String texto, Color color, Runnable r, String nombre) {
        JButton b = gui.btn(texto, color);
        b.addActionListener(e -> {
            String antes = pedido.getEstado();
            try {
                r.run();
                gui.logOk("STATE – " + nombre + "(): " + antes + " → " + pedido.getEstado());
                mostrar(resultado, nombre + "() permitido\n" + antes + "  →  " + pedido.getEstado(), DemoGUI.GREEN);
            } catch (TransicionInvalidaException ex) {
                gui.logFail("STATE rechazó: " + ex.getMessage());
                mostrar(resultado, "TRANSICION RECHAZADA\n" + ex.getMessage() + "\n(el estado '" + antes + "' no define " + nombre + "())", DemoGUI.RED);
            }
            pintar();
        });
        return b;
    }

    private void nuevoPedido() {
        pedido = ctx.ventas().crearPedido(new SolicitudVenta("72345678", "Ana Torres", ctx.producto("POL-OVR-001"), 1, 0, 0, "YAPE", "BOLETA"));
        pedido.suscribir(ev -> gui.logOk("Observer ← " + ev.estadoAnterior() + " → " + ev.estadoNuevo()));
        pintar();
    }

    private void pintar() {
        pildoras.removeAll();
        for (String e : ESTADOS) {
            boolean actual = e.equals(pedido.getEstado());
            JLabel l = new JLabel(e, SwingConstants.CENTER);
            l.setOpaque(true);
            l.setFont(DemoGUI.FONT_BOLD);
            l.setBorder(new EmptyBorder(6, 12, 6, 12));
            l.setBackground(actual ? ("Cancelado".equals(e) ? DemoGUI.RED : DemoGUI.GREEN) : DemoGUI.SURFACE2);
            l.setForeground(actual ? Color.WHITE : DemoGUI.TEXT3);
            pildoras.add(l);
        }
        pildoras.revalidate();
        pildoras.repaint();
    }
}
