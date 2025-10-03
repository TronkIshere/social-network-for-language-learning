package com.private_project.social_network_for_language_learning.tools.codeGenerate.writer;

import com.private_project.social_network_for_language_learning.tools.codeGenerate.utils.ProjectPathUtils;
import com.private_project.social_network_for_language_learning.tools.codeGenerate.utils.StringUtils;

public class ControllerFileWriter {
    public static StringBuilder writeFile(String selectedEntity) {

        StringBuilder code = new StringBuilder();
        //add imports
        code.append("package ").append(ProjectPathUtils.findPackage("controller")).append(";\n\n");
        code.append("import ").append(ProjectPathUtils.findPackage("common")).append(".ResponseAPI").append(";\n");
        code.append("import ").append(ProjectPathUtils.findPackage("request")).append(".").append(StringUtils.lowerFirst(selectedEntity)).append(".Upload").append(selectedEntity).append("Request").append(";\n");
        code.append("import ").append(ProjectPathUtils.findPackage("request")).append(".").append(StringUtils.lowerFirst(selectedEntity)).append(".Update").append(selectedEntity).append("Request").append(";\n");
        code.append("import ").append(ProjectPathUtils.findPackage("response")).append(".").append(StringUtils.lowerFirst(selectedEntity)).append(".").append(selectedEntity).append("Response").append(";\n");
        code.append("import ").append(ProjectPathUtils.findPackage("service")).append(".").append(selectedEntity).append("Service;\n");
        code.append("import org.springframework.http.HttpStatus;\n");
        code.append("import lombok.RequiredArgsConstructor;\n");
        code.append("import org.springframework.web.bind.annotation.*;\n");
        code.append("import java.util.UUID;\n\n");

        // Class definition
        code.append("@RestController\n");
        code.append("@RequiredArgsConstructor\n");
        code.append("@RequestMapping(\"/api/v1/").append(StringUtils.lowerFirst(selectedEntity)).append("s\")\n");
        code.append("public class ").append(selectedEntity).append("Controller {\n\n");
        code.append("\tprivate final ").append(selectedEntity).append("Service ").append(StringUtils.lowerFirst(selectedEntity)).append("Service;\n\n");

        // Create Method
        code.append("\t@PostMapping\n");
        code.append("\tpublic ResponseAPI<").append(selectedEntity).append("Response> create").append(selectedEntity).append("(\n");
        code.append("\t\t\t@RequestBody Upload").append(selectedEntity).append("Request request) {\n");
        code.append("\t\tvar result = ").append(StringUtils.lowerFirst(selectedEntity)).append("Service.create").append(selectedEntity).append("(request);\n");
        code.append("\t\treturn ResponseAPI.<").append(selectedEntity).append("Response>builder()\n");
        code.append("\t\t\t.code(HttpStatus.OK.value())\n");
        code.append("\t\t\t.message(\"success\")\n");
        code.append("\t\t\t.data(result)\n");
        code.append("\t\t\t.build();\n");
        code.append("\t}\n\n");

        // Get By ID Method
        code.append("\t@GetMapping(\"/{id}\")\n");
        code.append("\tpublic ResponseAPI<").append(selectedEntity).append("Response> get").append(selectedEntity).append("ById(@PathVariable UUID id) {\n");
        code.append("\t\tvar result = ").append(StringUtils.lowerFirst(selectedEntity)).append("Service.get").append(selectedEntity).append("ById(id);\n");
        code.append("\t\treturn ResponseAPI.<").append(selectedEntity).append("Response>builder()\n");
        code.append("\t\t\t.code(HttpStatus.OK.value())\n");
        code.append("\t\t\t.message(\"success\")\n");
        code.append("\t\t\t.data(result)\n");
        code.append("\t\t\t.build();\n");
        code.append("\t}\n\n");

        // Get all method
        // Get all method
        code.append("\t@GetMapping(\"/list\")\n");
        code.append("\tpublic ResponseAPI<List<").append(selectedEntity).append("Response>> getAll")
                .append(selectedEntity).append("s() {\n");
        code.append("\t\tvar result = ").append(StringUtils.lowerFirst(selectedEntity))
                .append("Service.getAll").append(selectedEntity).append("s();\n");
        code.append("\t\treturn ResponseAPI.<List<").append(selectedEntity).append("Response>>builder()\n");
        code.append("\t\t\t.code(HttpStatus.OK.value())\n");
        code.append("\t\t\t.message(\"success\")\n");
        code.append("\t\t\t.data(result)\n");
        code.append("\t\t\t.build();\n");
        code.append("\t}\n\n");


        // Update Method
        code.append("\t@PutMapping\n");
        code.append("\tpublic ResponseAPI<").append(selectedEntity).append("Response> update").append(selectedEntity).append("(\n");
        code.append("\t\t\t@RequestBody Update").append(selectedEntity).append("Request request) {\n");
        code.append("\t\tvar result = ").append(StringUtils.lowerFirst(selectedEntity)).append("Service.update").append(selectedEntity).append("(request);\n");
        code.append("\t\treturn ResponseAPI.<").append(selectedEntity).append("Response>builder()\n");
        code.append("\t\t\t.code(HttpStatus.OK.value())\n");
        code.append("\t\t\t.message(\"success\")\n");
        code.append("\t\t\t.data(result)\n");
        code.append("\t\t\t.build();\n");
        code.append("\t}\n\n");

        // Delete Method
        code.append("\t@DeleteMapping(\"/{id}\")\n");
        code.append("\tpublic ResponseAPI<String> delete").append(selectedEntity).append("(@PathVariable UUID id) {\n");
        code.append("\t\t").append(StringUtils.lowerFirst(selectedEntity)).append("Service.delete").append(selectedEntity).append("ById(id);\n");
        code.append("\t\treturn ResponseAPI.<String>builder()\n");
        code.append("\t\t\t.code(HttpStatus.OK.value())\n");
        code.append("\t\t\t.message(\"").append(selectedEntity).append(" deleted successfully\")\n");
        code.append("\t\t\t.data(\"success\")\n");
        code.append("\t\t\t.build();\n");
        code.append("\t}\n\n");

        // Soft Delete Method
        /*code.append("\t@PutMapping(\"/soft-delete/{id}\")\n");
        code.append("\tpublic ResponseAPI<String> softDelete").append(selectedEntity).append("(@PathVariable UUID id) {\n");
        code.append("\t\t").append(StringUtils.lowerFirst(selectedEntity)).append("Service.softDelete").append(selectedEntity).append("(id);\n");
        code.append("\t\treturn ResponseAPI.<String>builder()\n");
        code.append("\t\t\t.code(HttpStatus.OK.value())\n");
        code.append("\t\t\t.message(\"success\")\n");
        code.append("\t\t\t.data(\"success\")\n");
        code.append("\t\t\t.build();\n");
        code.append("\t}\n");*/

        code.append("}\n");
        return code;
    }
}
