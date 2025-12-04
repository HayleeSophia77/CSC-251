import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;

public class WeddingRentalGUI extends JFrame {

    private JRadioButton tableRadio;
    private JRadioButton chairRadio;
    private JTextField quantityField;
    private JComboBox<String> seatingBox;
    private JButton calculateButton;
    private JButton clearButton;
    private JLabel resultLabel;
    
    // For dragging the window
    private Point mouseDownCompCoords;

    // iOS-inspired color palette 🍎
    private final Color softBlue = new Color(180, 210, 230);
    private final Color backgroundBlue = new Color(200, 220, 240);
    private final Color mutedMint = new Color(200, 230, 220);
    private final Color softLavender = new Color(210, 210, 235);
    private final Color paleBlue = new Color(190, 220, 240);
    private final Color mediumBlue = new Color(100, 150, 180);
    private final Color darkBlue = new Color(70, 100, 130);
    private final Color buttonBlue = new Color(120, 170, 210);
    private final Color buttonBlueDark = new Color(140, 190, 230);
    private final Color softPink = new Color(230, 180, 190);
    private final Color softPinkLight = new Color(240, 200, 210);
    private final Color titleBarBlue = new Color(170, 200, 225);

    public WeddingRentalGUI() {
        // Apply FlatLaf Arc theme
        try {
            UIManager.setLookAndFeel(new FlatArcIJTheme());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // FlatLaf customizations
        UIManager.put("Button.arc", 20);
        UIManager.put("Component.arc", 15);
        UIManager.put("TextComponent.arc", 15);

        // Frame setup - UNDECORATED for custom iOS look!
        setTitle("Wedding Rental Cost Estimator");
        setSize(750, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true); // Remove default title bar
        
        // Make window rounded by making it non-opaque
        setBackground(new Color(0, 0, 0, 0));

        // Main container with rounded corners
        JPanel mainContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Outer shadow for depth
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 30, 30);
                
                // Main rounded background with gradient - FILL WITH PROPER INSETS
                GradientPaint gradient = new GradientPaint(
                    0, 0, backgroundBlue,
                    0, getHeight(), new Color(210, 225, 245)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(3, 3, getWidth() - 11, getHeight() - 11, 30, 30);
            }
        };
        mainContainer.setOpaque(false);
        mainContainer.setLayout(new BorderLayout());
        mainContainer.setBorder(BorderFactory.createEmptyBorder(3, 3, 8, 8));

        // Custom iOS-style title bar
        JPanel titleBar = createIOSTitleBar();
        mainContainer.add(titleBar, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 25));

        // Add cute header with emoji
        JLabel headerLabel = new JLabel("💍 Wedding Rental Calculator 💐", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setForeground(darkBlue);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        contentPanel.add(headerLabel);

        // Item Type Panel
        JPanel typePanel = createCutePanel("🎯 Select Item Type", softBlue);
        typePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));

        tableRadio = createCuteRadioButton("🪑 Tables");
        chairRadio = createCuteRadioButton("💺 Chairs");

        ButtonGroup group = new ButtonGroup();
        group.add(tableRadio);
        group.add(chairRadio);

        tableRadio.addActionListener(e -> seatingBox.setEnabled(false));
        chairRadio.addActionListener(e -> seatingBox.setEnabled(true));

        typePanel.add(tableRadio);
        typePanel.add(chairRadio);
        contentPanel.add(typePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Quantity Panel
        JPanel quantityPanel = createCutePanel("🔢 Enter Quantity", mutedMint);
        quantityPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        quantityField = new JTextField(15);
        quantityField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        quantityField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(15, mediumBlue, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        quantityPanel.add(quantityField);
        contentPanel.add(quantityPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Seating Options Panel
        JPanel seatingPanel = createCutePanel("🎪 Choose Seating Option", softLavender);
        seatingPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        String[] seatingOptions = {
                "💑 Bride & Groom – 10 chairs",
                "👨‍👩‍👧‍👦 Family Section – 9 chairs",
                "🅰️ Side Section A – 8 chairs",
                "🅱️ Side Section B – 7 chairs",
                "©️ Side Section C – 6 chairs",
                "🅾️ Side Section D – 5 chairs",
                "🔷 Side Section E – 4 chairs",
                "🔶 Side Section F – 3 chairs",
                "💃 Dance Floor Section – 40 chairs",
                "📐 Small Rectangle – 2 tables",
                "📏 Medium Rectangle – 2 tables",
                "🏢 Large Layout Table Cluster – 2 tables"
        };

        seatingBox = new JComboBox<>(seatingOptions);
        seatingBox.setEnabled(false);
        seatingBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        seatingBox.setPreferredSize(new Dimension(350, 40));
        seatingBox.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(15, mediumBlue, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        seatingPanel.add(seatingBox);
        contentPanel.add(seatingPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        calculateButton = createGradientButton("✨ Calculate Cost", buttonBlue, buttonBlueDark);
        clearButton = createGradientButton("🔄 Clear/New Estimate", softPink, softPinkLight);

        calculateButton.addActionListener(e -> calculateCost());
        clearButton.addActionListener(e -> clearFields());

        buttonPanel.add(calculateButton);
        buttonPanel.add(clearButton);
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Result Panel
        JPanel resultPanel = createCutePanel("💰 Result", paleBlue);
        resultPanel.setPreferredSize(new Dimension(650, 100));
        resultPanel.setMaximumSize(new Dimension(650, 100));

        resultLabel = new JLabel("Total Cost: ", SwingConstants.CENTER);
        resultLabel.setForeground(darkBlue);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultPanel.add(resultLabel);
        contentPanel.add(resultPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Footer
        JLabel footer = new JLabel("✨ Created by Haylee Paredes ✨", SwingConstants.CENTER);
        footer.setForeground(darkBlue);
        footer.setFont(new Font("SansSerif", Font.ITALIC, 13));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(footer);

        mainContainer.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainContainer);

        // Make window exist but keep it hidden initially
        setVisible(false);
        
        // Show welcome message FIRST
        showCuteWelcomeDialog();

        // NOW show the main window after dialog is closed
        setVisible(true);
    }

    // Create iOS-style title bar with window controls
    private JPanel createIOSTitleBar() {
        JPanel titleBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent title bar background with rounded top only
                g2d.setColor(new Color(titleBarBlue.getRed(), titleBarBlue.getGreen(), 
                                       titleBarBlue.getBlue(), 200));
                // Create a rounded rectangle for the top
                RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + 20, 30, 30);
                g2d.fill(roundedRect);
            }
        };
        titleBar.setOpaque(false);
        titleBar.setLayout(new BorderLayout());
        titleBar.setPreferredSize(new Dimension(750, 45));
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        // Add window dragging capability
        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords = e.getPoint();
            }
        });
        
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            }
        });

        // Left side - iOS-style window control buttons
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        controlsPanel.setOpaque(false);
        
        // Close button (red)
        JButton closeBtn = createIOSButton(new Color(255, 95, 87), "×");
        closeBtn.addActionListener(e -> System.exit(0));
        
        // Minimize button (yellow)
        JButton minimizeBtn = createIOSButton(new Color(255, 189, 46), "−");
        minimizeBtn.addActionListener(e -> setState(JFrame.ICONIFIED));
        
        // Maximize button (green) - we'll just make it a visual element
        JButton maximizeBtn = createIOSButton(new Color(40, 201, 64), "□");
        maximizeBtn.addActionListener(e -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.NORMAL);
            } else {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
        
        controlsPanel.add(closeBtn);
        controlsPanel.add(minimizeBtn);
        controlsPanel.add(maximizeBtn);

        // Center - Title
        JLabel titleLabel = new JLabel("💕 Wedding Rental Cost Estimator");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(darkBlue);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        titleBar.add(controlsPanel, BorderLayout.WEST);
        titleBar.add(titleLabel, BorderLayout.CENTER);

        return titleBar;
    }

    // Create iOS-style circular control button
    private JButton createIOSButton(Color color, String symbol) {
        JButton button = new JButton(symbol) {
            private boolean isHovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circle
                g2d.setColor(color);
                g2d.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
                
                // Draw symbol only on hover
                if (isHovered) {
                    g2d.setColor(new Color(0, 0, 0, 180));
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2d.drawString(getText(), x, y - 1);
                }
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // No border
            }
        };
        
        button.setPreferredSize(new Dimension(14, 14));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.putClientProperty("hovered", true);
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.putClientProperty("hovered", false);
                button.repaint();
            }
        });
        
        button.addPropertyChangeListener("hovered", evt -> {
            button.putClientProperty("isHovered", evt.getNewValue());
            button.repaint();
        });
        
        return button;
    }

    private JPanel createCutePanel(String title, Color backgroundColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Drop shadow
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 20, 20);
                
                // Main background
                g2d.setColor(new Color(backgroundColor.getRed(), backgroundColor.getGreen(), 
                                       backgroundColor.getBlue(), 250));
                g2d.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
                
                // Border
                g2d.setColor(mediumBlue);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
            }
        };
        
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            new TitledBorder(null, title, TitledBorder.CENTER, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 15), darkBlue)
        ));
        
        return panel;
    }

    private JRadioButton createCuteRadioButton(String text) {
        JRadioButton radio = new JRadioButton(text);
        radio.setFont(new Font("SansSerif", Font.BOLD, 16));
        radio.setForeground(darkBlue);
        radio.setOpaque(false);
        radio.setFocusPainted(false);
        
        radio.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                radio.setFont(new Font("SansSerif", Font.BOLD, 18));
                radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                radio.setFont(new Font("SansSerif", Font.BOLD, 16));
            }
        });
        
        return radio;
    }

    private JButton createGradientButton(String text, Color color1, Color color2) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color c1 = isHovered ? color2 : color1;
                Color c2 = isHovered ? color1 : color2;
                
                // Drop shadow
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, 20, 20);
                
                // Gradient fill
                GradientPaint gradient = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
                
                // Border
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 6, getHeight() - 6, 20, 20);
                
                // Text
                FontMetrics fm = g2d.getFontMetrics();
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
            }
        };
        
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(190, 45));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JButton)e.getSource()).putClientProperty("hovered", true);
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                ((JButton)e.getSource()).putClientProperty("hovered", false);
                button.repaint();
            }
        });
        
        button.addPropertyChangeListener("hovered", evt -> {
            button.putClientProperty("isHovered", evt.getNewValue());
            button.repaint();
        });
        
        return button;
    }

    private class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;
        private int thickness;
        
        RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }
    }

    private void showCuteWelcomeDialog() {
        JDialog dialog = new JDialog(this, "", true);
        dialog.setSize(420, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Outer shadow
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 25, 25);
                
                // Blue gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(180, 210, 240),
                    0, getHeight(), new Color(200, 220, 245)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 25, 25);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeLabel = new JLabel("💕 Welcome! 💕", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setForeground(darkBlue);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel messageLabel = new JLabel("Let's calculate your wedding rental costs!", SwingConstants.CENTER);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        messageLabel.setForeground(darkBlue);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton okButton = createGradientButton("OK", buttonBlue, buttonBlueDark);
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.setPreferredSize(new Dimension(120, 40));
        okButton.addActionListener(e -> dialog.dispose());
        
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(okButton);
        mainPanel.add(Box.createVerticalGlue());
        
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    private void calculateCost() {
        if (!tableRadio.isSelected() && !chairRadio.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please select Tables or Chairs! 🎯", 
                "Oops! 😊", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer manual = getManualQuantity();
        Integer preset = null;

        if (chairRadio.isSelected() && manual == null) {
            preset = getPresetQuantitySafe();
        }

        if (tableRadio.isSelected()) preset = null;
        if (manual == null && preset == null) {
            JOptionPane.showMessageDialog(this, "Enter quantity or pick a preset! 📝", 
                "Oops! 😊", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int qty = (manual != null) ? manual : preset;
        int costPer = tableRadio.isSelected() ? 20 : 10;

        resultLabel.setText("💰 Total Cost: $" + (qty * costPer) + " ✨");

        do {
            int response = JOptionPane.showConfirmDialog(this, 
                "Calculation complete! 🎉\nYour total is: $" + (qty * costPer) + 
                "\n\nWould you like to calculate another rental?", 
                "Success! 💕", 
                JOptionPane.YES_NO_OPTION);
            
            if (response == JOptionPane.YES_OPTION) {
                clearFields();
            }
        } while (false);
    }

    private Integer getManualQuantity() {
        try {
            String txt = quantityField.getText().trim();
            if (txt.isEmpty()) return null;
            int v = Integer.parseInt(txt);
            return v > 0 ? v : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getPresetQuantitySafe() {
        String s = (String) seatingBox.getSelectedItem();
        if (s == null) return null;
        String digits = s.replaceAll("[^0-9]", "");
        return digits.isEmpty() ? null : Integer.parseInt(digits);
    }

    private void clearFields() {
        quantityField.setText("");
        tableRadio.setSelected(false);
        chairRadio.setSelected(false);
        seatingBox.setEnabled(false);
        seatingBox.setSelectedIndex(0);
        resultLabel.setText("Total Cost: ");
        
        JOptionPane.showMessageDialog(this, "All cleared! Ready for a new estimate! 🔄", 
            "Reset Complete ✨", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeddingRentalGUI());
    }
}