package edu.ftcc.farmstore.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class GlassmorphismBackground extends JPanel {
    
    public GlassmorphismBackground() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(16, 10, 32));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        // Base gradient
        GradientPaint base = new GradientPaint(
            0, 0, new Color(0x1C1334),
            0, h, new Color(0x100A20)
        );
        g2.setPaint(base);
        g2.fillRect(0, 0, w, h);

        // Soft corner glows
        paintGlow(g2, (int)(w * 0.15), (int)(h * 0.25), 360, new Color(118, 75, 162, 60));
        paintGlow(g2, (int)(w * 0.85), (int)(h * 0.20), 420, new Color(240, 147, 251, 50));

        // Static translucent overlay
        GradientPaint staticOverlay = new GradientPaint(
            0, (int)(h * 0.18), new Color(118, 75, 162, 32),
            w, (int)(h * 0.65), new Color(240, 147, 251, 28)
        );
        g2.setPaint(staticOverlay);
        g2.fillRect(0, 0, w, h);

        // Header blend
        int blendHeight = Math.min(164, (int)(h * 0.40));
        GradientPaint headerBlend = new GradientPaint(
            0, 0, new Color(182, 124, 255, 110),
            0, blendHeight, new Color(26, 21, 37, 0)
        );
        g2.setPaint(headerBlend);
        g2.fillRect(0, 0, w, blendHeight);

        g2.dispose();
    }

    private void paintGlow(Graphics2D g2, int cx, int cy, int size, Color color) {
        RadialGradientPaint glow = new RadialGradientPaint(
            cx, cy, size / 2f,
            new float[]{0f, 1f},
            new Color[]{color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)}
        );
        g2.setPaint(glow);
        g2.fill(new Ellipse2D.Double(cx - size/2, cy - size/2, size, size));
    }
}