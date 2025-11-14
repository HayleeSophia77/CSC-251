package edu.ftcc.farmstore.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

public class ModernHeader extends JPanel {
    private JLabel titleLabel;
    private Point dragOffset;
    private static final boolean PREVIEW_SHEEN = true;
    private static final boolean TOP_HARD_HIGHLIGHT = true;

    public ModernHeader(String text) {
        super();
        setLayout(new BorderLayout());
        setOpaque(true);
        setPreferredSize(new Dimension(10, 54));

        titleLabel = new GlowLabel(text);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        titleLabel.setForeground(Color.WHITE);
        
        Font base = new Font("Segoe UI", Font.BOLD, 25);
        Map<TextAttribute, Object> atts = new HashMap<>();
        atts.put(TextAttribute.TRACKING, -0.01f);
        titleLabel.setFont(base.deriveFont(atts));
        
        add(titleLabel, BorderLayout.WEST);

        // Window control buttons
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setOpaque(false);
        controls.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 16));
        controls.setMaximumSize(new Dimension(70, 36));

        JButton btnMin = new JButton("");
        btnMin.setToolTipText("Minimize");
        btnMin.setFocusable(false);
        btnMin.setPreferredSize(new Dimension(28, 28));
        ModernTheme.styleWindowControl(btnMin);
        btnMin.putClientProperty("control", "min");
        btnMin.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(ModernHeader.this);
            if (w instanceof Frame f) {
                f.setState(Frame.ICONIFIED);
            }
        });
        btnMin.setMargin(new Insets(0, 0, 0, 0));
        btnMin.setRolloverEnabled(true);

        JButton btnClose = new JButton("");
        btnClose.setToolTipText("Close");
        btnClose.setFocusable(false);
        btnClose.setPreferredSize(new Dimension(28, 28));
        ModernTheme.styleWindowControl(btnClose);
        btnClose.putClientProperty("control", "close");
        btnClose.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(ModernHeader.this);
            if (w instanceof JFrame f) {
                f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
            } else if (w != null) {
                w.dispose();
            }
        });
        btnClose.setMargin(new Insets(0, 0, 0, 0));
        btnClose.setRolloverEnabled(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; 
        gbc.anchor = GridBagConstraints.EAST; 
        gbc.insets = new Insets(0, 0, 0, 6);
        controls.add(btnMin, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 1;
        controls.add(btnClose, gbc);
        add(controls, BorderLayout.EAST);

        // Allow dragging the window
        MouseAdapter dragger = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragOffset = e.getPoint();
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                Window w = SwingUtilities.getWindowAncestor(ModernHeader.this);
                if (w != null && dragOffset != null && (w instanceof Frame)) {
                    Point p = w.getLocation();
                    w.setLocation(p.x + e.getX() - dragOffset.x, p.y + e.getY() - dragOffset.y);
                }
            }
        };
        
        addMouseListener(dragger);
        addMouseMotionListener(dragger);
        titleLabel.addMouseListener(dragger);
        titleLabel.addMouseMotionListener(dragger);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        // Multi-stop gradient matching original exactly
        Color c1 = ModernTheme.PRIMARY_START;
        Color cMid = ModernTheme.PRIMARY_MID;
        Color c2 = ModernTheme.PRIMARY_END;
        Color topLift = new Color(
            Math.min(255, c1.getRed() + 28),
            Math.min(255, c1.getGreen() + 28),
            Math.min(255, c1.getBlue() + 28)
        );
        
        LinearGradientPaint lg = new LinearGradientPaint(0, 0, 0, h,
            new float[]{0f, 0.42f, 1f},
            new Color[]{topLift, cMid, c2});
        g2.setPaint(lg);
        g2.fillRect(0, 0, w, h);

        // Optional sheen effect
        if (PREVIEW_SHEEN) {
            int sheenHeight = (int)(h * 0.25);
            LinearGradientPaint sheen = new LinearGradientPaint(0, 0, 0, sheenHeight,
                new float[]{0f, 0.5f, 1f},
                new Color[]{new Color(255,255,255,60), new Color(255,255,255,30), new Color(255,255,255,0)});
            g2.setPaint(sheen);
            g2.fillRect(0, 2, w, sheenHeight);
        }

        // Soft vignette shading
        int cx = w / 2; 
        int cy = (int)(h * 0.40); 
        int radius = Math.max(w, h);
        float[] vignetteStops = {0f, 0.85f, 1f};
        Color edgeMid = new Color(0,0,0,10);
        Color edgeMax = new Color(0,0,0,20);
        Color[] cols = {new Color(0,0,0,0), edgeMid, edgeMax};
        RadialGradientPaint vignette = new RadialGradientPaint(new Point(cx, cy), radius, vignetteStops, cols, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        
        Shape prev = g2.getClip();
        int excludeTop = 10;
        g2.setClip(new Rectangle(0, excludeTop, w, Math.max(0, h - excludeTop)));
        g2.setPaint(vignette);
        g2.fillRect(0, excludeTop, w, Math.max(0, h - excludeTop));
        g2.setClip(prev);

        // Controlled top highlight
        int softFeatherH = Math.min(13,h);
        LinearGradientPaint softTopFeather = new LinearGradientPaint(0, TOP_HARD_HIGHLIGHT ? 1 : 0, 0, softFeatherH,
            new float[]{0f,1f},
            new Color[]{new Color(255,255,255,40), new Color(255,255,255,0)});
        g2.setPaint(softTopFeather);
        g2.fillRect(0, TOP_HARD_HIGHLIGHT ? 1 : 0, w, softFeatherH - (TOP_HARD_HIGHLIGHT ? 1 : 0));

        // Extended fade at bottom
        int featherH = Math.min(72, h);
        LinearGradientPaint blend = new LinearGradientPaint(0, h - featherH, 0, h,
            new float[]{0f, 0.55f, 1f},
            new Color[]{new Color(26,18,40,40), new Color(18,12,30,18), new Color(18,12,30,0)});
        g2.setPaint(blend);
        g2.fillRect(0, h - featherH, w, featherH);

        // Micro highlight above bottom
        if (!PREVIEW_SHEEN) {
            int hlY = h - featherH + 8;
            int hlH = 14;
            LinearGradientPaint hl = new LinearGradientPaint(0, hlY, 0, hlY + hlH,
                new float[]{0f, 1f},
                new Color[]{new Color(255,255,255,14), new Color(255,255,255,0)});
            g2.setPaint(hl);
            g2.fillRect(0, hlY, w, hlH);
        }

        g2.setClip(null);
        g2.dispose();
    }

    public static class GlowLabel extends JLabel {
        public GlowLabel(String text) { 
            super(text); 
            setOpaque(false); 
        }
        
        @Override 
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

            String s = getText();
            if (s == null || s.isEmpty()) { 
                g2.dispose(); 
                return; 
            }

            Font font = getFont();
            FontMetrics fm = g2.getFontMetrics(font);
            int x = getInsets().left;
            int baseline = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

            // Build precise glyph outline positioned at (x, baseline)
            java.awt.font.FontRenderContext frc = g2.getFontRenderContext();
            java.awt.font.GlyphVector gv = font.createGlyphVector(frc, s);
            java.awt.Shape textShape = gv.getOutline(x, baseline);

            // 1) Micro drop shadow (down 1px only)
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
            g2.setColor(Color.BLACK);
            AffineTransform shift = AffineTransform.getTranslateInstance(0, 1);
            g2.fill(shift.createTransformedShape(textShape));

            // 2) Tight gradient fill (almost solid near-white)
            g2.setComposite(AlphaComposite.SrcOver);
            float topY = baseline - fm.getAscent();
            float botY = baseline + fm.getDescent();
            LinearGradientPaint fill = new LinearGradientPaint(
                0, topY, 0, botY,
                new float[]{0f, 1f},
                new Color[]{
                    new Color(252,250,255),        // near-white
                    new Color(236,238,246)         // tiny hint of cool tone
                }
            );
            g2.setPaint(fill);
            g2.fill(textShape);

            // 3) Single hairline stroke for definition
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(255,255,255,150));
            g2.draw(textShape);

            // 4) Optional micro bottom edge
            Shape prevClip2 = g2.getClip();
            g2.setClip(textShape);
            LinearGradientPaint bottomEdge = new LinearGradientPaint(
                0, botY - Math.max(3, fm.getDescent() * 0.6f), 0, botY,
                new float[]{0f, 1f},
                new Color[]{new Color(0,0,0,0), new Color(0,0,0,22)}
            );
            g2.setPaint(bottomEdge);
            g2.fillRect(0, (int)(botY - Math.max(3, fm.getDescent() * 0.6f)), getWidth(), (int)Math.max(3, fm.getDescent() * 0.6f));
            g2.setClip(prevClip2);

            g2.dispose();
        }
    }
}