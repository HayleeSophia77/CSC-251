package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.util.PathsCfg;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class AppointmentsRepo {
    private AppointmentsRepo(){}

    private static final String[] HEADERS = {
        "id","customerId","animalId","serviceId","start","end","status","paidAmount"
    };

    private static Path path() { return PathsCfg.p("appointments.csv"); }

    public static List<String[]> readAll() {
        Path p = path();
        if (Files.notExists(p)) return new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
            List<String[]> rows = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                String ln = lines.get(i).trim();
                if (!ln.isBlank()) rows.add(ln.split(",", -1));
            }
            return rows;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void append(String[] row) {
        try {
            Path p = path();
            Files.createDirectories(p.getParent());
            if (Files.notExists(p)) {
                // write header
                List<String> out = new ArrayList<>();
                out.add(String.join(",", HEADERS));
                Files.write(p, out, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
            Files.writeString(p, "\n" + String.join(",", row), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updatePaidAndStatus(String apptId, String status, String paidAmount) {
        Path p = path();
        try {
            if (Files.notExists(p)) return;
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
            if (lines.isEmpty()) return;

            // headers
            List<String> out = new ArrayList<>();
            out.add(lines.get(0));

            for (int i = 1; i < lines.size(); i++) {
                String ln = lines.get(i);
                if (ln.isBlank()) continue;
                String[] cols = ln.split(",", -1);
                if (cols.length >= 8 && cols[0].equals(apptId)) {
                    cols[6] = status;
                    cols[7] = paidAmount;
                    out.add(String.join(",", cols));
                } else {
                    out.add(ln);
                }
            }
            Files.write(p, out, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
