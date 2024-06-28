package vn.mekosoft.backup.impl;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import vn.mekosoft.backup.config.ConfigReader;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.service.BackupProjectService;

public class BackupProjectServiceImpl implements BackupProjectService {
    private ConfigReader config = new ConfigReader();
    private String CONFIG_FOLDER_PATH = config.getConfigFolderPath();

    @Override
    public List<BackupProject> loadProjectData() {
        List<BackupProject> projects = new ArrayList<>();
        try (FileReader reader = new FileReader(CONFIG_FOLDER_PATH)) {
            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
            JsonArray backupProjectsArray = jsonObject.getAsJsonArray("backupProjects");
            if (backupProjectsArray != null) {
                for (JsonElement projectElement : backupProjectsArray) {
                    BackupProject project = new Gson().fromJson(projectElement, BackupProject.class);
                    projects.add(project);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return projects;
    }

    @Override
    public void addProject(BackupProject backupProject) {
        List<BackupProject> projects = loadProjectData();
        projects.add(backupProject);
        saveProjects(projects);
    }

    @Override
    public void updateProject(long projectId, BackupProject updatedProject) {
        List<BackupProject> projects = loadProjectData();
        for (BackupProject project : projects) {
            if (project.getProjectId() == projectId) {
                project.setProjectName(updatedProject.getProjectName());
                project.setDescription(updatedProject.getDescription());
                project.setHostname(updatedProject.getHostname());
                project.setUsername(updatedProject.getUsername());
                project.setPassword(updatedProject.getPassword());
                project.setBackupProjectStatus(updatedProject.getBackupProjectStatus());
                break;
            }
        }
        saveProjects(projects);
    }

    @Override
    public void deleteProject(long projectId) {
        List<BackupProject> projects = loadProjectData();
        projects.removeIf(project -> project.getProjectId() == projectId);
        saveProjects(projects);
    }

    private void saveProjects(List<BackupProject> projects) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = gson.toJsonTree(projects).getAsJsonArray();
        jsonObject.add("backupProjects", jsonArray);

        try (FileWriter writer = new FileWriter(CONFIG_FOLDER_PATH)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
