package edu.ftcc.farmstore.ui;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

import edu.ftcc.farmstore.util.PathsCfg;
import edu.ftcc.farmstore.repo.EmployeeRepo;
import edu.ftcc.farmstore.repo.TimeEntryRepo;

public class ReportsPanel extends JPanel implements MainFrame.Refreshable {

    private JTextArea txt;

    public ReportsPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(ModernTheme.PANEL);

        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.setOpaque(false);

        JButton btnRefresh = new JButton("Generate Report");
        ModernTheme.styleButton(btnRefresh);
        btnRefresh.addActionListener(e -> refresh());
        bar.add(btnRefresh);

        add(bar, BorderLayout.NORTH);

        txt = new JTextArea();
        txt.setEditable(false);
        txt.setFont(new Font("Consolas", Font.PLAIN, 15));
        txt.setForeground(ModernTheme.TEXT);
        txt.setBackground(ModernTheme.SURFACE);
        txt.setMargin(new Insets(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(txt);
        scroll.setOpaque(true);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(ModernTheme.SURFACE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        try {
            double storeSales = sumStoreSales(PathsCfg.p("sales.csv"));      // FIXED
            double serviceSales = sumAppointments(PathsCfg.p("appointments.csv"));
            double animalSales = 0; // Not used in your model

            double totalSales = storeSales + serviceSales + animalSales;

            double empHours = EmployeeRepo.all().stream()
                    .mapToDouble(e -> TimeEntryRepo.sumHoursFor(e.id))
                    .sum();

            double empPay = EmployeeRepo.all().stream()
                    .mapToDouble(e -> TimeEntryRepo.sumPayFor(e.id))
                    .sum();

            String out = String.format("""
                ===== FARM STORE REPORTS =====

                Total Store Sales:                           $%.2f
                Total Paid Service Appointments:             $%.2f

                Total Employee Hours:                        %.2f hrs
                Total Employee Pay:                          $%.2f

                ==============================
                GRAND TOTAL REVENUE:                         $%.2f
                TOTAL LABOR COST:                            $%.2f
                NET PROFIT:                                  $%.2f
                """,
                    storeSales,
                    serviceSales,
                    empHours,
                    empPay,
                    totalSales,
                    empPay,
                    (totalSales - empPay)
            );

            txt.setText(out);

        } catch (Exception e) {
            txt.setText("Error generating report:\n" + e.getMessage());
        }
    }

    // ==========================================================
    // FIXED: WORKS WITH YOUR REAL sales.csv FORMAT
    // ==========================================================
    private double sumStoreSales(Path p) {
        try {
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);

            return lines.stream()
                    .skip(1)
                    .mapToDouble(line -> {
                        try {
                            String[] c = splitCsv(line);

                            // YOUR sales.csv index:
                            // 0=id
                            // 1=customerId
                            // 2=dateTime
                            // 3=subTotal
                            // 4=tax
                            // 5=total  <---- THIS is the correct column
                            return Double.parseDouble(c[5]);

                        } catch (Exception ex) {
                            return 0;
                        }
                    })
                    .sum();

        } catch (Exception ex) {
            return 0;
        }
    }

    // SUM PAID APPOINTMENTS
    private double sumAppointments(Path p) {
        try {
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);

            return lines.stream()
                    .skip(1)
                    .mapToDouble(line -> {
                        try {
                            String[] c = splitCsv(line);
                            if (c[6].equalsIgnoreCase("COMPLETED"))
                                return Double.parseDouble(c[7]);
                        } catch (Exception ignored) {}
                        return 0;
                    })
                    .sum();

        } catch (Exception ex) {
            return 0;
        }
    }

    private String[] splitCsv(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
