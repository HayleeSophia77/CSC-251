package edu.ftcc.farmstore;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import edu.ftcc.farmstore.ui.MainFrame;
import edu.ftcc.farmstore.ui.ModernTheme;

public class App {
    public static void main(String[] args) {

        Seed.ensureAll();

        // Haylee - global exception handler
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "An unexpected error occurred.\nPlease restart the application.",
                "Application Error",
                JOptionPane.ERROR_MESSAGE);
        });

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            ModernTheme.apply(frame);
            frame.setVisible(true);
        });
    }
}
