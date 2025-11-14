package edu.ftcc.farmstore.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public final class ModernTheme {

    public static final Color BG = new Color(18, 18, 24);
    public static final Color PANEL = new Color(24, 26, 33);
    public static final Color SURFACE = new Color(32, 35, 42);
    public static final Color CARD = new Color(40, 44, 52);
    public static final Color TEXT = new Color(230, 230, 240);
    public static final Color TEXT_MUTED = new Color(160, 165, 180);
    public static final Color PRIMARY_START = new Color(62, 44, 96);
    public static final Color PRIMARY_MID = new Color(89, 60, 130);
    public static final Color PRIMARY_END = new Color(118, 75, 162);
    public static final Color SELECT_BG = new Color(118, 75, 162, 115);

    // === Apply to frames ===
    public static void apply(JFrame frame) {
        frame.getContentPane().setBackground(BG);
        UIManager.put("Panel.background", PANEL);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Button.background", PRIMARY_END);
        UIManager.put("Button.foreground", TEXT);
        frame.revalidate();
        frame.repaint();
    }

    // === Apply to panels ===
    public static void apply(JPanel panel) {
        panel.setBackground(PANEL);
        for (Component c : panel.getComponents()) {
            if (c instanceof JTable) styleTable((JTable) c);
            if (c instanceof JButton) styleButton((JButton) c);
            if (c instanceof JScrollPane) applyTo((JScrollPane) c);
        }
    }

    // === MISSING METHOD — now added ===
    public static void applyTo(JScrollPane sp) {
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        Component view = sp.getViewport().getView();
        if (view instanceof JTable) {
            styleTable((JTable) view);
        }
    }

    // === OPTIONAL helper ===
    public static void applyTo(JTable table) {
        styleTable(table);
    }

    // === Style buttons ===
    public static void styleButton(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(TEXT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = c.getWidth();
                int h = c.getHeight();
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_START, w, h, PRIMARY_END);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, 12, 12);
                g2.dispose();
                super.paint(g, c);
            }
        });
    }

    // === Style table ===
    public static void styleTable(JTable table) {
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setGridColor(new Color(255, 255, 255, 25));
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(SELECT_BG);
        table.setSelectionForeground(TEXT);

        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_START);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    // === Style window controls ===
    public static void styleWindowControl(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(TEXT);

        Dimension fixed = new Dimension(28, 28);
        btn.setPreferredSize(fixed);
        btn.setMinimumSize(fixed);
        btn.setMaximumSize(fixed);

        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = c.getWidth();
                int h = c.getHeight();
                int d = Math.min(w, h) - 4;
                int x = (w - d) / 2;
                int y = (h - d) / 2;

                ButtonModel m = b.getModel();
                boolean hover = m.isRollover();
                boolean pressed = m.isPressed();

                GradientPaint gp = new GradientPaint(
                    0, 0,
                    hover ? PRIMARY_MID : PRIMARY_START,
                    w, h,
                    hover ? PRIMARY_END : PRIMARY_MID
                );
                g2.setPaint(gp);
                g2.fill(new Ellipse2D.Float(x, y, d, d));

                if (pressed) {
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.setStroke(new BasicStroke(2f));
                    g2.draw(new Ellipse2D.Float(x + 1, y + 1, d - 2, d - 2));
                }

                g2.dispose();
                super.paint(g, c);
            }
        });
    }
}
