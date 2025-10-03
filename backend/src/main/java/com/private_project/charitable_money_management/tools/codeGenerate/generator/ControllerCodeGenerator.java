package com.private_project.charitable_money_management.tools.codeGenerate.generator;

import com.private_project.charitable_money_management.tools.codeGenerate.writer.ControllerFileWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class ControllerCodeGenerator {
    public static void createFile(Path controllerDirectoryPath, String selectedEntity) throws IOException {
        String controllerName = selectedEntity + "Controller";
        Path filePath = controllerDirectoryPath.resolve(controllerName + ".java");

        StringBuilder code = new StringBuilder();
        code.append(ControllerFileWriter.writeFile(selectedEntity));

        Files.write(filePath, code.toString().getBytes(StandardCharsets.UTF_8));
        log.info("Interface created at " + filePath);
    }
}
