package edu.ftcc.farmstore.ui;

public class SimpleDocListener implements javax.swing.event.DocumentListener {
    private final VoidFn fn;

    public SimpleDocListener(VoidFn fn) {
        this.fn = fn;
    }

    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        fn.run();
    }

    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        fn.run();
    }

    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        fn.run();
    }

    @FunctionalInterface
    public interface VoidFn {
        void run();
    }
}