package edu.ftcc.farmstore.ui;

import edu.ftcc.farmstore.util.PathsCfg;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class AppointmentsPanel extends JPanel implements MainFrame.Refreshable {

    private JTable table;
    private DefaultTableModel model;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public AppointmentsPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(ModernTheme.PANEL);

        // === Toolbar ===
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JButton btnEdit = new JButton("Edit");
        JButton btnComplete = new JButton("Complete & Pay");
        JButton btnCancel = new JButton("Cancel");
        JButton btnRefresh = new JButton("Refresh");

        ModernTheme.styleButton(btnEdit);
        ModernTheme.styleButton(btnComplete);
        ModernTheme.styleButton(btnCancel);
        ModernTheme.styleButton(btnRefresh);

        bar.add(btnEdit);
        bar.add(btnComplete);
        bar.add(btnCancel);
        bar.addSeparator();
        bar.add(btnRefresh);

        add(bar, BorderLayout.NORTH);

        // === Table ===
        model = new DefaultTableModel(
                new Object[]{
                        "Appointment ID", "Customer", "Animal",
                        "Service", "Start", "End", "Status", "Paid"
                },
                0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        ModernTheme.styleTable(table);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Events
        btnEdit.addActionListener(e -> onEdit());
        btnComplete.addActionListener(e -> onComplete());
        btnCancel.addActionListener(e -> onCancel());
        btnRefresh.addActionListener(e -> refresh());

        refresh();
    }

    // =========================================
    // =============== LOAD DATA ===============
    // =========================================
    @Override
    public void refresh() {
        model.setRowCount(0);

        List<Map<String, String>> appts =
                readCsv(PathsCfg.p("appointments.csv"));
        Map<String, String> customers =
                mapFromCsv(PathsCfg.p("customers.csv"), "id", "fullName");
        Map<String, String> animals =
                mapFromCsv(PathsCfg.p("animals.csv"), "id", "name");
        Map<String, String> services =
                mapFromCsv(PathsCfg.p("services.csv"), "id", "serviceName");

        for (Map<String, String> row : appts) {
            model.addRow(new Object[]{
                    row.get("id"),
                    customers.getOrDefault(row.get("customerId"), "Unknown"),
                    animals.getOrDefault(row.get("animalId"), "Unknown"),
                    services.getOrDefault(row.get("serviceId"), "Unknown"),
                    row.get("start"),
                    row.get("end"),
                    row.get("status"),
                    row.get("paidAmount")
            });
        }
    }

    // =========================================
    // =============== EDIT =====================
    // =========================================
    private void onEdit() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select an appointment to edit.");
            return;
        }

        String apptId = (String) model.getValueAt(r, 0);
        Path p = PathsCfg.p("appointments.csv");

        List<String> lines;
        try {
            lines = Files.readAllLines(p);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unable to read appointments.csv");
            return;
        }

        int idx = findRowIndex(lines, apptId);
        if (idx < 0) return;

        String[] c = split(lines.get(idx));

        JTextField tfCustomer = new JTextField(c[1]);
        JTextField tfAnimal = new JTextField(c[2]);
        JTextField tfService = new JTextField(c[3]);
        JTextField tfStart = new JTextField(c[4]);
        JTextField tfEnd = new JTextField(c[5]);

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Customer ID:")); form.add(tfCustomer);
        form.add(new JLabel("Animal ID:"));   form.add(tfAnimal);
        form.add(new JLabel("Service ID:"));  form.add(tfService);
        form.add(new JLabel("Start:"));       form.add(tfStart);
        form.add(new JLabel("End:"));         form.add(tfEnd);

        int ok = JOptionPane.showConfirmDialog(this, form,
                "Edit Appointment", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        c[1] = tfCustomer.getText();
        c[2] = tfAnimal.getText();
        c[3] = tfService.getText();
        c[4] = tfStart.getText();
        c[5] = tfEnd.getText();

        lines.set(idx, String.join(",", c));
        writeSafe(p, lines);
        refresh();
    }

    // =========================================
    // =========== COMPLETE & PAY ==============
    // =========================================
    private void onComplete() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select an appointment first!");
            return;
        }

        String apptId = (String) model.getValueAt(r, 0);
        String paidStr = JOptionPane.showInputDialog(this,
                "Enter amount paid:");
        if (paidStr == null) return;

        Path p = PathsCfg.p("appointments.csv");
        List<String> lines;

        try {
            lines = Files.readAllLines(p);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unable to read appointments.csv");
            return;
        }

        int idx = findRowIndex(lines, apptId);
        if (idx < 0) return;

        String[] c = split(lines.get(idx));
        c[6] = "COMPLETED";
        c[7] = paidStr;

        lines.set(idx, String.join(",", c));
        writeSafe(p, lines);
        refresh();
    }

    // =========================================
    // =============== CANCEL ==================
    // =========================================
    private void onCancel() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select an appointment first!");
            return;
        }

        String apptId = (String) model.getValueAt(r, 0);
        Path p = PathsCfg.p("appointments.csv");

        try {
            List<String> lines = Files.readAllLines(p);
            lines.removeIf(l -> split(l)[0].equals(apptId));
            writeSafe(p, lines);
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error cancelling appointment: " + ex.getMessage());
        }
    }

    // =========================================
    // =============== HELPERS =================
    // =========================================
    private int findRowIndex(List<String> lines, String id) {
        for (int i = 1; i < lines.size(); i++) {
            if (split(lines.get(i))[0].equals(id))
                return i;
        }
        return -1;
    }

    private String[] split(String line) {
        return line.split(",", -1);
    }

    private void writeSafe(Path p, List<String> lines) {
        try {
            Files.write(p, lines, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Write Error: " + ex.getMessage());
        }
    }

    private List<Map<String, String>> readCsv(Path p) {
        List<Map<String, String>> out = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(p);
            if (lines.isEmpty()) return out;

            String[] headers = split(lines.get(0));

            for (int i = 1; i < lines.size(); i++) {
                String[] parts = split(lines.get(i));
                Map<String, String> m = new HashMap<>();

                for (int c = 0; c < headers.length && c < parts.length; c++) {
                    m.put(headers[c], parts[c]);
                }

                out.add(m);
            }

        } catch (Exception ignored) {}

        return out;
    }

    private Map<String, String> mapFromCsv(Path p,
                                           String keyCol,
                                           String valueCol) {

        Map<String, String> map = new HashMap<>();

        try {
            List<String> lines = Files.readAllLines(p);
            if (lines.isEmpty()) return map;

            String[] headers = split(lines.get(0));

            int keyIndex = Arrays.asList(headers).indexOf(keyCol);
            int valIndex = Arrays.asList(headers).indexOf(valueCol);

            if (keyIndex < 0 || valIndex < 0) return map;

            for (int i = 1; i < lines.size(); i++) {
                String[] parts = split(lines.get(i));
                if (parts.length > Math.max(keyIndex, valIndex)) {
                    map.put(parts[keyIndex], parts[valIndex]);
                }
            }

        } catch (Exception ignored) {}

        return map;
    }
}
