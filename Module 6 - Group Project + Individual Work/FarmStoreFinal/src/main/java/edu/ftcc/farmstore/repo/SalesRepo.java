package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.util.PathsCfg;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Handles all sales logging for Store, Animals, and Services.
 * Each type of sale appends to sales.csv for reporting and auditing.
 */
public class SalesRepo {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Ensure sales.csv exists with header */
    public static void ensure() {
        Path p = PathsCfg.p("sales.csv");
        if (!Files.exists(p)) {
            try {
                Files.createDirectories(p.getParent());
                Files.writeString(p, "type,id,name,qty,price,total,date\n", StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // === Generic append helper ===
    private static void append(String line) {
        try {
            Path p = PathsCfg.p("sales.csv");
            ensure();
            Files.writeString(p, "\n" + line, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // === Store Sale Logging ===
    public static void appendStoreSale(String sku, String name, int qty, double price) {
        double total = qty * price;
        String line = String.join(",",
                "STORE",
                sku,
                name,
                String.valueOf(qty),
                String.format(java.util.Locale.US, "%.2f", price),
                String.format(java.util.Locale.US, "%.2f", total),
                FMT.format(LocalDateTime.now()));
        append(line);
    }

    // === Animal Sale Logging ===
    public static void appendAnimalSale(String animalId, String type, double price) {
        String line = String.join(",",
                "ANIMAL",
                animalId,
                type,
                "1",
                String.format(java.util.Locale.US, "%.2f", price),
                String.format(java.util.Locale.US, "%.2f", price),
                FMT.format(LocalDateTime.now()));
        append(line);
    }

    // === Service Payment Logging ===
    public static void appendServicePayment(String serviceId, String customerId, double paidAmount) {
        String line = String.join(",",
                "SERVICE",
                serviceId,
                "Customer-" + customerId,
                "1",
                String.format(java.util.Locale.US, "%.2f", paidAmount),
                String.format(java.util.Locale.US, "%.2f", paidAmount),
                FMT.format(LocalDateTime.now()));
        append(line);
    }

    // === Read all sales (optional) ===
    public static List<String> readAll() {
        try {
            Path p = PathsCfg.p("sales.csv");
            ensure();
            return Files.readAllLines(p, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // === Clear file (for testing/debug only) ===
    public static void clearAll() {
        try {
            Path p = PathsCfg.p("sales.csv");
            Files.writeString(p, "type,id,name,qty,price,total,date\n", StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // === Backward compatibility helper for ServicesPanel ===
public static void appendServiceSale(String customerId, String serviceId,
                                     double price, double paidCash, double paidCard) {
    double total = paidCash + paidCard;
    String line = String.join(",",
            "SERVICE",
            serviceId,
            "Customer-" + customerId,
            "1",
            String.format(java.util.Locale.US, "%.2f", price),
            String.format(java.util.Locale.US, "%.2f", total),
            FMT.format(LocalDateTime.now()));
    append(line);
}

}
