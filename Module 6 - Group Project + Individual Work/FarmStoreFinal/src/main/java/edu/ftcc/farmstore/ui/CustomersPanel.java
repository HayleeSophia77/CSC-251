package edu.ftcc.farmstore.ui;

import edu.ftcc.farmstore.util.PathsCfg;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public class CustomersPanel extends JPanel implements MainFrame.Refreshable {

    private JTable table;

    public CustomersPanel() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        // Toolbar
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JButton btnAdd = new JButton("Add Customer");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        ModernTheme.styleButton(btnAdd);
        ModernTheme.styleButton(btnEdit);
        ModernTheme.styleButton(btnDelete);
        ModernTheme.styleButton(btnRefresh);

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> refresh());

        bar.add(btnAdd);
        bar.add(btnEdit);
        bar.add(btnDelete);
        bar.addSeparator();
        bar.add(btnRefresh);

        add(bar, BorderLayout.NORTH);

        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    // ----------------------
    // Add Customer
    // ----------------------
    private void onAdd() {
        JTextField tfId = new JTextField();
        JTextField tfName = new JTextField();
        JTextField tfPhone = new JTextField();
        JTextField tfEmail = new JTextField();

        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("Customer ID:")); p.add(tfId);
        p.add(new JLabel("Full Name:")); p.add(tfName);
        p.add(new JLabel("Phone:")); p.add(tfPhone);
        p.add(new JLabel("Email:")); p.add(tfEmail);

        int ok = JOptionPane.showConfirmDialog(this, p, "Add Customer",
                JOptionPane.OK_CANCEL_OPTION);

        if (ok != JOptionPane.OK_OPTION) return;

        String line = String.join(",",
                tfId.getText().trim(),
                tfName.getText().trim(),
                tfPhone.getText().trim(),
                tfEmail.getText().trim()
        );

        try {
            Path pth = PathsCfg.p("customers.csv");
            Files.writeString(pth, "\n" + line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            JOptionPane.showMessageDialog(this, "Customer added!");
            refresh();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ----------------------
    // Edit Customer
    // ----------------------
    private void onEdit() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer first!");
            return;
        }

        DefaultTableModel m = (DefaultTableModel) table.getModel();
        String[] vals = new String[m.getColumnCount()];
        for (int i = 0; i < vals.length; i++)
            vals[i] = m.getValueAt(r, i).toString();

        JTextField tfId = new JTextField(vals[0]);
        JTextField tfName = new JTextField(vals[1]);
        JTextField tfPhone = new JTextField(vals[2]);
        JTextField tfEmail = new JTextField(vals[3]);

        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("Customer ID:")); p.add(tfId);
        p.add(new JLabel("Full Name:")); p.add(tfName);
        p.add(new JLabel("Phone:")); p.add(tfPhone);
        p.add(new JLabel("Email:")); p.add(tfEmail);

        int ok = JOptionPane.showConfirmDialog(this, p, "Edit Customer",
                JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        // update model
        m.setValueAt(tfId.getText(), r, 0);
        m.setValueAt(tfName.getText(), r, 1);
        m.setValueAt(tfPhone.getText(), r, 2);
        m.setValueAt(tfEmail.getText(), r, 3);

        saveTable(m);
    }

    // ----------------------
    // Delete
    // ----------------------
    private void onDelete() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer to delete!");
            return;
        }

        DefaultTableModel m = (DefaultTableModel) table.getModel();
        m.removeRow(r);
        saveTable(m);
    }

    // ----------------------
    // Save table back to CSV
    // ----------------------
    private void saveTable(DefaultTableModel m) {
        try {
            Path p = PathsCfg.p("customers.csv");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < m.getRowCount(); i++) {
                for (int j = 0; j < m.getColumnCount(); j++) {
                    if (j > 0) sb.append(",");
                    sb.append(m.getValueAt(i, j));
                }
                sb.append("\n");
            }
            Files.writeString(p, sb.toString(), StandardCharsets.UTF_8);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
        }
    }

    @Override
    public void refresh() {
        Path p = PathsCfg.p("customers.csv");

        try {
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
            if (lines.size() > 0) {
                String[] headers = lines.get(0).split(",");
                List<String[]> rows = lines.stream()
                        .skip(1)
                        .filter(l -> !l.isBlank())
                        .map(l -> l.split(",", -1))
                        .collect(Collectors.toList());

                DefaultTableModel m = new DefaultTableModel(headers, 0);
                for (String[] r : rows) m.addRow(r);

                table.setModel(m);
                ModernTheme.styleTable(table);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
