package edu.ftcc.farmstore.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedTextField extends JTextField {
    private String placeholder = "";
    
    public RoundedTextField() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(ModernTheme.SURFACE);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

        // Lighten border slightly for more legible interior foreground contrast
        g2.setColor(new Color(140, 150, 170, 110));
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));

        g2.dispose();

        // Ensure actual input text is bright for readability
        setForeground(new Color(235, 240, 250));
        super.paintComponent(g);

        if (getText().isEmpty() && !placeholder.isEmpty()) {
            Graphics2D g3 = (Graphics2D) g.create();
            g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Lighten placeholder a bit for readability
            g3.setColor(new Color(250, 252, 255, 160));
            g3.setFont(getFont());
            FontMetrics fm = g3.getFontMetrics();
            int x = getInsets().left + 5;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g3.drawString(placeholder, x, y);
            g3.dispose();
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Don't paint border - we handle it in paintComponent
    }
}