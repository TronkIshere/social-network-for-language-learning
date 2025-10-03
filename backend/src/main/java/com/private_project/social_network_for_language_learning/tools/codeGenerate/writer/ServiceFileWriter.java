package com.private_project.social_network_for_language_learning.tools.codeGenerate.writer;

import com.private_project.social_network_for_language_learning.tools.codeGenerate.utils.ProjectPathUtils;
import com.private_project.social_network_for_language_learning.tools.codeGenerate.utils.StringUtils;

import java.util.List;

public class ServiceFileWriter {
    public static StringBuilder writeFile(String selectedEntity, List<String> entityPropertiesList) {
        String entityVarName = StringUtils.lowerFirst(selectedEntity);
        String responseClassName = selectedEntity + "Response";

        StringBuilder code = new StringBuilder();
        // Add imports
        code.append("package ").append(ProjectPathUtils.findPackage("service")).append(".impl;\n\n");
        code.append("import ").append(ProjectPathUtils.findPackage("request")).append(".").append(StringUtils.lowerFirst(selectedEntity)).append(".Upload").append(selectedEntity).append("Request").append(";\n");
        code.append("import ").append(ProjectPathUtils.findPackage("request")).append(".").append(StringUtils.lowerFirst(selectedEntity)).append(".Update").append(selectedEntity).append("Request").append(";\n");
        code.append("import ").append(ProjectPathUtils.findPackage("response")).append(".").append(StringUtils.lowerFirst(selectedEntity)).append(".").append(selectedEntity).append("Response").append(";\n");
        code.append("import ").append(ProjectPathUtils.findPackage("entity")).append(".").append(selectedEntity).append(";\n");
        code.append("import ").append(ProjectPathUtils.findPackage("repository")).append(".").append(selectedEntity).append("Repository;\n");
        code.append("import ").append(ProjectPathUtils.findPackage("service")).append(".").append(selectedEntity).append("Service;\n");
        code.append("import ").append(ProjectPathUtils.findPackage("mapper")).append(".").append(selectedEntity).append("Mapper;\n");
        code.append("import jakarta.persistence.EntityNotFoundException;\n");
        code.append("import lombok.AccessLevel;\n");
        code.append("import lombok.RequiredArgsConstructor;\n");
        code.append("import lombok.experimental.FieldDefaults;\n");
        code.append("import org.springframework.data.domain.Page;\n");
        code.append("import org.springframework.data.domain.Pageable;\n");
        code.append("import org.springframework.stereotype.Service;\n");
        code.append("import java.time.LocalDateTime;\n");
        code.append("import java.util.UUID;\n");
        code.append("import java.util.List;\n\n");

        // Class definition
        code.append("@Service\n");
        code.append("@RequiredArgsConstructor\n");
        code.append("@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)\n");
        code.append("public class ").append(selectedEntity).append("ServiceImpl implements ").append(selectedEntity).append("Service {\n");
        code.append("\t").append(selectedEntity).append("Repository ").append(entityVarName).append("Repository;\n");

        // Create method
        code.append("\t@Override\n");
        code.append("\tpublic ").append(responseClassName).append(" create").append(selectedEntity)
                .append("(Upload").append(selectedEntity).append("Request request) {\n");
        code.append("\t\t").append(selectedEntity).append(" ").append(entityVarName).append(" = new ").append(selectedEntity).append("();\n");

        for (String property : entityPropertiesList) {
            String[] parts = property.split(" ");
            if (parts.length >= 2) {
                String propType = parts[0];
                String propName = parts[1].trim();
                String capitalizedPropName = StringUtils.capitalize(propName);
                code.append("\t\t").append(entityVarName).append(".set").append(capitalizedPropName)
                        .append("(request.get").append(capitalizedPropName).append("());\n");
            }
        }

        code.append("\t\t").append(selectedEntity).append(" savedEntity = ")
                .append(entityVarName).append("Repository.save(").append(entityVarName).append(");\n");
        code.append("\t\treturn ").append(StringUtils.upperFirst(entityVarName)).append("Mapper.toResponse(savedEntity);\n");
        code.append("\t}\n\n");

        // Get all method
        code.append("\t@Override\n");
        code.append("\tpublic List<").append(responseClassName).append("> getAll").append(selectedEntity).append("s() {\n");
        code.append("\t\treturn ").append(StringUtils.upperFirst(entityVarName)).append("Mapper.toResponseList(")
                .append(entityVarName).append("Repository.findAll());\n");
        code.append("\t}\n\n");

        // Get by ID method
        code.append("\t@Override\n");
        code.append("\tpublic ").append(responseClassName).append(" get").append(selectedEntity).append("ById(UUID id) {\n");
        code.append("\t\treturn ").append(StringUtils.upperFirst(entityVarName)).append("Mapper.toResponse(\n");
        code.append("\t\t\t").append(entityVarName).append("Repository.findById(id)\n");
        code.append("\t\t\t\t.orElseThrow(() -> new EntityNotFoundException(\"").append(selectedEntity).append(" not found\")));\n");
        code.append("\t}\n\n");

        // Update method
        code.append("\t@Override\n");
        code.append("\tpublic ").append(responseClassName).append(" update").append(selectedEntity).append("(Update").append(selectedEntity).append("Request request) {\n");
        code.append("\t\t").append(selectedEntity).append(" entity = ").append(entityVarName).append("Repository.findById(request.getId())\n");
        code.append("\t\t\t.orElseThrow(() -> new EntityNotFoundException(\"").append(selectedEntity).append(" not found\"));\n");
        code.append("\t\t").append(selectedEntity).append(" ").append(entityVarName).append(" = new ").append(selectedEntity).append("();\n");

        for (String property : entityPropertiesList) {
            String[] parts = property.split(" ");
            if (parts.length >= 2) {
                String propName = parts[1].trim();
                String capitalizedPropName = StringUtils.capitalize(propName);
                code.append("\t\t").append(entityVarName).append(".set").append(capitalizedPropName)
                        .append("(request.get").append(capitalizedPropName).append("());\n");
            }
        }

        code.append("\t\treturn ").append(StringUtils.upperFirst(entityVarName)).append("Mapper.toResponse(")
                .append(entityVarName).append("Repository.save(entity));\n");
        code.append("\t}\n\n");

        // Delete method
        code.append("\t@Override\n");
        code.append("\tpublic void delete").append(selectedEntity).append("ById(UUID id) {\n");
        code.append("\t\t").append(selectedEntity).append(" entity = ").append(entityVarName).append("Repository.findById(id)\n");
        code.append("\t\t\t.orElseThrow(() -> new EntityNotFoundException(\"").append(selectedEntity).append(" not found\"));\n");
        code.append("\t\t").append(entityVarName).append("Repository.delete(entity);\n");
        code.append("\t}\n\n");

        // Soft delete method
        code.append("\t@Override\n");
        code.append("\tpublic String softDelete").append(selectedEntity).append("(UUID id) {\n");
        code.append("\t\t").append(selectedEntity).append(" entity = ").append(entityVarName).append("Repository.findById(id)\n");
        code.append("\t\t\t.orElseThrow(() -> new EntityNotFoundException(\"").append(selectedEntity).append(" not found\"));\n");
        code.append("\t\tentity.setDeletedAt(LocalDateTime.now());\n");
        code.append("\t\t").append(entityVarName).append("Repository.save(entity);\n");
        code.append("\t\treturn \"")
                .append(selectedEntity).append(" with ID \" + id + \" has been soft deleted\";\n");
        code.append("\t}\n");

        code.append("}\n");
        return code;
    }
}