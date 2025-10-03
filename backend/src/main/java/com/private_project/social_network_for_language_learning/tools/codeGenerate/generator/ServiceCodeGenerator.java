package com.private_project.social_network_for_language_learning.tools.codeGenerate.generator;

import com.private_project.social_network_for_language_learning.tools.codeGenerate.writer.ServiceFileWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class ServiceCodeGenerator {
    public static void createFile(Path serviceDirectoryPath, String selectedEntity, List<String> entityPropertiesList) throws IOException {
        String serviceImplName = selectedEntity + "ServiceImpl";
        Path filePath = serviceDirectoryPath.resolve(serviceImplName + ".java");

        StringBuilder code = new StringBuilder();
        code.append(ServiceFileWriter.writeFile(selectedEntity, entityPropertiesList));

        Files.write(filePath, code.toString().getBytes(StandardCharsets.UTF_8));
        log.info("Interface created at " + filePath);
    }
}
