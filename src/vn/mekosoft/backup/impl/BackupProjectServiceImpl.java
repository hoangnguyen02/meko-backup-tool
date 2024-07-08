package vn.mekosoft.backup.impl;

import java.io.File;
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
        File configFile = new File(CONFIG_FOLDER_PATH);

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                initializeConfigFile(configFile);
            } catch (IOException e) {
                e.printStackTrace();
                return projects; 
            }
        }

        // Tiếp tục đọc dữ liệu từ file cấu hình như đã làm trước đó
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
    private void initializeConfigFile(File configFile) throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("backupProjects", new JsonArray());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(jsonObject, writer);
        }
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
        for (int i = 0; i < projects.size(); i++) {
            BackupProject project = projects.get(i);
            if (project.getProjectId() == projectId) {
                projects.remove(i);
                break; // Sau khi xoá, thoát khỏi vòng lặp
            }
        }
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
