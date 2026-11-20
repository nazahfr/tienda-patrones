package pe.utp.tienda.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import pe.utp.tienda.app.ContextoTienda;

/** Base comun de los paneles de la PC3: evita repetir en cada patron el armado del panel y del area de resultados. */
abstract class PanelPatron {
    protected final DemoGUI gui;
    protected final ContextoTienda ctx;

    protected PanelPatron(DemoGUI gui, ContextoTienda ctx) {
        this.gui = gui;
        this.ctx = ctx;
    }

    abstract JPanel build();

    protected JPanel base(String emoji, String titulo, String subtitulo, Color acento) {
        JPanel p = gui.panelBase();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(gui.header(emoji, titulo, subtitulo, acento));
        return p;
    }

    protected JTextArea salida(int filas, Color color) {
        JTextArea t = new JTextArea(filas, 0);
        t.setBackground(DemoGUI.SURFACE2);
        t.setForeground(color);
        t.setFont(DemoGUI.FONT_MONO);
        t.setEditable(false);
        t.setBorder(new EmptyBorder(8, 12, 8, 12));
        return t;
    }

    protected JComponent scroll(JTextArea t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(null);
        return sp;
    }

    protected void mostrar(JTextArea t, String texto, Color color) {
        t.setText(texto);
        t.setForeground(color);
        t.setCaretPosition(0);
    }

    protected JPanel separador(JPanel p) {
        p.add(Box.createVerticalStrut(12));
        return p;
    }
}
