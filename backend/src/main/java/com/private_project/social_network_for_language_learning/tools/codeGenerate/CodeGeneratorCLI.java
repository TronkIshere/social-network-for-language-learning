package com.private_project.social_network_for_language_learning.tools.codeGenerate;

import com.private_project.social_network_for_language_learning.tools.codeGenerate.generator.*;
import com.private_project.social_network_for_language_learning.tools.codeGenerate.utils.EntityUtils;
import com.private_project.social_network_for_language_learning.tools.codeGenerate.utils.ProjectPathUtils;
import com.private_project.social_network_for_language_learning.tools.codeGenerate.utils.StringUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@ShellComponent
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CodeGeneratorCLI {
    @ShellMethod(key = "addEntity", value = "Generate a new Entity class")
    public String generateEntity(String name, String fields) throws IOException {
        String className = StringUtils.capitalize(name);
        Path entitiesDirectoryPath = ProjectPathUtils.getOrCreateDirectory("entity");

        EntityCodeGenerator.createFile(entitiesDirectoryPath, fields, className);

        return "Entity created at " + entitiesDirectoryPath;
    }

    @ShellMethod(key = "addService", value = "Generate a new service class")
    public String generateService() throws IOException, ClassNotFoundException {
        Path entitiesDirectoryPath = getEntitiesDirectoryPath();

        List<String> entityClasses = EntityUtils.getEntityClasses(entitiesDirectoryPath);
        if (entityClasses.isEmpty()) return "Not Entity has found!";

        String selectedEntity = EntityUtils.selectEntityTable(entityClasses);
        if (selectedEntity == null) return "Not Entity selected";

        List<String> entityPropertiesList = EntityUtils.getEntityProperties(selectedEntity);

        createAllCrudFiles(selectedEntity, entityPropertiesList);
        return "Created controller, service, repository is completed";
    }

    private Path getEntitiesDirectoryPath() throws IOException {
        return ProjectPathUtils.getOrCreateDirectory("entity");
    }

    private void createAllCrudFiles(String selectedEntity, List<String> entityPropertiesList) throws IOException {
        Path responseDirectoryPath = ProjectPathUtils.getOrCreateDirectory("dto/response/" + selectedEntity.toLowerCase());
        Path requestDirectoryPath = ProjectPathUtils.getOrCreateDirectory("dto/request/" + selectedEntity.toLowerCase());
        Path repoDirectoryPath = ProjectPathUtils.getOrCreateDirectory("repository");
        Path interfaceDirectoryPath = ProjectPathUtils.getOrCreateDirectory("service");
        Path serviceDirectoryPath = ProjectPathUtils.getOrCreateDirectory("service/impl");
        Path controllerDirectoryPath = ProjectPathUtils.getOrCreateDirectory("controller");
        Path mapperDirectoryPath = ProjectPathUtils.getOrCreateDirectory("mapper");

        RepositoryCodeGenerator.createFile(repoDirectoryPath, selectedEntity);
        InterfaceCodeGenerator.createFile(interfaceDirectoryPath, selectedEntity);
        ServiceCodeGenerator.createFile(serviceDirectoryPath, selectedEntity, entityPropertiesList);
        ControllerCodeGenerator.createFile(controllerDirectoryPath, selectedEntity);
        MapperCodeGenerator.createFile(mapperDirectoryPath, selectedEntity, entityPropertiesList);
        RequestCodeGenerator.createFile(requestDirectoryPath, "Upload" + selectedEntity + "Request", selectedEntity, entityPropertiesList);
        RequestCodeGenerator.createFile(requestDirectoryPath, "Update" + selectedEntity + "Request", selectedEntity, entityPropertiesList);
        ResponseCodeGenerator.createFile(responseDirectoryPath, selectedEntity + "Response", selectedEntity, entityPropertiesList);
    }
}

