package com.private_project.charitable_money_management.tools.codeGenerate.generator;

import com.private_project.charitable_money_management.tools.codeGenerate.writer.MapperFileWriter;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Slf4j
public class MapperCodeGenerator {
    public static void createFile(Path mapperDirectoryPath, String className, List<String> entityProperties) throws IOException {
        Path filePath = mapperDirectoryPath.resolve(className + "Mapper.java");

        StringBuilder code = new StringBuilder();
        code.append(MapperFileWriter.writeFile(className, entityProperties));

        Files.write(filePath, code.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        log.info("Mapper created at {}", filePath);
    }
}