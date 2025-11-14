package edu.ftcc.farmstore.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;

// FIXED imports – no more ambiguous List!
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import edu.ftcc.farmstore.model.Employee;
import edu.ftcc.farmstore.model.TimeEntry;
import edu.ftcc.farmstore.repo.EmployeeRepo;
import edu.ftcc.farmstore.repo.TimeEntryRepo;

public class EmployeesPanel extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID","Name","Hourly Rate","Active","Clocked In","Hours Worked","Total Pay"}, 0
    ) {
        public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);

    public EmployeesPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Toolbar -----------------------------------------------------
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton toggle = new JButton("Toggle Active");
        JButton clockIn = new JButton("Clock In");
        JButton clockOut = new JButton("Clock Out");

        ModernTheme.styleButton(add);
        ModernTheme.styleButton(edit);
        ModernTheme.styleButton(toggle);
        ModernTheme.styleButton(clockIn);
        ModernTheme.styleButton(clockOut);

        bar.add(add);
        bar.add(edit);
        bar.add(toggle);
        bar.add(clockIn);
        bar.add(clockOut);

        add(bar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Button actions
        add.addActionListener(e -> safe("adding employee", this::onAdd));
        edit.addActionListener(e -> safe("editing employee", this::onEdit));
        toggle.addActionListener(e -> safe("toggling active", this::onToggle));
        clockIn.addActionListener(e -> safe("clock in", this::onClockIn));
        clockOut.addActionListener(e -> safe("clock out", this::onClockOut));

        refresh();
    }

    // LOAD TABLE ----------------------------------------------------
    private void refresh() {
        model.setRowCount(0);

        for (Employee emp : EmployeeRepo.all()) {

            boolean openShift = TimeEntryRepo.openShiftFor(emp.id).isPresent();

            double hours = TimeEntryRepo.sumHoursFor(emp.id);
            double pay = TimeEntryRepo.sumPayFor(emp.id);

            model.addRow(new Object[]{
                    emp.id,
                    emp.name,
                    money(emp.hourlyRate),
                    yn(emp.active),
                    yn(openShift),
                    String.format("%.2f", hours),
                    money(pay)
            });
        }

        ModernTheme.styleTable(table);
    }

    private Optional<Employee> selected() {
        int r = table.getSelectedRow();
        if (r < 0) return Optional.empty();
        return EmployeeRepo.byId((String) model.getValueAt(r, 0));
    }

    // ADD EMPLOYEE --------------------------------------------------
    private void onAdd() {
        String name = JOptionPane.showInputDialog(this, "Name:");
        if (name == null || name.isBlank()) return;

        double rate = d(JOptionPane.showInputDialog(this, "Hourly Rate:"));

        List<Employee> all = EmployeeRepo.all();

        // Auto-generate ID
        String newId = EmployeeRepo.nextId();

        all.add(new Employee(newId, name, rate, true));
        EmployeeRepo.saveAll(all);

        JOptionPane.showMessageDialog(this, "Employee added with ID: " + newId);
        refresh();
    }

    // EDIT ----------------------------------------------------------
    private void onEdit() {
        Optional<Employee> opt = selected();
        if (opt.isEmpty()) return;

        Employee e = opt.get();

        String name = JOptionPane.showInputDialog(this, "Name:", e.name);
        if (name == null) return;

        double rate = d(JOptionPane.showInputDialog(this, "Hourly Rate:", e.hourlyRate + ""));

        List<Employee> all = EmployeeRepo.all();
        for (Employee x : all)
            if (x.id.equals(e.id)) {
                x.name = name;
                x.hourlyRate = rate;
            }

        EmployeeRepo.saveAll(all);
        refresh();
    }

    // TOGGLE ACTIVE -------------------------------------------------
    private void onToggle() {
        Optional<Employee> opt = selected();
        if (opt.isEmpty()) return;

        Employee e = opt.get();
        List<Employee> all = EmployeeRepo.all();

        for (Employee x : all)
            if (x.id.equals(e.id))
                x.active = !x.active;

        EmployeeRepo.saveAll(all);
        refresh();
    }

    // CLOCK IN ------------------------------------------------------
    private void onClockIn() {
        Optional<Employee> opt = selected();
        if (opt.isEmpty()) return;

        Employee e = opt.get();

        if (!e.active) {
            JOptionPane.showMessageDialog(this, "Inactive employee cannot clock in.");
            return;
        }

        if (TimeEntryRepo.openShiftFor(e.id).isPresent()) {
            JOptionPane.showMessageDialog(this, "Already clocked in.");
            return;
        }

        TimeEntry entry = new TimeEntry(
                "T" + System.currentTimeMillis(),
                e.id,
                LocalDateTime.now(),
                null,
                0,
                0
        );

        List<TimeEntry> all = TimeEntryRepo.all();
        all.add(entry);
        TimeEntryRepo.saveAll(all);

        JOptionPane.showMessageDialog(this, e.name + " clocked in!");
        refresh();
    }

    // CLOCK OUT -----------------------------------------------------
    private void onClockOut() {
        Optional<Employee> opt = selected();
        if (opt.isEmpty()) return;

        Employee e = opt.get();
        Optional<TimeEntry> open = TimeEntryRepo.openShiftFor(e.id);

        if (open.isEmpty()) {
            JOptionPane.showMessageDialog(this, "This employee is not clocked in.");
            return;
        }

        TimeEntry t = open.get();
        LocalDateTime now = LocalDateTime.now();

        double hours = Duration.between(t.clockIn, now).toMinutes() / 60.0;
        double pay = hours * e.hourlyRate;

        t.clockOut = now;
        t.hours = hours;
        t.pay = pay;

        List<TimeEntry> all = TimeEntryRepo.all();
        for (int i = 0; i < all.size(); i++)
            if (all.get(i).id.equals(t.id))
                all.set(i, t);

        TimeEntryRepo.saveAll(all);

        JOptionPane.showMessageDialog(this,
                "%s clocked out.\nHours: %.2f\nPay: %s".formatted(e.name, hours, money(pay)));

        refresh();
    }

    // HELPERS -------------------------------------------------------
    private static String money(double d) { return NumberFormat.getCurrencyInstance().format(d); }

    private static double d(String s) {
        try { return Double.parseDouble(s); }
        catch (Exception ex) { return 0; }
    }

    private static String yn(boolean b) { return b ? "Yes" : "No"; }

    private void safe(String doing, Runnable r) {
        try { r.run(); }
        catch (Throwable t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error " + doing + ": " + t.getMessage());
        }
    }
}
