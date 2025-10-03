package com.private_project.charitable_money_management.tools.codeGenerate.writer;

import com.private_project.charitable_money_management.tools.codeGenerate.utils.ProjectPathUtils;
import com.private_project.charitable_money_management.tools.codeGenerate.utils.StringUtils;

import java.util.List;

public class MapperFileWriter {
    public static StringBuilder writeFile(String className, List<String> entityProperties) {
        String lowerClassName = StringUtils.lowerFirst(className);
        String responseClassName = className + "Response";

        StringBuilder code = new StringBuilder();

        // Package and imports
        code.append("package ").append(ProjectPathUtils.findPackage("mapper")).append(";\n\n");
        code.append("import ").append(ProjectPathUtils.findPackage("entity")).append(".").append(className).append(";\n");
        code.append("import ").append(ProjectPathUtils.findPackage("response")).append("." + lowerClassName).append(".").append(responseClassName).append(";\n");
        code.append("import java.util.List;\n");
        code.append("import java.util.stream.Collectors;\n\n");

        // Class definition
        code.append("public class ").append(className).append("Mapper {\n");
        code.append("\tpublic ").append(className).append("Mapper() {\n");
        code.append("\t}\n\n");

        // toResponse method
        code.append("\tpublic static ").append(responseClassName).append(" toResponse(").append(className).append(" ").append(lowerClassName).append(") {\n");
        code.append("\t\treturn ").append(responseClassName).append(".builder()\n");

        for (String property : entityProperties) {
            String[] parts = property.split(":");
            if (parts.length == 2) {
                String propName = parts[1].trim();
                code.append("\t\t\t.").append(propName).append("(").append(lowerClassName).append(".get").append(StringUtils.capitalize(propName)).append("())\n");
            }
        }
        code.append("\t\t\t.build();\n");
        code.append("\t}\n\n");

        // toResponseList method
        code.append("\tpublic static List<").append(responseClassName).append("> toResponseList(List<").append(className).append("> ").append(lowerClassName).append("s) {\n");
        code.append("\t\treturn ").append(lowerClassName).append("s.stream()\n");
        code.append("\t\t\t.map(").append(className).append("Mapper::toResponse)\n");
        code.append("\t\t\t.collect(Collectors.toList());\n");
        code.append("\t}\n");

        code.append("}\n");

        return code;
    }
}