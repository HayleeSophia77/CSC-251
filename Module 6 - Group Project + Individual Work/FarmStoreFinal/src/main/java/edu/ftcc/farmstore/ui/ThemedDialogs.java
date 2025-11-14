package edu.ftcc.farmstore.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ThemedDialogs {

    public static void showMessageDialog(Component parent, String message, String title) {
        Window w = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        ThemedMessageDialog d = new ThemedMessageDialog(w, message, title);
        d.setVisible(true);
    }

    public static String showInputDialog(Component parent, String prompt) {
        return showInputDialog(parent, prompt, null);
    }

    public static String showInputDialog(Component parent, String prompt, String initialValue) {
        Window w = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        ThemedInputDialog d = new ThemedInputDialog(w, prompt, initialValue);
        d.setVisible(true);
        return d.getValue();
    }

    public static int showConfirmDialog(Component parent, String message, String title, int optionType) {
        Window w = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        ThemedConfirmDialog d = new ThemedConfirmDialog(w, message, title, optionType);
        d.setVisible(true);
        return d.getResult();
    }

    private static class ThemedMessageDialog extends JDialog {
        private ThemedMessageDialog(Window parent, String message, String title) {
            super(parent, ModalityType.APPLICATION_MODAL);
            setUndecorated(true);
            setLayout(new BorderLayout());
            setBackground(new Color(0, 0, 0, 0));
            
            // Slight global translucency for the dialog window
            try { 
                setOpacity(0.92f); 
            } catch (Throwable ignore) { /* opacity not supported */ }
            
            // Apply rounded shape immediately and keep on resize
            addComponentListener(new java.awt.event.ComponentAdapter(){
                @Override 
                public void componentResized(java.awt.event.ComponentEvent e){
                    try { 
                        setShape(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),18,18)); 
                    } catch (Throwable ignore) {}
                }
            });
            try { 
                setShape(new RoundRectangle2D.Double(0,0,1,1,18,18)); 
            } catch (Throwable ignore) {}

            JPanel outer = createDialogPanel();
            JPanel titleBar = createTitleBar(title == null ? "Message" : title);
            JPanel center = createMessagePanel(message);
            JPanel buttons = createButtonPanel();

            // Wrap center + buttons with side padding separate from title bar
            JPanel contentWrap = new JPanel(new BorderLayout());
            contentWrap.setOpaque(false);
            contentWrap.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
            contentWrap.add(center, BorderLayout.CENTER);
            contentWrap.add(buttons, BorderLayout.SOUTH);
            
            outer.add(titleBar, BorderLayout.NORTH);
            outer.add(contentWrap, BorderLayout.CENTER);
            add(outer, BorderLayout.CENTER);

            getRootPane().setDefaultButton((JButton)buttons.getComponent(0));
            pack();
            setMinimumSize(new Dimension(340, 160));
            setLocationRelativeTo(parent);
        }

        private JPanel createDialogPanel() {
            return new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    RoundRectangle2D rr = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 18, 18);
                    g2.setColor(new Color(25, 20, 35, 224));
                    g2.fill(rr);
                    g2.setColor(new Color(120, 100, 255, 80));
                    g2.setStroke(new BasicStroke(2f));
                    g2.draw(rr);
                    g2.dispose();
                }
            };
        }

        private JPanel createTitleBar(String title) {
            JPanel titleBar = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    Color c1 = ModernTheme.PRIMARY_START;
                    Color cMid = ModernTheme.PRIMARY_MID;
                    Color c2 = ModernTheme.PRIMARY_END;
                    Color topLift = new Color(
                        Math.min(255, c1.getRed() + 28),
                        Math.min(255, c1.getGreen() + 28),
                        Math.min(255, c1.getBlue() + 28));
                    LinearGradientPaint lg = new LinearGradientPaint(0, 0, 0, h,
                        new float[]{0f, 0.42f, 1f},
                        new Color[]{topLift, cMid, c2});
                    g2.setPaint(lg);
                    g2.fillRect(0, 0, w, h);
                    GradientPaint feather = new GradientPaint(0, h - 14, new Color(26, 18, 40, 34), 0, h, new Color(26, 18, 40, 0));
                    g2.setPaint(feather);
                    g2.fillRect(0, h - 14, w, 14);
                    g2.dispose();
                }
            };
            
            titleBar.setOpaque(false);
            titleBar.setPreferredSize(new Dimension(300, 40));
            
            JLabel titleLabel = new JLabel(title, JLabel.LEFT);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            titleLabel.setForeground(ModernTheme.TEXT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
            
            JButton closeBtn = new JButton("");
            closeBtn.putClientProperty("control", "close");
            ModernTheme.styleWindowControl(closeBtn);
            closeBtn.setPreferredSize(new Dimension(28, 28));
            closeBtn.addActionListener(e -> dispose());
            closeBtn.setFocusable(false);

            JPanel closeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            closeWrap.setOpaque(false);
            closeWrap.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 10));
            closeWrap.add(closeBtn);
            
            titleBar.add(titleLabel, BorderLayout.CENTER);
            titleBar.add(closeWrap, BorderLayout.EAST);

            enableDragging(titleBar);
            return titleBar;
        }

        private JPanel createMessagePanel(String message) {
            JPanel center = new JPanel(new GridBagLayout());
            center.setBackground(new Color(ModernTheme.PANEL.getRed(), ModernTheme.PANEL.getGreen(), ModernTheme.PANEL.getBlue(), 0));
            center.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
            
            JLabel msgLabel = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
            msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            msgLabel.setForeground(ModernTheme.TEXT);
            msgLabel.setHorizontalAlignment(JLabel.CENTER);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.CENTER;
            
            center.add(msgLabel, gbc);
            return center;
        }

        private JPanel createButtonPanel() {
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
            btns.setOpaque(false);
            btns.setBorder(BorderFactory.createEmptyBorder(12, 0, 18, 0));
            JButton ok = new JButton("OK");
            ModernTheme.styleButton(ok);
            ok.addActionListener(e -> dispose());
            btns.add(ok);
            return btns;
        }

        private void enableDragging(JPanel titleBar) {
            final Point[] mouseDown = {null};
            titleBar.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) { 
                    mouseDown[0] = e.getPoint(); 
                }
            });
            titleBar.addMouseMotionListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (mouseDown[0] != null) {
                        Point p = getLocation();
                        setLocation(p.x + e.getX() - mouseDown[0].x, p.y + e.getY() - mouseDown[0].y);
                    }
                }
            });
        }
    }

    private static class ThemedInputDialog extends JDialog {
        private String value = null;
        private boolean submitted = false;

        private ThemedInputDialog(Window parent, String prompt, String initialValue) {
            super(parent, ModalityType.APPLICATION_MODAL);
            setUndecorated(true);
            setLayout(new BorderLayout());
            setBackground(new Color(0, 0, 0, 0));
            
            // Slight global translucency for the dialog window
            try { 
                setOpacity(0.92f); 
            } catch (Throwable ignore) { /* opacity not supported */ }

            // Apply rounded shape immediately and keep on resize
            addComponentListener(new java.awt.event.ComponentAdapter(){
                @Override 
                public void componentResized(java.awt.event.ComponentEvent e){
                    try { 
                        setShape(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),18,18)); 
                    } catch (Throwable ignore) {}
                }
            });
            try { 
                setShape(new RoundRectangle2D.Double(0,0,1,1,18,18)); 
            } catch (Throwable ignore) {}

            JPanel outer = createDialogPanel();
            JPanel titleBar = createTitleBar();
            JPanel center = createInputPanel(prompt, initialValue);

            outer.add(titleBar, BorderLayout.NORTH);
            outer.add(center, BorderLayout.CENTER);
            add(outer, BorderLayout.CENTER);

            pack();
            setLocationRelativeTo(parent);
        }

        public String getValue() {
            return submitted ? value : null;
        }

        private JPanel createInputPanel(String prompt, String initialValue) {
            JPanel center = new JPanel(new BorderLayout(0, 8));
            center.setBackground(new Color(ModernTheme.PANEL.getRed(), ModernTheme.PANEL.getGreen(), ModernTheme.PANEL.getBlue(), 0));
            center.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
            
            JLabel promptLabel = new JLabel(prompt);
            promptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            promptLabel.setForeground(ModernTheme.TEXT);
            
            RoundedTextField input = new RoundedTextField();
            input.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            input.setText(initialValue == null ? "" : initialValue);
            input.setPreferredSize(new Dimension(220, 36));
            if (initialValue != null) {
                input.setSelectionStart(0);
                input.setSelectionEnd(input.getText().length());
            }

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            btns.setOpaque(false);
            JButton ok = new JButton("OK");
            JButton cancel = new JButton("Cancel");
            ModernTheme.styleButton(ok);
            ModernTheme.styleButton(cancel);
            
            ok.addActionListener(e -> {
                value = input.getText();
                submitted = true;
                dispose();
            });
            cancel.addActionListener(e -> {
                value = null;
                submitted = false;
                dispose();
            });
            
            btns.add(ok);
            btns.add(cancel);

            center.add(promptLabel, BorderLayout.NORTH);
            center.add(input, BorderLayout.CENTER);
            center.add(btns, BorderLayout.SOUTH);

            getRootPane().setDefaultButton(ok);
            return center;
        }

        private JPanel createDialogPanel() {
            JPanel outer = new JPanel(new BorderLayout()) {
                @Override 
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    RoundRectangle2D rr = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 18, 18);
                    g2.setColor(new Color(25,20,35,224));
                    g2.fill(rr);
                    g2.setColor(new Color(120,100,255,80));
                    g2.setStroke(new BasicStroke(2f));
                    g2.draw(rr);
                    g2.dispose();
                }
            };
            outer.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
            outer.setOpaque(false);
            return outer;
        }

        private JPanel createTitleBar() {
            JPanel titleBar = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    Color c1 = ModernTheme.PRIMARY_START;
                    Color cMid = ModernTheme.PRIMARY_MID;
                    Color c2 = ModernTheme.PRIMARY_END;
                    Color topLift = new Color(
                        Math.min(255, c1.getRed() + 28),
                        Math.min(255, c1.getGreen() + 28),
                        Math.min(255, c1.getBlue() + 28));
                    LinearGradientPaint lg = new LinearGradientPaint(0, 0, 0, h,
                        new float[]{0f, 0.42f, 1f},
                        new Color[]{topLift, cMid, c2});
                    g2.setPaint(lg);
                    g2.fillRect(0, 0, w, h);
                    GradientPaint feather = new GradientPaint(0, h - 14, new Color(26, 18, 40, 34), 0, h, new Color(26, 18, 40, 0));
                    g2.setPaint(feather);
                    g2.fillRect(0, h - 14, w, 14);
                    g2.dispose();
                }
            };
            
            titleBar.setOpaque(false);
            titleBar.setPreferredSize(new Dimension(300, 40));
            
            JLabel titleLabel = new JLabel("Input", JLabel.LEFT);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            titleLabel.setForeground(ModernTheme.TEXT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
            
            JButton closeBtn = new JButton("");
            closeBtn.putClientProperty("control", "close");
            ModernTheme.styleWindowControl(closeBtn);
            closeBtn.setPreferredSize(new Dimension(28, 28));
            closeBtn.addActionListener(e -> {
                value = null;
                submitted = false;
                dispose();
            });
            closeBtn.setFocusable(false);

            JPanel closeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            closeWrap.setOpaque(false);
            closeWrap.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 10));
            closeWrap.add(closeBtn);
            
            titleBar.add(titleLabel, BorderLayout.CENTER);
            titleBar.add(closeWrap, BorderLayout.EAST);

            enableDragging(titleBar);
            return titleBar;
        }

        private void enableDragging(JPanel titleBar) {
            final Point[] mouseDown = {null};
            titleBar.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) { 
                    mouseDown[0] = e.getPoint(); 
                }
            });
            titleBar.addMouseMotionListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (mouseDown[0] != null) {
                        Point p = getLocation();
                        setLocation(p.x + e.getX() - mouseDown[0].x, p.y + e.getY() - mouseDown[0].y);
                    }
                }
            });
        }
    }

    private static class ThemedConfirmDialog extends JDialog {
        private int result = -1;

        private ThemedConfirmDialog(Window parent, String message, String title, int optionType) {
            super(parent, ModalityType.APPLICATION_MODAL);
            setUndecorated(true);
            setLayout(new BorderLayout());
            setBackground(new Color(0, 0, 0, 0));
            
            // Slight global translucency for the dialog window
            try { 
                setOpacity(0.92f); 
            } catch (Throwable ignore) { /* opacity not supported */ }

            // Apply rounded shape immediately and keep on resize
            addComponentListener(new java.awt.event.ComponentAdapter(){
                @Override 
                public void componentResized(java.awt.event.ComponentEvent e){
                    try { 
                        setShape(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),18,18)); 
                    } catch (Throwable ignore) {}
                }
            });
            try { 
                setShape(new RoundRectangle2D.Double(0,0,1,1,18,18)); 
            } catch (Throwable ignore) {}

            JPanel outer = createDialogPanel();
            JPanel titleBar = createTitleBar(title == null ? "Confirm" : title);
            JPanel center = createConfirmPanel(message, optionType);

            outer.add(titleBar, BorderLayout.NORTH);
            outer.add(center, BorderLayout.CENTER);
            add(outer, BorderLayout.CENTER);

            pack();
            setLocationRelativeTo(parent);
        }

        public int getResult() {
            return result;
        }

        private JPanel createConfirmPanel(String message, int optionType) {
            JPanel center = new JPanel(new BorderLayout(0, 12));
            center.setBackground(new Color(ModernTheme.PANEL.getRed(), ModernTheme.PANEL.getGreen(), ModernTheme.PANEL.getBlue(), 0));
            center.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
            
            JLabel messageLabel = new JLabel("<html><div style='text-align: center; width: 200px;'>" + message + "</div></html>");
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            messageLabel.setForeground(ModernTheme.TEXT);
            messageLabel.setHorizontalAlignment(JLabel.CENTER);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            btns.setOpaque(false);
            
            if (optionType == javax.swing.JOptionPane.YES_NO_OPTION) {
                JButton yes = new JButton("Yes");
                JButton no = new JButton("No");
                ModernTheme.styleButton(yes);
                ModernTheme.styleButton(no);
                
                yes.addActionListener(e -> { result = javax.swing.JOptionPane.YES_OPTION; dispose(); });
                no.addActionListener(e -> { result = javax.swing.JOptionPane.NO_OPTION; dispose(); });
                
                btns.add(no);
                btns.add(yes);
                getRootPane().setDefaultButton(yes);
            } else {
                JButton ok = new JButton("OK");
                JButton cancel = new JButton("Cancel");
                ModernTheme.styleButton(ok);
                ModernTheme.styleButton(cancel);
                
                ok.addActionListener(e -> { result = javax.swing.JOptionPane.OK_OPTION; dispose(); });
                cancel.addActionListener(e -> { result = javax.swing.JOptionPane.CANCEL_OPTION; dispose(); });
                
                btns.add(cancel);
                btns.add(ok);
                getRootPane().setDefaultButton(ok);
            }

            center.add(messageLabel, BorderLayout.CENTER);
            center.add(btns, BorderLayout.SOUTH);
            return center;
        }

        private JPanel createDialogPanel() {
            JPanel outer = new JPanel(new BorderLayout()) {
                @Override 
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    RoundRectangle2D rr = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 18, 18);
                    g2.setColor(new Color(25,20,35,224));
                    g2.fill(rr);
                    g2.setColor(new Color(120,100,255,80));
                    g2.setStroke(new BasicStroke(2f));
                    g2.draw(rr);
                    g2.dispose();
                }
            };
            outer.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
            outer.setOpaque(false);
            return outer;
        }

        private JPanel createTitleBar(String title) {
            JPanel titleBar = new JPanel(new BorderLayout()) {
                @Override 
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    Color c1 = ModernTheme.PRIMARY_START;
                    Color cMid = ModernTheme.PRIMARY_MID;
                    Color c2 = ModernTheme.PRIMARY_END;
                    Color topLift = new Color(
                            Math.min(255, c1.getRed()+28),
                            Math.min(255, c1.getGreen()+28),
                            Math.min(255, c1.getBlue()+28));
                    LinearGradientPaint lg = new LinearGradientPaint(0,0,0,h,
                        new float[]{0f,0.42f,1f},
                        new Color[]{topLift, cMid, c2});
                    g2.setPaint(lg);
                    g2.fillRect(0,0,w,h);
                    GradientPaint feather = new GradientPaint(0,h-14,new Color(26,18,40,34),0,h,new Color(26,18,40,0));
                    g2.setPaint(feather);
                    g2.fillRect(0,h-14,w,14);
                    g2.dispose();
                }
            };
            titleBar.setOpaque(false);
            titleBar.setPreferredSize(new Dimension(300, 40));
            
            JLabel titleLabel = new JLabel(title, JLabel.LEFT);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            titleLabel.setForeground(ModernTheme.TEXT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
            titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
            
            JButton closeBtn = new JButton("");
            closeBtn.putClientProperty("control", "close");
            ModernTheme.styleWindowControl(closeBtn);
            closeBtn.setPreferredSize(new Dimension(28, 28));
            closeBtn.addActionListener(e -> dispose());
            closeBtn.setFocusable(false);
            
            JPanel closeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            closeWrap.setOpaque(false);
            closeWrap.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 10));
            closeWrap.add(closeBtn);
            
            titleBar.add(titleLabel, BorderLayout.CENTER);
            titleBar.add(closeWrap, BorderLayout.EAST);
            
            // Dragging functionality
            final Point[] mouseDown = {null};
            titleBar.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) { 
                    mouseDown[0] = e.getPoint(); 
                }
            });
            titleBar.addMouseMotionListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (mouseDown[0] != null) {
                        Point p = getLocation();
                        setLocation(p.x + e.getX() - mouseDown[0].x, p.y + e.getY() - mouseDown[0].y);
                    }
                }
            });
            return titleBar;
        }
    }
}