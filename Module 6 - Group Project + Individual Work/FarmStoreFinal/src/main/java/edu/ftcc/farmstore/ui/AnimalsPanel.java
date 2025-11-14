package edu.ftcc.farmstore.ui;

import edu.ftcc.farmstore.repo.SalesRepo;
import edu.ftcc.farmstore.util.PathsCfg;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class AnimalsPanel extends JPanel implements MainFrame.Refreshable {

    private JTable table;

    private static final String[] HEADERS = {
            "id","species","name","breed","ageMonths","price","available","breeder"
    };

    public AnimalsPanel() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        // === Toolbar ===
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JButton btnAdd = new JButton("Add Animal");
        JButton btnEdit = new JButton("Edit Animal");
        JButton btnDelete = new JButton("Delete Animal");
        JButton btnSell = new JButton("Sell Animal");
        JButton btnRefresh = new JButton("Refresh");

        ModernTheme.styleButton(btnAdd);
        ModernTheme.styleButton(btnEdit);
        ModernTheme.styleButton(btnDelete);
        ModernTheme.styleButton(btnSell);
        ModernTheme.styleButton(btnRefresh);

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnSell.addActionListener(e -> onSell());
        btnRefresh.addActionListener(e -> refresh());

        bar.add(btnAdd);
        bar.add(btnEdit);
        bar.add(btnDelete);
        bar.add(btnSell);
        bar.addSeparator();
        bar.add(btnRefresh);

        add(bar, BorderLayout.NORTH);

        // === Table ===
        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    // ============================================================
    // ADD Animal
    // ============================================================
    private void onAdd() {
        JTextField tfId = new JTextField();
        JTextField tfSpecies = new JTextField();
        JTextField tfName = new JTextField();
        JTextField tfBreed = new JTextField();
        JTextField tfAge = new JTextField();
        JTextField tfPrice = new JTextField();
        JCheckBox cbAvail = new JCheckBox("Available?", true);
        JTextField tfBreeder = new JTextField();

        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("ID:")); p.add(tfId);
        p.add(new JLabel("Species:")); p.add(tfSpecies);
        p.add(new JLabel("Name:")); p.add(tfName);
        p.add(new JLabel("Breed:")); p.add(tfBreed);
        p.add(new JLabel("Age (months):")); p.add(tfAge);
        p.add(new JLabel("Price:")); p.add(tfPrice);
        p.add(new JLabel("Breeder:")); p.add(tfBreeder);
        p.add(new JLabel("")); p.add(cbAvail);

        int ok = JOptionPane.showConfirmDialog(this, p, "Add Animal", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String[] row = {
                tfId.getText().trim(),
                tfSpecies.getText().trim(),
                tfName.getText().trim(),
                tfBreed.getText().trim(),
                tfAge.getText().trim(),
                tfPrice.getText().trim(),
                String.valueOf(cbAvail.isSelected()),
                tfBreeder.getText().trim()
        };

        try {
            Files.writeString(
                    PathsCfg.p("animals.csv"),
                    "\n" + String.join(",", row),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        refresh();
    }

    // ============================================================
    // EDIT Animal
    // ============================================================
    private void onEdit() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first!");
            return;
        }

        DefaultTableModel m = (DefaultTableModel) table.getModel();

        JTextField tfId = new JTextField(m.getValueAt(r, 0).toString());
        JTextField tfSpecies = new JTextField(m.getValueAt(r, 1).toString());
        JTextField tfName = new JTextField(m.getValueAt(r, 2).toString());
        JTextField tfBreed = new JTextField(m.getValueAt(r, 3).toString());
        JTextField tfAge = new JTextField(m.getValueAt(r, 4).toString());
        JTextField tfPrice = new JTextField(m.getValueAt(r, 5).toString());
        JCheckBox cbAvail = new JCheckBox("Available?", Boolean.parseBoolean(m.getValueAt(r, 6).toString()));
        JTextField tfBreeder = new JTextField(m.getValueAt(r, 7).toString());

        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("ID:")); p.add(tfId);
        p.add(new JLabel("Species:")); p.add(tfSpecies);
        p.add(new JLabel("Name:")); p.add(tfName);
        p.add(new JLabel("Breed:")); p.add(tfBreed);
        p.add(new JLabel("Age (months):")); p.add(tfAge);
        p.add(new JLabel("Price:")); p.add(tfPrice);
        p.add(new JLabel("Breeder:")); p.add(tfBreeder);
        p.add(new JLabel("")); p.add(cbAvail);

        int ok = JOptionPane.showConfirmDialog(this, p, "Edit Animal", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        m.setValueAt(tfId.getText(), r, 0);
        m.setValueAt(tfSpecies.getText(), r, 1);
        m.setValueAt(tfName.getText(), r, 2);
        m.setValueAt(tfBreed.getText(), r, 3);
        m.setValueAt(tfAge.getText(), r, 4);
        m.setValueAt(tfPrice.getText(), r, 5);
        m.setValueAt(String.valueOf(cbAvail.isSelected()), r, 6);
        m.setValueAt(tfBreeder.getText(), r, 7);

        saveTable(m);
    }

    // ============================================================
    // DELETE Animal
    // ============================================================
    private void onDelete() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select an animal!");
            return;
        }
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        m.removeRow(r);
        saveTable(m);
    }

    // ============================================================
    // SELL Animal
    // ============================================================
    private void onSell() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select an animal!");
            return;
        }

        String id = table.getValueAt(r, 0).toString();
        String species = table.getValueAt(r, 1).toString();
        double price = Double.parseDouble(table.getValueAt(r, 5).toString());

        // log sale
        SalesRepo.appendAnimalSale(id, species, price);

        // mark unavailable
        table.setValueAt("false", r, 6);

        saveTable((DefaultTableModel) table.getModel());
        JOptionPane.showMessageDialog(this, "Animal sold!");
    }

    // ============================================================
    // SAVE TABLE BACK TO CSV
    // ============================================================
    private void saveTable(DefaultTableModel m) {
        try {
            Path p = PathsCfg.p("animals.csv");
            StringBuilder sb = new StringBuilder();

            // Write header
            sb.append(String.join(",", HEADERS)).append("\n");

            // Write rows
            for (int i = 0; i < m.getRowCount(); i++) {
                for (int j = 0; j < m.getColumnCount(); j++) {
                    if (j > 0) sb.append(",");
                    sb.append(m.getValueAt(i, j));
                }
                sb.append("\n");
            }

            Files.writeString(p, sb.toString(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
        }
    }

    // ============================================================
    // REFRESH
    // ============================================================
    @Override
    public void refresh() {
        Path p = PathsCfg.p("animals.csv");

        try {
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
            if (lines.size() < 2) return;

            List<String[]> rows = new ArrayList<>();

            for (int i = 1; i < lines.size(); i++) {
                String[] c = lines.get(i).split(",", -1);
                if (c.length >= 8) rows.add(c);
            }

            DefaultTableModel m = new DefaultTableModel(HEADERS, 0);
            for (String[] r : rows) m.addRow(r);

            table.setModel(m);
            ModernTheme.styleTable(table);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading animals.csv:\n" + e.getMessage());
        }
    }
}
