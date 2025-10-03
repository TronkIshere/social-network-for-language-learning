package com.private_project.social_network_for_language_learning.tools.codeGenerate.generator;

import com.private_project.social_network_for_language_learning.tools.codeGenerate.writer.ResponseFileWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class ResponseCodeGenerator {
    public static void createFile(Path directoryPath, String responseName, String selectedEntity, List<String> properties) throws IOException {
        Path filePath = directoryPath.resolve(responseName + ".java");

        StringBuilder code = new StringBuilder();
        code.append(ResponseFileWriter.writeFile(properties, responseName, selectedEntity));

        Files.write(filePath, code.toString().getBytes(StandardCharsets.UTF_8));
        log.info(responseName + " created at " + filePath);
    }
}
