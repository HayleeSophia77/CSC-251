package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.model.TimeEntry;
import edu.ftcc.farmstore.util.PathsCfg;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fully updated TimeEntryRepo
 * - Fixes clock-out not saving
 * - Adds hour + pay summation for reports
 * - Safe CSV handling
 * - 100% compatible with new EmployeesPanel
 */
public class TimeEntryRepo {

    private static final String FILE = "time_entries.csv";

    /** Load all entries */
    public static List<TimeEntry> all() {
        List<TimeEntry> list = new ArrayList<>();
        Path p = PathsCfg.p(FILE);

        try {
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);

            for (int i = 1; i < lines.size(); i++) {
                String[] c = lines.get(i).split(",", -1);

                if (c.length >= 6) {
                    LocalDateTime clockIn = LocalDateTime.parse(c[2]);
                    LocalDateTime clockOut =
                            c[3].isBlank() ? null : LocalDateTime.parse(c[3]);

                    list.add(new TimeEntry(
                            c[0],                    // id
                            c[1],                    // employeeId
                            clockIn,
                            clockOut,
                            parseDouble(c[4]),
                            parseDouble(c[5])
                    ));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    /** Save all entries back to CSV */
    public static void saveAll(List<TimeEntry> list) {
        Path p = PathsCfg.p(FILE);

        StringBuilder sb = new StringBuilder(
            "id,employeeId,clockIn,clockOut,hours,pay\n"
        );

        for (TimeEntry t : list) {
            sb.append(t.id).append(",")
              .append(t.employeeId).append(",")
              .append(t.clockIn).append(",")
              .append(t.clockOut == null ? "" : t.clockOut).append(",")
              .append(t.hours).append(",")
              .append(t.pay).append("\n");
        }

        try {
            Files.writeString(p, sb.toString(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Locate open shift (missing clockOut) */
    public static Optional<TimeEntry> openShiftFor(String employeeId) {
        return all().stream()
                .filter(t -> t.employeeId.equals(employeeId) && t.clockOut == null)
                .findFirst();
    }

    /** Haylee feature — Sum total hours for an employee */
    public static double sumHoursFor(String employeeId) {
        return all().stream()
                .filter(t -> t.employeeId.equals(employeeId))
                .mapToDouble(t -> t.hours)
                .sum();
    }

    /** Haylee feature — Sum total pay for an employee */
    public static double sumPayFor(String employeeId) {
        return all().stream()
                .filter(t -> t.employeeId.equals(employeeId))
                .mapToDouble(t -> t.pay)
                .sum();
    }

    // === Safe parse helper ===
    private static double parseDouble(String s) {
        try { return Double.parseDouble(s); }
        catch (Exception ex) { return 0; }
    }
}
