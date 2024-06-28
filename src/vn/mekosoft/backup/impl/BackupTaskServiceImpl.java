package vn.mekosoft.backup.impl;

import com.google.gson.*;
import vn.mekosoft.backup.config.ConfigReader;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupProject; // Thêm import BackupProject
import vn.mekosoft.backup.service.BackupTaskService;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackupTaskServiceImpl implements BackupTaskService {

    private final ConfigReader config = new ConfigReader();
    private final String CONFIG_FOLDER_PATH = config.getConfigFolderPath();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public List<BackupTask> loadTaskData() {
        List<BackupTask> tasks = new ArrayList<>();
        try (FileReader reader = new FileReader(CONFIG_FOLDER_PATH)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            if (jsonObject != null && jsonObject.has("backupTasks")) {
                JsonArray tasksArray = jsonObject.getAsJsonArray("backupTasks");
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
        } catch (FileNotFoundException e) {
            System.err.println("Config file not found: " + CONFIG_FOLDER_PATH);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading config file: " + CONFIG_FOLDER_PATH);
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public void updateBackupTask(long projectId, long taskId, BackupTask updatedTask) {
        List<BackupProject> projects = loadProjectData();
        for (BackupProject project : projects) {
            if (project.getProjectId() == projectId) {
                List<BackupTask> tasks = project.getBackupTasks();
                for (BackupTask task : tasks) {
                    if (task.getBackupTaskId() == taskId) {
                        task.setProjectId(updatedTask.getProjectId());
                        task.setName(updatedTask.getName());
                        task.setLocalSchedular(updatedTask.getLocalSchedular());
                        task.setLocalPath(updatedTask.getLocalPath());
                        task.setLocalRetention(updatedTask.getLocalRetention());
                        task.setRemoteSchedular(updatedTask.getRemoteSchedular());
                        task.setRemotePath(updatedTask.getRemotePath());
                        task.setRemoteRetention(updatedTask.getRemoteRetention());
                        task.setBackupTaskStatus(updatedTask.getBackupTaskStatus());
                        task.setBackupFolders(updatedTask.getBackupFolders());
                        break;
                    }
                }
            }
        }
        saveProjectsToFile(projects);
    }


    @Override
    public void deleteBackupTask(long projectId, long taskId) {
        List<BackupProject> projects = loadProjectData();
        for (BackupProject project : projects) {
            if (project.getProjectId() == projectId) {
                List<BackupTask> tasks = project.getBackupTasks();
                tasks.removeIf(task -> task.getBackupTaskId() == taskId);
                project.setBackupTasks(tasks);
                saveProjectsToFile(projects);
                return;
            }
        }
    }

    public List<BackupProject> loadProjectData() {
        Gson gson = new Gson();
        List<BackupProject> projects = new ArrayList<>();
        try (FileReader reader = new FileReader(CONFIG_FOLDER_PATH)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            if (jsonObject != null && jsonObject.has("backupProjects")) {
                JsonArray projectsArray = jsonObject.getAsJsonArray("backupProjects");
                for (JsonElement projectElement : projectsArray) {
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
                            }
                            task.setBackupFolders(folders);
                            tasks.add(task);
                        }
                    }
                    project.setBackupTasks(tasks);
                    projects.add(project);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Project config file not found: " + CONFIG_FOLDER_PATH);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading project config file: " + CONFIG_FOLDER_PATH);
            e.printStackTrace();
        }
        return projects;
    }

    public void saveTasksToFile(List<BackupTask> tasks) {
        try (FileWriter writer = new FileWriter(CONFIG_FOLDER_PATH)) {
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveProjectsToFile(List<BackupProject> projects) {
        try (FileWriter writer = new FileWriter(CONFIG_FOLDER_PATH)) {
            JsonObject jsonObject = new JsonObject();
            JsonArray projectsArray = new JsonArray();
            for (BackupProject project : projects) {
                JsonObject projectObject = gson.toJsonTree(project).getAsJsonObject();
                projectsArray.add(projectObject);
            }
            jsonObject.add("backupProjects", projectsArray);
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
