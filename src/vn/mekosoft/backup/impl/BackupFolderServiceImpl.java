package vn.mekosoft.backup.impl;

import com.google.gson.*;
import vn.mekosoft.backup.config.ConfigReader;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.service.BackupFolderService;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackupFolderServiceImpl implements BackupFolderService {
    private final ConfigReader config = new ConfigReader();
    private final String CONFIG_FOLDER_PATH = config.getConfigFolderPath();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public List<BackupFolder> loadFolderData(long projectId, long taskId) {
        List<BackupFolder> folders = new ArrayList<>();
        try (FileReader reader = new FileReader(CONFIG_FOLDER_PATH)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            if (jsonObject != null && jsonObject.has("backupProjects")) {
                JsonArray projectsArray = jsonObject.getAsJsonArray("backupProjects");
                for (JsonElement projectElement : projectsArray) {
                    BackupProject project = gson.fromJson(projectElement, BackupProject.class);
                    if (project != null && project.getProjectId() == projectId) {
                        for (BackupTask task : project.getBackupTasks()) {
                            if (task.getBackupTaskId() == taskId) {
                                for (BackupFolder folder : task.getBackupFolders()) {
                                    folder.setBackupTaskId(task.getBackupTaskId()); 
                                    folders.add(folder);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folders;
    }

    @Override
    public void updateBackupFolder(long projectId, long taskId, long folderId, BackupFolder updatedFolder) {
        List<BackupProject> projects = loadProjectData();
        for (BackupProject project : projects) {
            if (project.getProjectId() == projectId) {
                for (BackupTask task : project.getBackupTasks()) {
                    if (task.getBackupTaskId() == taskId) {
                        for (BackupFolder folder : task.getBackupFolders()) {
                            if (folder.getBackupFolderId() == folderId) {
                                folder.setFolderPath(updatedFolder.getFolderPath());
                                saveProjectsToFile(projects);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void deleteBackupFolder(long projectId, long taskId, long folderId) {
        List<BackupProject> projects = loadProjectData();
        for (BackupProject project : projects) {
            if (project.getProjectId() == projectId) {
                for (BackupTask task : project.getBackupTasks()) {
                    if (task.getBackupTaskId() == taskId) {
                        task.getBackupFolders().removeIf(folder -> folder.getBackupFolderId() == folderId);
                    }
                }
                saveProjectsToFile(projects);
                return;
            }
        }
    }

    private List<BackupProject> loadProjectData() {
        List<BackupProject> projects = new ArrayList<>();
        try (FileReader reader = new FileReader(CONFIG_FOLDER_PATH)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            if (jsonObject != null && jsonObject.has("backupProjects")) {
                JsonArray projectsArray = jsonObject.getAsJsonArray("backupProjects");
                for (JsonElement projectElement : projectsArray) {
                    BackupProject project = gson.fromJson(projectElement, BackupProject.class);
                    if (project != null) {
                        List<BackupTask> tasks = new ArrayList<>();
                        for (JsonElement taskElement : projectElement.getAsJsonObject().getAsJsonArray("backupTasks")) {
                            BackupTask task = gson.fromJson(taskElement, BackupTask.class);
                            List<BackupFolder> folders = new ArrayList<>();
                            for (JsonElement folderElement : taskElement.getAsJsonObject().getAsJsonArray("backupFolders")) {
                                BackupFolder folder = gson.fromJson(folderElement, BackupFolder.class);
                                folder.setBackupTaskId(task.getBackupTaskId()); // Gán lại backupTaskId từ task
                                folders.add(folder);
                            }
                            task.setBackupFolders(folders);
                            tasks.add(task);
                        }
                        project.setBackupTasks(tasks);
                        projects.add(project);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }
        return projects;
    }

    private void saveProjectsToFile(List<BackupProject> projects) {
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
