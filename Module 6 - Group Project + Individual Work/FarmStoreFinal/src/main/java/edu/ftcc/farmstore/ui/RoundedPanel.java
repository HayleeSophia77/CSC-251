package edu.ftcc.farmstore.ui;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private final int arc;
    
    public RoundedPanel(int arc) { 
        this.arc = arc; 
        setOpaque(false); 
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); 
    }
    
    @Override 
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        // Slightly lighter card fill and thinner visual border
        g2.setColor(new Color(26, 30, 38)); // card fill
        g2.fillRoundRect(0, 0, w, h, arc, arc);
        g2.setColor(new Color(255, 255, 255, 14)); // subtler stroke
        g2.drawRoundRect(0, 0, w-1, h-1, arc, arc);
        g2.dispose();
        super.paintComponent(g);
    }
}