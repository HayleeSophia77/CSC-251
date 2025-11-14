package edu.ftcc.farmstore.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import edu.ftcc.farmstore.util.PathsCfg;
import edu.ftcc.farmstore.repo.*;
import edu.ftcc.farmstore.model.*;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ServicesPanel extends JPanel implements MainFrame.Refreshable {

    private JTable table;
    private DefaultTableModel model;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public ServicesPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(ModernTheme.PANEL);

        // ===== Toolbar =====
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JButton btnSchedule = new JButton("Schedule Service");
        JButton btnRefresh = new JButton("Refresh");

        ModernTheme.styleButton(btnSchedule);
        ModernTheme.styleButton(btnRefresh);

        bar.add(btnSchedule);
        bar.add(btnRefresh);

        add(bar, BorderLayout.NORTH);

        // ===== Table =====
        model = new DefaultTableModel(
                new Object[]{"ID", "Service Name", "Description", "Price", "Duration"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        ModernTheme.styleTable(table);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Actions =====
        btnSchedule.addActionListener(e -> onSchedule());
        btnRefresh.addActionListener(e -> refresh());

        refresh();
    }

    @Override
    public void refresh() {
        model.setRowCount(0);

        Path p = PathsCfg.p("services.csv");

        try {
            List<String> lines = Files.readAllLines(p);
            for (int i = 1; i < lines.size(); i++) {
                String[] c = splitCsv(lines.get(i));
                if (c.length >= 5) {
                    model.addRow(new Object[]{
                            c[0], c[1], c[2], c[3], c[4]
                    });
                }
            }
        } catch (Exception ignored) {}
    }

    // ===============================================================
    // SCHEDULE SERVICE (REPLACES ADD APPOINTMENT)
    // ===============================================================
    private void onSchedule() {

        // ---------------- Customers ----------------
        Map<String, String> cust = mapDropdown(PathsCfg.p("customers.csv"), "id", "fullName");
        String[] custChoices = cust.values().toArray(new String[0]);

        // ---------------- Animals ----------------
        Map<String, String> animals = mapDropdown(PathsCfg.p("animals.csv"), "id", "name");
        String[] animalChoices = animals.values().toArray(new String[0]);

        // ---------------- Services ----------------
        Map<String, String> services = mapDropdown(PathsCfg.p("services.csv"), "id", "serviceName");
        String[] serviceChoices = services.values().toArray(new String[0]);

        JComboBox<String> cbCust = new JComboBox<>(custChoices);
        JComboBox<String> cbAnimal = new JComboBox<>(animalChoices);
        JComboBox<String> cbService = new JComboBox<>(serviceChoices);

        JTextField tfStart = new JTextField(FMT.format(LocalDateTime.now()));
        JTextField tfEnd = new JTextField(FMT.format(LocalDateTime.now().plusMinutes(30)));

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Customer:")); form.add(cbCust);
        form.add(new JLabel("Animal:")); form.add(cbAnimal);
        form.add(new JLabel("Service:")); form.add(cbService);
        form.add(new JLabel("Start:")); form.add(tfStart);
        form.add(new JLabel("End:")); form.add(tfEnd);

        int ok = JOptionPane.showConfirmDialog(this, form, "Schedule Service", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String custId = getKeyByValue(cust, (String) cbCust.getSelectedItem());
        String animalId = getKeyByValue(animals, (String) cbAnimal.getSelectedItem());
        String serviceId = getKeyByValue(services, (String) cbService.getSelectedItem());

        String id = "AP" + System.currentTimeMillis();

        String line = String.join(",",
                id,
                custId,
                animalId,
                serviceId,
                tfStart.getText(),
                tfEnd.getText(),
                "SCHEDULED",
                "0.00"
        );

        try {
            Files.writeString(PathsCfg.p("appointments.csv"),
                    "\n" + line, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error writing to file:\n" + ex.getMessage());
        }
    }

    // ===============================================================
    // HELPERS
    // ===============================================================

    private Map<String, String> mapDropdown(Path p, String keyCol, String valueCol) {
        Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER); // SORTED

        try {
            List<String> lines = Files.readAllLines(p);
            if (lines.isEmpty()) return map;

            String[] headers = lines.get(0).split(",", -1);

            int keyI = -1, valI = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equals(keyCol)) keyI = i;
                if (headers[i].equals(valueCol)) valI = i;
            }

            if (keyI < 0 || valI < 0) return map;

            for (int i = 1; i < lines.size(); i++) {
                String[] c = splitCsv(lines.get(i));
                if (c.length > Math.max(keyI, valI))
                    map.put(c[keyCol.equals("id") ? keyI : keyI],  // key
                            c[valI] + " – " + c[keyI]);           // value shown
            }

        } catch (Exception ignored) {}

        return map;
    }

    private String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> e : map.entrySet())
            if (e.getValue().equals(value))
                return e.getKey();
        return null;
    }

    private String[] splitCsv(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
