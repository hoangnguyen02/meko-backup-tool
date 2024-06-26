package vn.mekosoft.backup.impl;

import java.io.FileNotFoundException;
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
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.service.BackupService;

public class BackupServiceImpl implements BackupService {

    private ConfigReader config = new ConfigReader();
    private String CONFIG_FOLDER_PATH = config.getConfigFolderPath();

    @Override
    public List<BackupProject> loadData() {
        Gson gson = new Gson();
        List<BackupProject> backupProjects = new ArrayList<>();

        try (FileReader reader = new FileReader(CONFIG_FOLDER_PATH)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            if (jsonObject != null && jsonObject.has("backupProjects")) {
                JsonArray backupProjectsArray = jsonObject.getAsJsonArray("backupProjects");
                for (JsonElement projectElement : backupProjectsArray) {
                    JsonObject projectObject = projectElement.getAsJsonObject();
                    BackupProject project = gson.fromJson(projectObject, BackupProject.class);

                    JsonArray tasksArray = projectObject.getAsJsonArray("backupTasks");
                    List<BackupTask> tasks = new ArrayList<>();
                    if (tasksArray != null) {
                        for (JsonElement taskElement : tasksArray) {
                            JsonObject taskObject = taskElement.getAsJsonObject();
                            BackupTask task = gson.fromJson(taskObject, BackupTask.class);

                            JsonArray foldersArray = taskObject.getAsJsonArray("backupFolders");
                            List<BackupFolder> folders = new ArrayList<>();
                            if (foldersArray != null) {
                                for (JsonElement folderElement : foldersArray) {
                                    BackupFolder folder = gson.fromJson(folderElement, BackupFolder.class);
                                    folders.add(folder);
                                }
                            } else {
                                System.out.println("No folders");
                            }
                            task.setBackupFolders(folders);
                            tasks.add(task);
                        }
                    }
                    project.setBackupTasks(tasks);
                    backupProjects.add(project);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Config file not found: " + CONFIG_FOLDER_PATH);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading config file: " + CONFIG_FOLDER_PATH);
            e.printStackTrace();
        }

        return backupProjects;
    }

    @Override
    public void saveData(List<BackupProject> backupProjects) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = gson.toJsonTree(backupProjects).getAsJsonArray();
        jsonObject.add("backupProjects", jsonArray);

        try (FileWriter writer = new FileWriter(CONFIG_FOLDER_PATH)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            System.err.println("Error writing to config file: " + CONFIG_FOLDER_PATH);
            e.printStackTrace();
        }
    }
}
