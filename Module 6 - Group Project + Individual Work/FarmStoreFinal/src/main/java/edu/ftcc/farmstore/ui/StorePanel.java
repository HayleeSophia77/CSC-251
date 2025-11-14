package edu.ftcc.farmstore.ui;

import edu.ftcc.farmstore.repo.InventoryRepo;
import edu.ftcc.farmstore.repo.SalesRepo;
import edu.ftcc.farmstore.util.PathsCfg;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class StorePanel extends JPanel implements MainFrame.Refreshable {

    private final JTable table = new JTable();
    private final InvModel model = new InvModel();

    public StorePanel() {
        setLayout(new BorderLayout());

        // === Toolbar ===
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JButton btnAdd = new JButton("Add Item");
        JButton btnSell = new JButton("Sell Item");
        JButton btnDelete = new JButton("Delete Item");
        JButton btnEditQty = new JButton("Edit Quantity");
        JButton btnRefresh = new JButton("Refresh");

        ModernTheme.styleButton(btnAdd);
        ModernTheme.styleButton(btnSell);
        ModernTheme.styleButton(btnDelete);
        ModernTheme.styleButton(btnEditQty);
        ModernTheme.styleButton(btnRefresh);

        btnAdd.addActionListener(e -> onAdd());
        btnSell.addActionListener(e -> onSell());
        btnDelete.addActionListener(e -> onDelete());
        btnEditQty.addActionListener(e -> onEditQuantity());
        btnRefresh.addActionListener(e -> refresh());

        bar.add(btnAdd);
        bar.add(btnSell);
        bar.add(btnDelete);
        bar.add(btnEditQty);
        bar.addSeparator();
        bar.add(btnRefresh);

        add(bar, BorderLayout.NORTH);

        table.setModel(model);
        ModernTheme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh();
    }

    // -------------------------------------------------------
    // ADD ITEM
    // -------------------------------------------------------
    private void onAdd() {
        JTextField tfId = new JTextField();
        JTextField tfSku = new JTextField();
        JTextField tfName = new JTextField();
        JTextField tfCat = new JTextField();
        JTextField tfPrice = new JTextField();
        JTextField tfQty = new JTextField();
        JCheckBox cbTax = new JCheckBox("Taxable");

        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("ID:")); p.add(tfId);
        p.add(new JLabel("SKU:")); p.add(tfSku);
        p.add(new JLabel("Name:")); p.add(tfName);
        p.add(new JLabel("Category:")); p.add(tfCat);
        p.add(new JLabel("Unit Price:")); p.add(tfPrice);
        p.add(new JLabel("Quantity:")); p.add(tfQty);
        p.add(new JLabel("")); p.add(cbTax);

        int ok = JOptionPane.showConfirmDialog(this, p, "Add Inventory Item", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String[] row = {
                tfId.getText().trim(),
                tfSku.getText().trim(),
                tfName.getText().trim(),
                tfCat.getText().trim(),
                tfPrice.getText().trim(),
                tfQty.getText().trim(),
                String.valueOf(cbTax.isSelected())
        };

        InventoryRepo.append(row);
        refresh();
        JOptionPane.showMessageDialog(this, "New item added successfully!");
    }

    // -------------------------------------------------------
    // SELL ITEM (NOW LOGS TO sales.csv!)
    // -------------------------------------------------------
    private void onSell() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select an item to sell first.");
            return;
        }

        String sku = (String) model.getValueAt(r, 1);
        String name = (String) model.getValueAt(r, 2);
        double price = Double.parseDouble(model.getValueAt(r, 4).toString());
        int qty = Integer.parseInt(model.getValueAt(r, 5).toString());

        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity to sell for " + name + ":", "1");
        if (qtyStr == null) return;

        int sellQty;
        try {
            sellQty = Integer.parseInt(qtyStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid number entered.");
            return;
        }

        if (sellQty > qty) {
            JOptionPane.showMessageDialog(this, "Not enough in stock!");
            return;
        }

        // Update inventory
        int newQty = qty - sellQty;
        InventoryRepo.updateQuantity(sku, newQty);

        // 💥 NEW: LOG THE SALE
        SalesRepo.appendStoreSale(sku, name, sellQty, price);

        refresh();
        JOptionPane.showMessageDialog(this, "Sale recorded! Inventory updated & saved.");
    }

    // -------------------------------------------------------
    // DELETE ITEM
    // -------------------------------------------------------
    private void onDelete() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select an item to delete.");
            return;
        }
        String sku = (String) model.getValueAt(r, 1);

        int ok = JOptionPane.showConfirmDialog(this, "Delete item SKU: " + sku + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            InventoryRepo.deleteBySku(sku);
            refresh();
        }
    }

    // -------------------------------------------------------
    // EDIT QUANTITY
    // -------------------------------------------------------
    private void onEditQuantity() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select an item to edit first.");
            return;
        }

        String sku = (String) model.getValueAt(r, 1);
        String name = (String) model.getValueAt(r, 2);
        int currentQty = Integer.parseInt(model.getValueAt(r, 5).toString());

        String newQtyStr = JOptionPane.showInputDialog(this,
                "Enter new quantity for " + name + " (Current: " + currentQty + "):",
                currentQty);

        if (newQtyStr == null) return;

        int newQty;
        try {
            newQty = Integer.parseInt(newQtyStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
            return;
        }

        InventoryRepo.updateQuantity(sku, newQty);
        refresh();
        JOptionPane.showMessageDialog(this, "Quantity updated!");
    }

    @Override
    public void refresh() {
        model.reload();
    }

    // -------------------------------------------------------
    // TABLE MODEL
    // -------------------------------------------------------
    private static class InvModel extends AbstractTableModel {

        private final String[] cols = {"id", "sku", "name", "category", "unitPrice", "qtyOnHand", "taxable"};
        private List<String[]> rows = new ArrayList<>();

        void reload() {
            Path p = PathsCfg.p("inventory.csv");
            try {
                List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
                rows.clear();

                for (int i = 1; i < lines.size(); i++) {
                    String[] c = lines.get(i).split(",", -1);
                    if (c.length >= 7) rows.add(c);
                }
                fireTableDataChanged();

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Error reading inventory.csv: " + e.getMessage(),
                        "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }
        @Override public Object getValueAt(int r, int c) { return rows.get(r)[c]; }
        @Override public boolean isCellEditable(int r, int c) { return false; }
    }
}
