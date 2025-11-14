package edu.ftcc.farmstore.repo;

import edu.ftcc.farmstore.model.Employee;
import edu.ftcc.farmstore.util.PathsCfg;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.io.IOException;

public class EmployeeRepo {

    private static final String FILE = "employees.csv";

    /** Load all employees */
    public static List<Employee> all() {
        List<Employee> list = new ArrayList<>();
        Path p = PathsCfg.p(FILE);

        try {
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);

            for (int i = 1; i < lines.size(); i++) {
                String[] c = lines.get(i).split(",", -1);
                if (c.length >= 4) {
                    list.add(new Employee(
                            c[0],                     // id
                            c[1],                     // name
                            Double.parseDouble(c[2]), // hourly rate
                            Boolean.parseBoolean(c[3])// active
                    ));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    /** Find employee by ID */
    public static Optional<Employee> byId(String id) {
        return all().stream()
                .filter(e -> e.id.equals(id))
                .findFirst();
    }

    /** Save all employees back to csv */
    public static void saveAll(List<Employee> list) {
        Path p = PathsCfg.p(FILE);

        StringBuilder sb = new StringBuilder("id,name,hourlyRate,active\n");

        for (Employee e : list) {
            sb.append(e.id).append(",")
              .append(e.name).append(",")
              .append(e.hourlyRate).append(",")
              .append(e.active).append("\n");
        }

        try {
            Files.writeString(p, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ============================================================
    //     ⭐ AUTO-GENERATE NEXT EMPLOYEE ID (E1, E2, E3…)
    // ============================================================
    public static String nextId() {

        List<Employee> all = all();

        int max = 0;
        for (Employee e : all) {
            try {
                if (e.id.startsWith("E")) {
                    int num = Integer.parseInt(e.id.substring(1));
                    if (num > max) max = num;
                }
            } catch (Exception ignored) {}
        }

        return "E" + (max + 1); // next available ID
    }
}
