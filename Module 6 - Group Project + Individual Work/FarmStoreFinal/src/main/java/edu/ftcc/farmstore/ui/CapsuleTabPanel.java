package edu.ftcc.farmstore.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class CapsuleTabPanel extends JPanel {
    private List<String> tabNames = new ArrayList<>();
    private List<JPanel> tabPanels = new ArrayList<>();
    private List<CapsuleButton> buttons = new ArrayList<>();
    private JPanel contentArea;
    private JPanel tabBar;
    private int currentIndex = -1;

    public CapsuleTabPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        tabBar = new TabRail();
        // Let the background gradient show through for a smooth blend from the header
        tabBar.setOpaque(false);
        tabBar.setBorder(BorderFactory.createEmptyBorder(4, 16, 6, 16)); // tuck rail up by 2px

        contentArea = new JPanel(new BorderLayout());
        contentArea.setOpaque(false);
        // Remove drop-shadow entirely
        contentArea.setBorder(null);

        add(tabBar, BorderLayout.NORTH);
        add(contentArea, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (tabBar != null) {
            int railBottom = tabBar.getY() + tabBar.getHeight();
            // Place seam softener a couple pixels below so it doesn't brighten the rail itself
            int seamY = railBottom + 2;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Softer seam veil below the rail to avoid any perceived divide
            GradientPaint gp = new GradientPaint(0, seamY, new Color(20,16,32,38), 0, seamY + 8, new Color(20,16,32,0));
            g2.setPaint(gp);
            g2.fillRect(0, seamY, getWidth(), 8);
            g2.dispose();
        }
    }

    public void addTab(String name, JPanel panel) {
        tabNames.add(name);
        tabPanels.add(panel);

        CapsuleButton btn = new CapsuleButton(name);
        int index = buttons.size();
        btn.addActionListener(e -> selectTab(index));
        buttons.add(btn);

        tabBar.add(btn);
    }

    public void finishTabs() {
        selectTab(0);
    }

    public void selectTab(int index) {
        if (index < 0 || index >= tabPanels.size()) return;
        if (index == currentIndex) return; // no-op if selecting the same tab

        // Update button selection state first (so TabRail can paint accent under the active tab)
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setSelected(i == index);
        }

        JPanel newPanel = tabPanels.get(index);
        contentArea.removeAll();
        contentArea.add(newPanel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
        currentIndex = index;
    }

    private static class TabRail extends JPanel {
        TabRail() {
            setOpaque(false);
            // Slightly tighter vertical gap so pills don't clip and read centered
            setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));
            // Remove hard preferred height; let content dictate but enforce minimum
            setMinimumSize(new Dimension(10, 44));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            // Soft base with subtle vertical gradient (lighter center, darker edges) to match header feather blend
            GradientPaint base = new GradientPaint(0, 0, new Color(14, 10, 26, 215), 0, h, new Color(10, 8, 22, 235));
            g2.setPaint(base);
            g2.fillRect(0, 0, w, h);

            // Very soft top ambient glow (feathered) rather than a sharp highlight
            GradientPaint topGlow = new GradientPaint(0, 0, new Color(118,75,162,24), 0, Math.max(12, h/2), new Color(118,75,162,0));
            g2.setPaint(topGlow);
            g2.fillRect(0, 0, w, Math.max(12, h/2));

            // Remove previous bottom shadow; use faint upward fade that matches header bottom haze (no hard line)
            GradientPaint bottomFeather = new GradientPaint(0, h - 10, new Color(26,18,40,42), 0, h, new Color(26,18,40,0)); // soften alpha from 60 -> 42
            g2.setPaint(bottomFeather);
            g2.fillRect(0, h - 10, w, 10);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class CapsuleButton extends JButton {
        private boolean selected = false;
        private boolean hovered = false;
        private float animProgress = 0f;
        private Timer animTimer;

        public CapsuleButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setPreferredSize(new Dimension(112, 36));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    animateIn();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    animateOut();
                }
            });
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            super.setSelected(selected); // keep model state in sync so parents can query isSelected()
            if (selected) {
                animProgress = 1f;
            }
            repaint();
        }

        private void animateIn() {
            if (animTimer != null) animTimer.stop();
            animTimer = new Timer(15, e -> {
                animProgress = Math.min(1f, animProgress + 0.1f);
                repaint();
                if (animProgress >= 1f) ((Timer) e.getSource()).stop();
            });
            animTimer.start();
        }

        private void animateOut() {
            if (selected) return;
            if (animTimer != null) animTimer.stop();
            animTimer = new Timer(15, e -> {
                animProgress = Math.max(0f, animProgress - 0.1f);
                repaint();
                if (animProgress <= 0f) ((Timer) e.getSource()).stop();
            });
            animTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int arc = h;

            if (selected) {
                // Selected: unified muted violet gradient
                GradientPaint gp = new GradientPaint(0, 0, ModernTheme.PRIMARY_START, w, h, ModernTheme.PRIMARY_END);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
                
                // Glow effect
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.38f));
                g2.setPaint(new GradientPaint(0, 0, ModernTheme.PRIMARY_START, w, h, ModernTheme.PRIMARY_END));
                g2.fill(new RoundRectangle2D.Float(-2, -2, w + 4, h + 4, arc + 4, arc + 4));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                setForeground(Color.WHITE);
            } else {
                // Unselected: glass effect
                Color baseColor = new Color(255, 255, 255, (int) (20 + 25 * animProgress));
                g2.setColor(baseColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

                // Border
                g2.setColor(new Color(255, 255, 255, (int) (40 + 40 * animProgress)));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, arc, arc));

                setForeground(hovered ? Color.WHITE : new Color(200, 200, 220));
            }

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void removeNotify() {
            if (animTimer != null) animTimer.stop();
            super.removeNotify();
        }
    }
}