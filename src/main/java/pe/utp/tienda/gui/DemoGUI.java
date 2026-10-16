package pe.utp.tienda.gui;

import pe.utp.tienda.creational.abstractfactory.PasarelaPagoFactory;
import pe.utp.tienda.creational.factory.ComprobanteFactory;
import pe.utp.tienda.creational.singleton.ConfiguracionApp;
import pe.utp.tienda.creational.singleton.ConexionBD;
import pe.utp.tienda.model.*;
import pe.utp.tienda.service.InventarioService;
import pe.utp.tienda.service.NotificacionService;
import pe.utp.tienda.structural.adapter.ReniecAdapter;
import pe.utp.tienda.structural.adapter.ReniecApiExterna;
import pe.utp.tienda.structural.composite.Categoria;
import pe.utp.tienda.structural.decorator.*;
import pe.utp.tienda.structural.facade.VentaFacade;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Interfaz gráfica Swing que demuestra todos los patrones de diseño
 * del proyecto usando las clases Java reales (no simulación).
 */
public class DemoGUI extends JFrame {

    // ── Paleta de colores ─────────────────────────────────
    static final Color BG         = new Color(10, 12, 20);
    static final Color SURFACE    = new Color(17, 24, 39);
    static final Color SURFACE2   = new Color(26, 34, 54);
    static final Color BORDER     = new Color(31, 45, 69);
    static final Color ACCENT     = new Color(99, 102, 241);
    static final Color ACCENT2    = new Color(139, 92, 246);
    static final Color CYAN       = new Color(6, 182, 212);
    static final Color GREEN      = new Color(16, 185, 129);
    static final Color RED        = new Color(239, 68, 68);
    static final Color YELLOW     = new Color(245, 158, 11);
    static final Color TEXT       = new Color(226, 232, 240);
    static final Color TEXT2      = new Color(148, 163, 184);
    static final Color TEXT3      = new Color(100, 116, 139);

    // Fuentes
    static Font FONT_TITLE, FONT_BODY, FONT_MONO, FONT_SMALL, FONT_BOLD;

    // Estado compartido
    private final Polera      polera    = new Polera("POL-OVR-001", "Polera Oversize Negra", "M", 59.0, 25);
    private final Pantalon    pantalon  = new Pantalon("PAN-CRG-001", "Pantalone Cargo Beige", "32", 79.0, 15);
    private final Zapatilla   zapatilla = new Zapatilla("ZAP-URB-001", "Zapatillas Urbanas Blancas", "40", 148.0, 10);
    private final Cliente     clienteAna = new Cliente("72345678", "Ana", "Torres");
    private final VentaFacade facade    = new VentaFacade(
            new ReniecAdapter(new ReniecApiExterna()),
            new InventarioService(),
            new NotificacionService());

    private JTextArea consoleArea;
    private CardLayout cardLayout;
    private JPanel     cardsPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DemoGUI gui = new DemoGUI();
            gui.setVisible(true);
        });
    }

    public DemoGUI() {
        initFonts();
        setTitle("Tienda de Ropa – Demo Patrones de Diseño · UTP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        JPanel header     = buildHeader();
        JPanel console    = buildConsole();
        JSplitPane body   = buildBody();

        add(header,  BorderLayout.NORTH);
        add(body,    BorderLayout.CENTER);
        add(console, BorderLayout.SOUTH);
    }

    // ── Fuentes ───────────────────────────────────────────
    private void initFonts() {
        FONT_TITLE = new Font("Segoe UI", Font.BOLD,   20);
        FONT_BOLD  = new Font("Segoe UI", Font.BOLD,   13);
        FONT_BODY  = new Font("Segoe UI", Font.PLAIN,  13);
        FONT_MONO  = new Font("Consolas", Font.PLAIN,  12);
        FONT_SMALL = new Font("Segoe UI", Font.PLAIN,  11);
    }

    // ── HEADER ────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new GradientPanel(new Color(15, 23, 42), new Color(30, 27, 75));
        h.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 12));
        h.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));

        JLabel icon = new JLabel("🛍️");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        h.add(icon);

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 2));
        titles.setOpaque(false);
        JLabel t1 = new JLabel("Tienda de Ropa – Patrones de Diseño");
        t1.setFont(FONT_TITLE); t1.setForeground(TEXT);
        JLabel t2 = new JLabel("Demostración interactiva · UTP · Semanas 6–9");
        t2.setFont(FONT_SMALL); t2.setForeground(TEXT2);
        titles.add(t1); titles.add(t2);
        h.add(titles);

        h.add(Box.createHorizontalGlue());
        h.add(pill("Creacionales ×5", new Color(99, 102, 241, 60)));
        h.add(pill("Estructurales ×4", new Color(6, 182, 212, 60)));
        return h;
    }

    private JLabel pill(String text, Color bg) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 999, 999);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(TEXT);
        l.setOpaque(false);
        l.setBorder(new EmptyBorder(4, 12, 4, 12));
        return l;
    }

    // ── BODY (sidebar + cards) ────────────────────────────
    private JSplitPane buildBody() {
        cardLayout  = new CardLayout();
        cardsPanel  = new JPanel(cardLayout);
        cardsPanel.setBackground(BG);

        // Agregar paneles
        cardsPanel.add(buildSingletonPanel(),       "singleton");
        cardsPanel.add(buildPrototypePanel(),        "prototype");
        cardsPanel.add(buildBuilderPanel(),          "builder");
        cardsPanel.add(buildFactoryPanel(),          "factory");
        cardsPanel.add(buildAbstractFactoryPanel(),  "abstractfactory");
        cardsPanel.add(buildAdapterPanel(),          "adapter");
        cardsPanel.add(buildFacadePanel(),           "facade");
        cardsPanel.add(buildDecoratorPanel(),        "decorator");
        cardsPanel.add(buildCompositePanel(),        "composite");

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildSidebar(), new JScrollPane(cardsPanel));
        split.setDividerLocation(230);
        split.setDividerSize(1);
        split.setBorder(null);
        split.getLeftComponent().setBackground(SURFACE);
        ((JScrollPane) split.getRightComponent()).setBorder(null);
        ((JScrollPane) split.getRightComponent()).getViewport().setBackground(BG);
        return split;
    }

    // ── SIDEBAR ───────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(SURFACE);
        side.setBorder(new MatteBorder(0, 0, 0, 1, BORDER));

        side.add(sideTitle("CREACIONALES"));
        String[][] creacional = {
            {"singleton",       "🔒", "Singleton",        "C1"},
            {"prototype",       "🧬", "Prototype",         "C2"},
            {"builder",         "🔧", "Builder",           "C3"},
            {"factory",         "🏭", "Factory Method",    "C4"},
            {"abstractfactory", "🏗", "Abstract Factory",  "C5"}
        };
        ButtonGroup bg = new ButtonGroup();
        JToggleButton first = null;
        for (String[] d : creacional) {
            JToggleButton btn = sideBtn(d[0], d[1], d[2], d[3], ACCENT);
            bg.add(btn);
            side.add(btn);
            if (first == null) { first = btn; first.setSelected(true); }
        }

        side.add(sideTitle("ESTRUCTURALES"));
        String[][] estructural = {
            {"adapter",   "🔌", "Adapter",  "E1"},
            {"facade",    "🪟", "Facade",   "E2"},
            {"decorator", "🎀", "Decorator","E3"},
            {"composite", "🌳", "Composite","E4"}
        };
        for (String[] d : estructural) {
            JToggleButton btn = sideBtn(d[0], d[1], d[2], d[3], CYAN);
            bg.add(btn);
            side.add(btn);
        }
        side.add(Box.createVerticalGlue());
        return side;
    }

    private JLabel sideTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(TEXT3);
        l.setBorder(new EmptyBorder(14, 16, 4, 16));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JToggleButton sideBtn(String card, String emoji, String name, String tag, Color tagColor) {
        JToggleButton btn = new JToggleButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 40));
                    g2.fillRoundRect(4, 2, getWidth()-8, getHeight()-4, 10, 10);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 12));
                    g2.fillRoundRect(4, 2, getWidth()-8, getHeight()-4, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(230, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emojiL = new JLabel(emoji);
        emojiL.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btn.add(emojiL);

        JLabel nameL = new JLabel(name);
        nameL.setFont(FONT_BODY);
        nameL.setForeground(TEXT2);
        btn.add(nameL);

        JLabel tagL = new JLabel(tag) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(tagColor.getRed(), tagColor.getGreen(), tagColor.getBlue(), 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 999, 999);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tagL.setFont(new Font("Segoe UI", Font.BOLD, 9));
        tagL.setForeground(tagColor);
        tagL.setOpaque(false);
        tagL.setBorder(new EmptyBorder(2, 6, 2, 6));
        btn.add(tagL);

        btn.addActionListener(e -> {
            cardLayout.show(cardsPanel, card);
            nameL.setForeground(TEXT);
        });
        btn.addChangeListener(e -> nameL.setForeground(btn.isSelected() ? TEXT : TEXT2));
        return btn;
    }

    // ── CONSOLA INFERIOR ──────────────────────────────────
    private JPanel buildConsole() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(4, 7, 14));
        p.setBorder(new MatteBorder(1, 0, 0, 0, BORDER));
        p.setPreferredSize(new Dimension(0, 170));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        header.setBackground(SURFACE2);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        for (Color c : new Color[]{RED, YELLOW, GREEN})
            header.add(dot(c));
        JLabel lbl = new JLabel("Sistema de Salida – Patrones de Diseño");
        lbl.setFont(FONT_MONO); lbl.setForeground(TEXT3);
        header.add(lbl);

        JButton clear = smallBtn("Limpiar", SURFACE2);
        clear.addActionListener(e -> consoleArea.setText(""));
        header.add(Box.createHorizontalGlue());
        header.add(clear);
        p.add(header, BorderLayout.NORTH);

        consoleArea = new JTextArea();
        consoleArea.setBackground(new Color(4, 7, 14));
        consoleArea.setForeground(CYAN);
        consoleArea.setFont(FONT_MONO);
        consoleArea.setEditable(false);
        consoleArea.setBorder(new EmptyBorder(8, 14, 8, 14));
        consoleArea.setCaretColor(GREEN);
        JScrollPane sp = new JScrollPane(consoleArea);
        sp.setBorder(null);
        sp.getViewport().setBackground(new Color(4, 7, 14));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JLabel dot(Color c) {
        JLabel l = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.fillOval(0, 0, 12, 12);
                g2.dispose();
            }
        };
        l.setPreferredSize(new Dimension(12, 12));
        return l;
    }

    // ══════════════════════════════════════════════════════
    //  UTILIDADES DE UI
    // ══════════════════════════════════════════════════════

    private JPanel card(String title, JComponent body) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 16, 14, 16));
        if (title != null) {
            JLabel lbl = new JLabel(title);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(TEXT3);
            lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
            p.add(lbl, BorderLayout.NORTH);
        }
        p.add(body, BorderLayout.CENTER);
        return p;
    }

    private JButton btn(String text, Color base) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()
                        ? base.darker()
                        : getModel().isRollover() ? base.brighter() : base;
                g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(FONT_BOLD);
        b.setForeground(Color.WHITE);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        return b;
    }

    private JButton smallBtn(String text, Color base) {
        JButton b = btn(text, base);
        b.setFont(FONT_SMALL);
        b.setBorder(new EmptyBorder(4, 10, 4, 10));
        return b;
    }

    private JTextField field(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setBackground(SURFACE2);
        tf.setForeground(TEXT);
        tf.setCaretColor(TEXT);
        tf.setFont(FONT_BODY);
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        return tf;
    }

    private JComboBox<String> combo(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(SURFACE2);
        cb.setForeground(TEXT);
        cb.setFont(FONT_BODY);
        ((JLabel) cb.getRenderer()).setForeground(TEXT);
        return cb;
    }

    private JLabel kv(String key, String value, Color valueColor) {
        JLabel l = new JLabel("<html><span style='color:gray'>" + key + "</span>  <b>" + value + "</b></html>");
        l.setForeground(valueColor);
        l.setFont(FONT_BODY);
        return l;
    }

    private void log(String msg) {
        String ts = new SimpleDateFormat("HH:mm:ss").format(new Date());
        if (consoleArea != null) {
            consoleArea.append("[" + ts + "] " + msg + "\n");
            consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
        } else {
            System.out.println("[" + ts + "] " + msg);
        }
    }

    private void logOk(String msg)   { log("✔  " + msg); }
    private void logFail(String msg) { log("✘  " + msg); }
    private void logSep(String msg)  { log("─── " + msg + " ───────────────────────"); }

    private JPanel panelBase() {
        JPanel p = new JPanel();
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(24, 28, 24, 28));
        return p;
    }

    private JPanel row(Component... cs) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        for (Component c : cs) p.add(c);
        return p;
    }

    private JLabel header(String emoji, String title, String subtitle, Color iconBg) {
        JLabel lbl = new JLabel("<html><span style='font-size:18px'>" + emoji +
                "</span>  <b style='font-size:15px'>" + title + "</b>" +
                "<br><span style='color:#94a3b8;font-size:11px'>" + subtitle + "</span></html>");
        lbl.setForeground(TEXT);
        lbl.setFont(FONT_BODY);
        lbl.setBorder(new EmptyBorder(0, 0, 18, 0));
        return lbl;
    }

    // ══════════════════════════════════════════════════════
    //  1. SINGLETON
    // ══════════════════════════════════════════════════════
    private JPanel buildSingletonPanel() {
        JPanel p = panelBase();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(header("🔒", "Singleton",
                "ConfiguracionApp & ConexionBD – Initialization-on-demand holder (lazy + thread-safe)",
                ACCENT));

        // Info cards
        JPanel cards = new JPanel(new GridLayout(1, 3, 12, 0));
        cards.setOpaque(false);
        ConfiguracionApp cfg = ConfiguracionApp.getInstancia();
        cards.add(infoCard("Tienda", cfg.getNombreTienda(), TEXT));
        cards.add(infoCard("IGV",    String.format("%.0f%%", cfg.getIgv() * 100), ACCENT));
        cards.add(infoCard("Moneda", cfg.getMoneda(), CYAN));
        p.add(cards);
        p.add(Box.createVerticalStrut(16));

        JTextArea hashArea = new JTextArea(3, 50);
        hashArea.setBackground(SURFACE2);
        hashArea.setForeground(GREEN);
        hashArea.setFont(FONT_MONO);
        hashArea.setEditable(false);
        hashArea.setBorder(new EmptyBorder(8, 12, 8, 12));

        JButton verBtn = btn("🔁  Solicitar getInstancia() ×2", ACCENT);
        verBtn.addActionListener(e -> {
            ConfiguracionApp a = ConfiguracionApp.getInstancia();
            ConfiguracionApp b = ConfiguracionApp.getInstancia();
            ConexionBD       c = ConexionBD.getInstancia();
            ConexionBD       d = ConexionBD.getInstancia();
            logSep("SINGLETON");
            logOk("ConfiguracionApp.getInstancia() == ConfiguracionApp.getInstancia()  →  " + (a == b));
            logOk("ConexionBD.getInstancia()       == ConexionBD.getInstancia()        →  " + (c == d));
            log("    hashCode ConfiguracionApp: " + System.identityHashCode(a));
            log("    hashCode ConexionBD:       " + System.identityHashCode(c));
            log("    URL BD: " + c.getUrl());
            hashArea.setText(
                "ConfiguracionApp @" + Integer.toHexString(System.identityHashCode(a)) + "\n" +
                "ConfiguracionApp @" + Integer.toHexString(System.identityHashCode(b)) + "  ← mismo objeto\n" +
                "mismaInstancia? " + (a == b));
        });
        p.add(row(verBtn));
        p.add(Box.createVerticalStrut(10));
        p.add(card("Verificación de identidad de objetos (System.identityHashCode)", hashArea));
        return p;
    }

    private JPanel infoCard(String key, String value, Color accent) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel k = new JLabel(key.toUpperCase());
        k.setFont(new Font("Segoe UI", Font.BOLD, 10));
        k.setForeground(TEXT3);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 22));
        v.setForeground(accent);
        p.add(k, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════
    //  2. PROTOTYPE
    // ══════════════════════════════════════════════════════
    private JPanel buildPrototypePanel() {
        JPanel p = panelBase();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(header("🧬", "Prototype",
                "clonarVariante() crea nuevas tallas sin repetir datos; Pedido.clone() hace deep-copy de items",
                CYAN));

        JComboBox<String> prod = combo("Polera Oversize M – S/ 59", "Pantalón Cargo 32 – S/ 79", "Zapatillas 40 – S/ 148");
        JComboBox<String> talla = combo("S", "L", "XL", "34", "38", "42");
        JTextField stockF = field("8");
        stockF.setPreferredSize(new Dimension(80, 32));

        JTextArea result = new JTextArea(5, 0);
        result.setBackground(SURFACE2); result.setForeground(GREEN);
        result.setFont(FONT_MONO); result.setEditable(false);
        result.setBorder(new EmptyBorder(8, 12, 8, 12));

        JButton clonarBtn = btn("🧬  clonarVariante()", CYAN);
        clonarBtn.addActionListener(e -> {
            Producto base = switch (prod.getSelectedIndex()) {
                case 1  -> pantalon;
                case 2  -> zapatilla;
                default -> polera;
            };
            String newTalla = (String) talla.getSelectedItem();
            int newStock;
            try { newStock = Integer.parseInt(stockF.getText().trim()); }
            catch (NumberFormatException ex) { newStock = 5; }

            java.util.regex.Matcher skuMatcher = java.util.regex.Pattern.compile("(\\d+)$").matcher(base.getSku());
            String newSku = skuMatcher.find()
                    ? base.getSku().substring(0, skuMatcher.start()) + String.format("%03d", Integer.parseInt(skuMatcher.group()) + 1)
                    : base.getSku() + "-2";

            Producto clon = base.clonarVariante(newSku, newTalla, newStock);

            logSep("PROTOTYPE – clonarVariante()");
            logOk("base.clone() → super.clone() (copia superficial OK para String/primitivos)");
            logOk("clon != base                 →  " + (clon != base));
            logOk("clon.getSku()    = " + clon.getSku() + "  (nuevo)");
            logOk("clon.getTalla()  = " + clon.getTalla() + "  (nueva)");
            logOk("base.getTalla()  = " + base.getTalla() + "  (sin modificar)");

            result.setText(
                "ORIGINAL  SKU=" + base.getSku() + "  talla=" + base.getTalla() + "  stock=" + base.getStock() + "\n" +
                "CLON      SKU=" + clon.getSku() + "  talla=" + clon.getTalla() + "  stock=" + clon.getStock() + "\n" +
                "clon != original  →  " + (clon != base) + "\n" +
                "SKUs distintos    →  " + (!base.getSku().equals(clon.getSku())) + "\n" +
                "Precio heredado   →  S/ " + clon.getPrecio());
        });

        // Pedido clone
        JButton pedidoBtn = btn("📋  Pedido.clone() (deep copy)", ACCENT2);
        pedidoBtn.addActionListener(e -> {
            Pedido orig  = new Pedido.Builder(clienteAna).agregarItem(polera, 2).agregarItem(pantalon, 1).build();
            Pedido copia = orig.clone();
            logSep("PROTOTYPE – Pedido.clone()");
            logOk("orig.getItems() != copia.getItems()  →  " + (orig.getItems() != copia.getItems()));
            logOk("copia.getItems().size()              →  " + copia.getItems().size());
            logOk("copia.getEstado()                    →  " + copia.getEstado());
            result.setText(result.getText() + "\n─── Pedido deep-copy ───\n" +
                "items distintos?  " + (orig.getItems() != copia.getItems()) +
                "\ncopia.estado: " + copia.getEstado());
        });

        p.add(row(new JLabel("Producto: ") {{ setForeground(TEXT2); setFont(FONT_BODY); }}, prod,
                  new JLabel("  Nueva talla: ") {{ setForeground(TEXT2); setFont(FONT_BODY); }}, talla,
                  new JLabel("  Stock: ") {{ setForeground(TEXT2); setFont(FONT_BODY); }}, stockF,
                  clonarBtn, pedidoBtn));
        p.add(Box.createVerticalStrut(12));
        p.add(card("Resultado del prototipo", result));
        return p;
    }

    // ══════════════════════════════════════════════════════
    //  3. BUILDER
    // ══════════════════════════════════════════════════════
    private JPanel buildBuilderPanel() {
        JPanel p = panelBase();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(header("🔧", "Builder",
                "Pedido.Builder – construcción paso a paso con validación en build()", YELLOW));

        JTextField dniF    = field("72345678");
        JTextField nomF    = field("Ana Torres");
        JTextField descF   = field("10");
        JTextField envioF  = field("12.00");
        JTextField notasF  = field("Entrega en horario de tarde");
        JComboBox<String> prodCB = combo("Polera M – S/59 (stock "+polera.getStock()+")",
                                         "Pantalón 32 – S/79 (stock "+pantalon.getStock()+")",
                                         "Zapatilla 40 – S/148 (stock "+zapatilla.getStock()+")");
        JTextField cantF = field("2");
        cantF.setPreferredSize(new Dimension(60, 32));

        JTextArea result = new JTextArea(6, 0);
        result.setBackground(SURFACE2); result.setForeground(CYAN);
        result.setFont(FONT_MONO); result.setEditable(false);
        result.setBorder(new EmptyBorder(8, 12, 8, 12));

        JButton buildBtn = btn("⚙  build()", ACCENT);
        buildBtn.addActionListener(e -> {
            try {
                String[] parts = nomF.getText().trim().split(" ", 2);
                Cliente cl = new Cliente(dniF.getText().trim(), parts[0], parts.length > 1 ? parts[1] : "");
                Producto prod2 = switch (prodCB.getSelectedIndex()) {
                    case 1  -> pantalon;
                    case 2  -> zapatilla;
                    default -> polera;
                };
                int cant = Integer.parseInt(cantF.getText().trim());
                double desc = Double.parseDouble(descF.getText().trim());
                double envio = Double.parseDouble(envioF.getText().trim());

                Pedido pedido = new Pedido.Builder(cl)
                        .agregarItem(prod2, cant)
                        .descuento(desc)
                        .envio(envio)
                        .notas(notasF.getText().trim())
                        .build();

                logSep("BUILDER – build()");
                logOk("new Pedido.Builder(cliente).agregarItem().descuento().envio().notas().build()");
                logOk("items: " + pedido.getItems().size() + "  subtotal: S/ " + String.format("%.2f", pedido.getSubtotal()));
                logOk("descuento: " + pedido.getDescuentoPct() + "%  envío: S/ " + pedido.getCostoEnvio());
                logOk("estado: " + pedido.getEstado());

                result.setText(
                    "PEDIDO CONSTRUIDO OK\n" +
                    "Cliente:    " + cl.getNombreCompleto() + " (DNI " + cl.getDni() + ")\n" +
                    "Items:      " + pedido.getItems().size() + " producto(s)\n" +
                    "Subtotal:   S/ " + String.format("%.2f", pedido.getSubtotal()) + "\n" +
                    "Descuento:  " + pedido.getDescuentoPct() + "%\n" +
                    "Envío:      S/ " + pedido.getCostoEnvio() + "\n" +
                    "Notas:      " + pedido.getNotas() + "\n" +
                    "Estado:     " + pedido.getEstado());
            } catch (Exception ex) {
                logFail("BUILD FALLIDO: " + ex.getMessage());
                result.setText("ERROR: " + ex.getMessage());
            }
        });

        JButton errBtn = btn("💥  build() sin items", RED);
        errBtn.addActionListener(e -> {
            try {
                new Pedido.Builder(clienteAna).build();
            } catch (IllegalStateException ex) {
                logSep("BUILDER – Validación");
                logFail("IllegalStateException: " + ex.getMessage());
                result.setText("Builder rechazó el pedido:\n→ " + ex.getMessage());
            }
        });

        JPanel campos = new JPanel(new GridLayout(3, 1, 0, 8));
        campos.setOpaque(false);
        campos.add(row(label("DNI:"), dniF, label("  Nombre:"), nomF));
        campos.add(row(label("Descuento %:"), descF, label("  Envío S/:"), envioF));
        campos.add(row(label("Producto:"), prodCB, label("  Cant:"), cantF, label("  Notas:"), notasF));
        p.add(campos);
        p.add(Box.createVerticalStrut(12));
        p.add(row(buildBtn, errBtn));
        p.add(Box.createVerticalStrut(12));
        p.add(card("Resultado del Builder", result));
        return p;
    }

    // ══════════════════════════════════════════════════════
    //  4. FACTORY METHOD
    // ══════════════════════════════════════════════════════
    private JPanel buildFactoryPanel() {
        JPanel p = panelBase();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(header("🏭", "Factory Method",
                "ComprobanteFactory.de(tipo) – subclase decide qué Comprobante concreto instanciar", GREEN));

        JComboBox<String> tipoCB = combo("BOLETA", "FACTURA");
        JTextField montoF = field("185.50");
        montoF.setPreferredSize(new Dimension(120, 32));

        JTextArea result = new JTextArea(8, 0);
        result.setBackground(SURFACE2); result.setForeground(YELLOW);
        result.setFont(FONT_MONO); result.setEditable(false);
        result.setBorder(new EmptyBorder(8, 12, 8, 12));

        JButton emitBtn = btn("📄  emitir()", GREEN);
        emitBtn.addActionListener(e -> {
            try {
                String tipo  = (String) tipoCB.getSelectedItem();
                double monto = Double.parseDouble(montoF.getText().trim());
                Pedido pedido = new Pedido.Builder(clienteAna)
                        .agregarItem(polera, 2).build();
                String comprobante = ComprobanteFactory.de(tipo).emitir(pedido, monto);
                logSep("FACTORY METHOD – " + tipo);
                logOk("ComprobanteFactory.de(\"" + tipo + "\") → new " +
                        (tipo.equals("BOLETA") ? "BoletaFactory" : "FacturaFactory") + "()");
                logOk("emitir(pedido, " + monto + ") → " + comprobante.split("\n")[0]);
                result.setText(comprobante);
            } catch (Exception ex) {
                logFail(ex.getMessage());
            }
        });

        p.add(row(label("Tipo comprobante:"), tipoCB, label("  Monto S/:"), montoF, emitBtn));
        p.add(Box.createVerticalStrut(12));
        p.add(card("Comprobante generado", result));
        return p;
    }

    // ══════════════════════════════════════════════════════
    //  5. ABSTRACT FACTORY
    // ══════════════════════════════════════════════════════
    private JPanel buildAbstractFactoryPanel() {
        JPanel p = panelBase();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(header("🏗", "Abstract Factory",
                "PasarelaPagoFactory.para(metodo) – familia coherente MetodoPago + ReciboPago", new Color(236,72,153)));

        JComboBox<String> pasarelaCB = combo("YAPE  (límite S/ 500)", "PLIN  (límite S/ 600)", "TARJETA  (sin límite)");
        JTextField montoF = field("250.00");
        montoF.setPreferredSize(new Dimension(120, 32));

        JTextArea result = new JTextArea(6, 0);
        result.setBackground(SURFACE2); result.setForeground(CYAN);
        result.setFont(FONT_MONO); result.setEditable(false);
        result.setBorder(new EmptyBorder(8, 12, 8, 12));

        JButton cobrarBtn = btn("💰  cobrar()", ACCENT2);
        cobrarBtn.addActionListener(e -> {
            try {
                String[] nombres = {"YAPE","PLIN","TARJETA"};
                String nombre = nombres[pasarelaCB.getSelectedIndex()];
                double monto  = Double.parseDouble(montoF.getText().trim());

                PasarelaPagoFactory factory = PasarelaPagoFactory.para(nombre);
                var metodoPago = factory.crearMetodoPago();
                var recibo     = factory.crearRecibo();
                boolean ok     = metodoPago.cobrar(monto);

                logSep("ABSTRACT FACTORY – " + nombre);
                logOk("PasarelaPagoFactory.para(\"" + nombre + "\") → " + factory.getClass().getSimpleName());
                logOk("factory.crearMetodoPago()  → " + metodoPago.getClass().getSimpleName());
                logOk("factory.crearRecibo()      → " + recibo.getClass().getSimpleName());
                logOk("metodoPago.cobrar(S/ " + monto + ") → " + ok);
                if (ok) logOk("Recibo: " + recibo.generar(monto));
                else logFail("Pago rechazado: monto supera límite de " + nombre);

                result.setText(
                    "Pasarela:   " + nombre + "\n" +
                    "Factory:    " + factory.getClass().getSimpleName() + "\n" +
                    "MetodoPago: " + metodoPago.getClass().getSimpleName() + "\n" +
                    "Recibo:     " + recibo.getClass().getSimpleName() + "\n" +
                    "cobrar(S/ " + monto + ")  →  " + ok + "\n" +
                    (ok ? recibo.generar(monto) : "PAGO RECHAZADO por límite"));
            } catch (Exception ex) {
                logFail(ex.getMessage());
            }
        });

        p.add(row(label("Pasarela:"), pasarelaCB, label("  Monto S/:"), montoF, cobrarBtn));
        p.add(Box.createVerticalStrut(12));
        p.add(card("Resultado Abstract Factory", result));
        return p;
    }

    // ══════════════════════════════════════════════════════
    //  6. ADAPTER
    // ══════════════════════════════════════════════════════
    private JPanel buildAdapterPanel() {
        JPanel p = panelBase();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(header("🔌", "Adapter",
                "ReniecAdapter adapta ReniecApiExterna(int) → ValidadorDocumento(String)", YELLOW));

        // Diagrama de flujo
        JPanel flow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        flow.setOpaque(false);
        flow.add(flowBox("Sistema\nValidadorDocumento\nvalidarDni(String)", BORDER));
        flow.add(arrow());
        flow.add(flowBox("ADAPTER\nReniecAdapter\nString → int", ACCENT));
        flow.add(arrow());
        flow.add(flowBox("API Externa\nReniecApiExterna\nverificar(int)", BORDER));
        p.add(flow);
        p.add(Box.createVerticalStrut(14));

        JTextField dniF = field("72345678");
        dniF.setPreferredSize(new Dimension(180, 32));

        JTextArea result = new JTextArea(5, 0);
        result.setBackground(SURFACE2); result.setForeground(GREEN);
        result.setFont(FONT_MONO); result.setEditable(false);
        result.setBorder(new EmptyBorder(8, 12, 8, 12));

        JButton validarBtn = btn("🔍  validarDni()", YELLOW);
        validarBtn.addActionListener(e -> {
            String dni = dniF.getText().trim();
            ReniecAdapter adapter = new ReniecAdapter(new ReniecApiExterna());
            boolean ok = adapter.validarDni(dni);

            logSep("ADAPTER – validarDni(\"" + dni + "\")");
            log("    ReniecAdapter.validarDni(\"" + dni + "\")");
            log("    [Adapter] convierte String \"" + dni + "\" → int " + parseSafe(dni));
            log("    [API]     ReniecApiExterna.verificar(" + parseSafe(dni) + ")  → " + ok);
            if (ok) logOk("DNI " + dni + " VÁLIDO → cliente habilitado");
            else    logFail("DNI \"" + dni + "\" INVÁLIDO → IllegalArgumentException");

            result.setText(
                "validarDni(\"" + dni + "\")\n" +
                "  [Adapter] String → int " + parseSafe(dni) + "\n" +
                "  [RENIEC]  verificar() → " + ok + "\n\n" +
                (ok ? "✔  DNI VÁLIDO" : "✘  DNI INVÁLIDO (necesita exactamente 8 dígitos)"));
            result.setForeground(ok ? GREEN : RED);
        });

        p.add(row(label("DNI (String):"), dniF, validarBtn));
        p.add(Box.createVerticalStrut(12));
        p.add(card("Resultado del Adapter", result));
        return p;
    }

    private int parseSafe(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    private JPanel flowBox(String text, Color border) {
        JTextArea ta = new JTextArea(text);
        ta.setBackground(SURFACE2); ta.setForeground(TEXT2);
        ta.setFont(FONT_MONO); ta.setEditable(false);
        ta.setBorder(new CompoundBorder(new LineBorder(border, 1, true), new EmptyBorder(8, 12, 8, 12)));
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(ta);
        return wrap;
    }

    private JLabel arrow() {
        JLabel l = new JLabel("  →  ");
        l.setForeground(ACCENT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        return l;
    }

    // ══════════════════════════════════════════════════════
    //  7. FACADE
    // ══════════════════════════════════════════════════════
    private JPanel buildFacadePanel() {
        JPanel p = panelBase();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(header("🪟", "Facade",
                "VentaFacade.registrarVenta() orquesta 7 subsistemas con una sola llamada", CYAN));

        JTextField dniF  = field("72345678");
        JTextField nomF  = field("Ana Torres");
        JComboBox<String> prodCB = combo(
                "Polera M – S/59", "Pantalón 32 – S/79", "Zapatilla 40 – S/148");
        JTextField cantF = field("2");
        cantF.setPreferredSize(new Dimension(60, 32));
        JComboBox<String> pagoCB  = combo("YAPE", "PLIN", "TARJETA");
        JComboBox<String> compCB  = combo("BOLETA", "FACTURA");
        JTextField descF  = field("10");
        JTextField envioF = field("12");

        JTextArea result = new JTextArea(8, 0);
        result.setBackground(SURFACE2); result.setForeground(CYAN);
        result.setFont(FONT_MONO); result.setEditable(false);
        result.setBorder(new EmptyBorder(8, 12, 8, 12));

        JButton ventaBtn = btn("🚀  registrarVenta()", ACCENT);
        ventaBtn.addActionListener(e -> {
            try {
                String[] parts = nomF.getText().trim().split(" ", 2);
                Cliente cl = new Cliente(dniF.getText().trim(), parts[0], parts.length > 1 ? parts[1] : "");
                Producto prod2 = switch (prodCB.getSelectedIndex()) {
                    case 1 -> pantalon; case 2 -> zapatilla; default -> polera;
                };
                int    cant    = Integer.parseInt(cantF.getText().trim());
                double desc    = Double.parseDouble(descF.getText().trim());
                double envio   = Double.parseDouble(envioF.getText().trim());
                String metodo  = (String) pagoCB.getSelectedItem();
                String tipoComp= (String) compCB.getSelectedItem();

                Pedido pedido = new Pedido.Builder(cl)
                        .agregarItem(prod2, cant)
                        .descuento(desc).envio(envio)
                        .build();

                logSep("FACADE – registrarVenta()");
                String res = facade.registrarVenta(pedido, metodo, tipoComp);
                logOk("Venta completada. Estado: " + pedido.getEstado());
                logOk("Stock " + prod2.getNombre() + ": " + prod2.getStock());
                result.setText(res + "\nEstado del pedido: " + pedido.getEstado());
                result.setForeground(GREEN);
            } catch (Exception ex) {
                logFail("FACADE rechazó: " + ex.getMessage());
                result.setText("RECHAZADO:\n" + ex.getMessage());
                result.setForeground(RED);
            }
        });

        JButton errBtn = btn("💥  DNI inválido", RED);
        errBtn.addActionListener(e -> {
            try {
                Cliente malo = new Cliente("123", "Pedro", "Gomez");
                Pedido pedido = new Pedido.Builder(malo).agregarItem(zapatilla, 1).build();
                facade.registrarVenta(pedido, "TARJETA", "BOLETA");
            } catch (Exception ex) {
                logSep("FACADE – DNI inválido");
                logFail(ex.getMessage());
                result.setText("FACADE RECHAZÓ:\n→ " + ex.getMessage());
                result.setForeground(RED);
            }
        });

        JPanel campos = new JPanel(new GridLayout(2, 1, 0, 8));
        campos.setOpaque(false);
        campos.add(row(label("DNI:"), dniF, label("Nombre:"), nomF,
                       label("Producto:"), prodCB, label("Cant:"), cantF));
        campos.add(row(label("Pago:"), pagoCB, label("Comprobante:"), compCB,
                       label("Dto%:"), descF, label("Envío S/:"), envioF));
        p.add(campos);
        p.add(Box.createVerticalStrut(10));
        p.add(row(ventaBtn, errBtn));
        p.add(Box.createVerticalStrut(12));
        p.add(card("Resultado Facade + todos los patrones integrados", result));
        return p;
    }

    // ══════════════════════════════════════════════════════
    //  8. DECORATOR
    // ══════════════════════════════════════════════════════
    private JPanel buildDecoratorPanel() {
        JPanel p = panelBase();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(header("🎀", "Decorator",
                "PrecioBase → ConDescuento → ConIgv → ConEnvio – capas dinámicas sin modificar la base", new Color(236,72,153)));

        JTextField baseF = field("100.00");
        baseF.setPreferredSize(new Dimension(100, 32));
        JTextField descF  = field("10");
        descF.setPreferredSize(new Dimension(60, 32));
        JTextField envioF = field("12");
        envioF.setPreferredSize(new Dimension(80, 32));

        JCheckBox useDesc  = new JCheckBox("ConDescuento"); styleCheck(useDesc, true);
        JCheckBox useIgv   = new JCheckBox("ConIgv (18%)"); styleCheck(useIgv, true);
        JCheckBox useEnvio = new JCheckBox("ConEnvio");     styleCheck(useEnvio, true);

        JLabel totalLbl = new JLabel("S/ ---");
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        totalLbl.setForeground(ACCENT);

        JTextArea cadena = new JTextArea(3, 0);
        cadena.setBackground(SURFACE2); cadena.setForeground(CYAN);
        cadena.setFont(FONT_MONO); cadena.setEditable(false);
        cadena.setBorder(new EmptyBorder(8, 12, 8, 12));

        Runnable calc = () -> {
            try {
                double base    = Double.parseDouble(baseF.getText().trim());
                double descPct = Double.parseDouble(descF.getText().trim());
                double envio   = Double.parseDouble(envioF.getText().trim());

                CalculoPrecio precio = new PrecioBase(base);
                StringBuilder cadenaStr = new StringBuilder("PrecioBase(" + String.format("S/ %.2f", base) + ")");

                if (useDesc.isSelected()) {
                    precio = new ConDescuento(precio, descPct);
                    cadenaStr.append("\n  → ConDescuento(-" + descPct + "%)");
                }
                double afterDesc = precio.calcular();

                if (useIgv.isSelected()) {
                    precio = new ConIgv(precio);
                    cadenaStr.append("\n  → ConIgv(+18%)");
                }
                double afterIgv = precio.calcular();

                if (useEnvio.isSelected()) {
                    precio = new ConEnvio(precio, envio);
                    cadenaStr.append("\n  → ConEnvio(+S/ " + envio + ")");
                }
                double total = precio.calcular();
                cadenaStr.append("\n  = S/ " + String.format("%.2f", total));

                totalLbl.setText(String.format("S/ %.2f", total));
                cadena.setText(cadenaStr.toString());

                logSep("DECORATOR");
                logOk(precio.detalle() + " = S/ " + String.format("%.2f", total));
            } catch (NumberFormatException ex) { /* ignorar */ }
        };

        JButton calcBtn = btn("🎀  calcular()", new Color(236,72,153));
        calcBtn.addActionListener(e -> calc.run());
        ActionListener recalc = e -> calc.run();
        useDesc.addActionListener(recalc);
        useIgv.addActionListener(recalc);
        useEnvio.addActionListener(recalc);

        p.add(row(label("Precio base S/:"), baseF, label("  Descuento %:"), descF,
                  label("  Envío S/:"), envioF, calcBtn));
        p.add(Box.createVerticalStrut(8));
        p.add(row(useDesc, useIgv, useEnvio));
        p.add(Box.createVerticalStrut(14));
        p.add(row(label("TOTAL:  "), totalLbl));
        p.add(Box.createVerticalStrut(12));
        p.add(card("Cadena de decoradores", cadena));
        calc.run();
        return p;
    }

    private void styleCheck(JCheckBox cb, boolean sel) {
        cb.setSelected(sel);
        cb.setBackground(SURFACE2);
        cb.setForeground(TEXT);
        cb.setFont(FONT_BODY);
        cb.setFocusPainted(false);
        cb.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(6, 10, 6, 10)));
    }

    // ══════════════════════════════════════════════════════
    //  9. COMPOSITE
    // ══════════════════════════════════════════════════════
    private JPanel buildCompositePanel() {
        JPanel p = panelBase();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(header("🌳", "Composite",
                "Categoria (composite) + Producto (hoja) implementan ComponenteCatalogo – stock recursivo", GREEN));

        // Árbol de categorías con datos reales
        Categoria tienda = new Categoria("Tienda");
        Categoria poleras = new Categoria("Poleras");
        Categoria pantalones = new Categoria("Pantalones");
        Categoria zapatillas = new Categoria("Zapatillas");

        poleras.agregar(polera);
        Producto poleraL = polera.clonarVariante("POL-OVR-002", "L", 12);
        poleras.agregar(poleraL);

        pantalones.agregar(pantalon);
        Producto pantalon34 = pantalon.clonarVariante("PAN-CRG-002", "34", 8);
        pantalones.agregar(pantalon34);

        zapatillas.agregar(zapatilla);
        Producto zap42 = zapatilla.clonarVariante("ZAP-URB-002", "42", 6);
        zapatillas.agregar(zap42);

        tienda.agregar(poleras);
        tienda.agregar(pantalones);
        tienda.agregar(zapatillas);

        JTextArea arbol = new JTextArea(12, 0);
        arbol.setBackground(SURFACE2); arbol.setForeground(YELLOW);
        arbol.setFont(FONT_MONO); arbol.setEditable(false);
        arbol.setBorder(new EmptyBorder(8, 12, 8, 12));

        StringBuilder sb = new StringBuilder();
        sb.append("[🌳] Tienda\n");
        sb.append("  stock total  = ").append(tienda.getStockTotal()).append("\n");
        sb.append("  valor total  = S/ ").append(String.format("%.2f", tienda.getPrecioTotal())).append("\n\n");

        for (Categoria cat : new Categoria[]{poleras, pantalones, zapatillas}) {
            sb.append("  [+] ").append(cat.getNombre())
              .append("  stock=").append(cat.getStockTotal())
              .append("  valor=S/").append(String.format("%.2f", cat.getPrecioTotal())).append("\n");
        }

        // Capturar mostrar() redirigiendo System.out
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream old = System.out;
        System.setOut(new java.io.PrintStream(baos));
        tienda.mostrar("");
        System.setOut(old);
        arbol.setText(baos.toString());

        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setOpaque(false);
        stats.add(infoCard("Stock total",   String.valueOf(tienda.getStockTotal()), GREEN));
        stats.add(infoCard("Valor total",   String.format("S/ %.0f", tienda.getPrecioTotal()), ACCENT));
        stats.add(infoCard("Nodos árbol",   "7", CYAN));
        p.add(stats);
        p.add(Box.createVerticalStrut(14));
        p.add(card("Árbol de catálogo – mostrar() recursivo", arbol));
        logSep("COMPOSITE");
        logOk("tienda.getStockTotal() (recursivo) → " + tienda.getStockTotal());
        logOk("tienda.getPrecioTotal() (recursivo) → S/ " + String.format("%.2f", tienda.getPrecioTotal()));
        return p;
    }

    // ── Helpers ───────────────────────────────────────────
    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT2);
        l.setFont(FONT_BODY);
        return l;
    }

    // ── Panel degradado ───────────────────────────────────
    static class GradientPanel extends JPanel {
        final Color c1, c2;
        GradientPanel(Color c1, Color c2) { this.c1 = c1; this.c2 = c2; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0,0,c1,getWidth(),0,c2));
            g2.fillRect(0,0,getWidth(),getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
