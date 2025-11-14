package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.util.PathsCfg;
import edu.ftcc.farmstore.util.Csv;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * InventoryRepo handles reading and writing the inventory.csv file.
 * This powers the "Store Sales" page in the Farm Store Manager.
 */
public class InventoryRepo {

    private static final String FILE_NAME = "inventory.csv";

    /** Read all inventory rows */
    public static List<String[]> readAll() {
        Path p = PathsCfg.p(FILE_NAME);

        // Debug to confirm the file path
        System.out.println("[InventoryRepo] Reading from: " + p.toAbsolutePath());

        // If file doesn’t exist, create it with headers
        if (!Files.exists(p)) {
            ensure();
        }

        List<String[]> rows = Csv.read(p);
        if (rows == null) {
            System.err.println("[InventoryRepo] ⚠️ Csv.read returned null — check file path or format!");
            return new ArrayList<>();
        }

        return rows;
    }

    /** Save full inventory list (overwrite file) */
    public static void saveAll(List<String[]> rows) {
        Path p = PathsCfg.p(FILE_NAME);
        Csv.write(p, "id,sku,name,category,unitPrice,qtyOnHand,taxable", rows);
    }

    /** Append a new inventory item to the file */
    public static void append(String[] newRow) {
        Path p = PathsCfg.p(FILE_NAME);
        try {
            Files.writeString(
                p,
                "\n" + String.join(",", newRow),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
            System.out.println("[InventoryRepo] ✅ Added new row: " + String.join(",", newRow));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Update quantity for a sold item (used during "New Sale") */
    public static void updateQuantity(String sku, int newQty) {
        List<String[]> rows = readAll();
        boolean changed = false;

        for (String[] row : rows) {
            if (row[1].equalsIgnoreCase(sku)) {
                row[5] = String.valueOf(newQty);
                changed = true;
                break;
            }
        }

        if (changed) {
            saveAll(rows);
            System.out.println("[InventoryRepo] Updated quantity for " + sku + " → " + newQty);
        }
    }

    /** Delete an item by SKU */
    public static void deleteBySku(String sku) {
        List<String[]> rows = readAll();
        rows.removeIf(r -> r[1].equalsIgnoreCase(sku));
        saveAll(rows);
        System.out.println("[InventoryRepo] Deleted item: " + sku);
    }

    /** Ensure file exists with correct header */
    public static void ensure() {
        Path p = PathsCfg.p(FILE_NAME);
        try {
            Files.createDirectories(p.getParent());
            if (!Files.exists(p)) {
                Files.writeString(p,
                    "id,sku,name,category,unitPrice,qtyOnHand,taxable\n",
                    StandardCharsets.UTF_8
                );
                System.out.println("[InventoryRepo] Created new file: " + p.toAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
