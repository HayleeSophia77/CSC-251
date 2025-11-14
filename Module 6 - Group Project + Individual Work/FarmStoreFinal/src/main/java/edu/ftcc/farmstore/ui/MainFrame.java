package edu.ftcc.farmstore.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    public interface Refreshable {
        void refresh();
    }

    private final List<Refreshable> refreshPanels = new ArrayList<>();

    public MainFrame() {
        super("Farm Store Manager");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(ModernTheme.PANEL);
        tabs.setForeground(Color.WHITE);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // --------------------------------------------------------
        // Create Panels (ALL panels included)
        // --------------------------------------------------------
        StorePanel store = new StorePanel();
        AnimalsPanel animals = new AnimalsPanel();
        ServicesPanel services = new ServicesPanel();
        CustomersPanel customers = new CustomersPanel();
        EmployeesPanel employees = new EmployeesPanel();
        AppointmentsPanel appointments = new AppointmentsPanel();   // ✅ restored  
        ReportsPanel reports = new ReportsPanel();

        // --------------------------------------------------------
        // Add Tabs In Correct Order
        // --------------------------------------------------------
        addTab(tabs, "Store", store);
        addTab(tabs, "Animals", animals);
        addTab(tabs, "Services", services);
        addTab(tabs, "Appointments", appointments);  // ⭐ BACK IN APP
        addTab(tabs, "Customers", customers);
        addTab(tabs, "Employees", employees);
        addTab(tabs, "Reports", reports);

        add(tabs, BorderLayout.CENTER);

        ModernTheme.apply(this);
    }

    private void addTab(JTabbedPane tabs, String name, JPanel panel) {
        tabs.addTab(name, panel);
        if (panel instanceof Refreshable r) {
            refreshPanels.add(r);
        }
    }

    public void refreshAll() {
        for (Refreshable p : refreshPanels) {
            p.refresh();
        }
    }
}
