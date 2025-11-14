package edu.ftcc.farmstore.util;

import java.nio.file.Path;

public class PathsCfg {
    public static final String DATA_DIR = "data";

    public static Path p(String name) {
        Path file = Path.of(DATA_DIR, name);
        System.out.println("[DEBUG] Looking for file: " + file.toAbsolutePath());
        return file;
    }
}
