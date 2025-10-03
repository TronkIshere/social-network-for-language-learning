package com.private_project.charitable_money_management.tools.codeGenerate.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ProjectPathUtils {
    private static final Path SRC_MAIN_JAVA = Paths.get("src", "main", "java");
    private static final Path BASE_PATH = Paths.get("src", "main", "java", "com", "tronk", "analysis");
    public static Path getOrCreateDirectory(String folderPath) throws IOException {
        String[] folders = folderPath.split("/");
        Path currentPath = BASE_PATH;

        for (String folder : folders) {
            currentPath = currentPath.resolve(folder);
            if (!Files.exists(currentPath)) {
                Files.createDirectories(currentPath);
            }
        }

        System.out.println(folderPath + " directory path: " + currentPath.toAbsolutePath());
        return currentPath;
    }

    public static Path findEntityFolder(Path dir, String folderName) {
        try (Stream<Path> paths = Files.walk(dir)) {
            return paths.filter(Files::isDirectory)
                    .filter(p -> p.getFileName().toString().equalsIgnoreCase(folderName))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            System.err.println("Error finding entity folder: " + e.getMessage());
            return null;
        }
    }

    public static String findPackage(String packageName) {
        try (Stream<Path> paths = Files.walk(SRC_MAIN_JAVA)) {
            return paths.filter(Files::isDirectory)
                    .filter(p -> p.getFileName().toString().equalsIgnoreCase(packageName))
                    .map(p -> getPackageFromPath(p))
                    .findFirst()
                    .orElse("");
        } catch (IOException e) {
            System.err.println("Error finding package: " + e.getMessage());
            return "";
        }
    }

    public static String getPackageFromPath(Path path) {
        Path base = Paths.get("src", "main", "java");
        Path relativePath = base.relativize(path);
        return relativePath.toString().replace(FileSystems.getDefault().getSeparator(), ".");
    }
}
