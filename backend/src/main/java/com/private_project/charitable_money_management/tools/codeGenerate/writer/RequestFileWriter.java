package com.private_project.charitable_money_management.tools.codeGenerate.writer;

import com.private_project.charitable_money_management.tools.codeGenerate.utils.ProjectPathUtils;
import com.private_project.charitable_money_management.tools.codeGenerate.utils.RequiredImports;

import java.util.List;

public class RequestFileWriter {
    public static StringBuilder writeFile(List<String> fields, String requestName, String selectedEntity) {
        StringBuilder code = new StringBuilder();

        // Package and imports
        code.append("package ").append(ProjectPathUtils.findPackage("request")).append(".").append(selectedEntity.toLowerCase()).append(";\n\n");
        code.append("import lombok.*;\n");
        code.append("import java.io.Serializable;\n");
        code.append("import lombok.experimental.FieldDefaults;\n");
        code.append(RequiredImports.getRequiredImports(String.join(", ", fields)));

        // Class definition
        code.append("@Getter\n");
        code.append("@Setter\n");
        code.append("@NoArgsConstructor\n");
        code.append("@AllArgsConstructor\n");
        code.append("@Builder\n");
        code.append("@FieldDefaults(level = AccessLevel.PRIVATE)\n");
        code.append("public class ").append(requestName).append(" implements Serializable {\n");

        // Fields
        for (String field : fields) {
            String[] parts = field.split(" ");
            if (parts.length == 2) {
                String type = parts[0];
                String name = parts[1];
                code.append("\t").append(type).append(" ").append(name).append(";\n");
            }
        }
        code.append("UUID id;\n");

        code.append("}\n");
        return code;
    }
}
