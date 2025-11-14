package edu.ftcc.farmstore;

import edu.ftcc.farmstore.util.PathsCfg;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public final class Seed {

    public static void ensureAll() {
        ensureFolder();

        create("inventory.csv", """
            id,sku,name,category,unitPrice,qtyOnHand,taxable
        """);

        create("animals.csv", """
            id,species,name,breed,ageMonths,price,available,breeder
        """);

        create("services.csv", """
            id,serviceName,description,price,durationMinutes
        """);

        create("appointments.csv", """
            id,customerId,animalId,serviceId,start,end,status,paidAmount
        """);

        create("customers.csv", """
            id,fullName,phone,email
        """);

        create("employees.csv", """
            id,name,hourlyRate,active
        """);

        create("time_entries.csv", """
            id,employeeId,clockIn,clockOut,hours,pay
        """);

        create("sales.csv", """
            id,customerId,dateTime,subTotal,tax,total,paidCash,paidCard,lines
        """);
    }

    // ---------------------------------------------------------
    // FIXED METHOD — no more red errors
    // Creates /data folder cleanly
    // ---------------------------------------------------------
    private static void ensureFolder() {
        try {
            Path dataPath = Path.of(PathsCfg.DATA_DIR);
            Files.createDirectories(dataPath);
        } catch (Exception ignored) {}
    }

    // ---------------------------------------------------------
    // Create file with header ONLY if missing
    // ---------------------------------------------------------
    private static void create(String name, String header) {
        Path p = PathsCfg.p(name);

        if (Files.exists(p))
            return;

        try {
            Files.writeString(
                    p,
                    header.strip() + "\n",
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE
            );
            System.out.println("[Seed] Created: " + p.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
