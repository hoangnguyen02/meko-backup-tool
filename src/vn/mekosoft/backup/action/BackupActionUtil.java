package vn.mekosoft.backup.action;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.config.Config;

public class BackupActionUtil {
    private static long currentMaxProjectId = 0;

    public static void addProject(String projectName, String description, String hostname, String username,
            String password, BackupProjectStatus projectStatus) {

        Config config = new Config();
        String CONFIG_FOLDER_PATH = config.getConfigFolderPath();

        List<BackupProject> backupProjects = new BackupServiceImpl().loadData();
        
        for (BackupProject project : backupProjects) {
            if (project.getProjectId() > currentMaxProjectId) {
                currentMaxProjectId = project.getProjectId();
            }
        }
        
        long projectId = currentMaxProjectId + 1;

        for (BackupProject project : backupProjects) {
            if (project.getProjectId() == projectId) {
                currentMaxProjectId++;
                return;
            }
        }

        BackupProject newProject = new BackupProject();
        newProject.setProjectId(projectId);
        newProject.setProjectName(projectName);
        newProject.setDescription(description);
        newProject.setHostname(hostname);
        newProject.setUsername(username);
        newProject.setPassword(password);
        newProject.setBackupProjectStatusFromEnum(projectStatus);
        newProject.setBackupTasks(new ArrayList<>());
        backupProjects.add(newProject);

        try (FileWriter writer = new FileWriter(CONFIG_FOLDER_PATH)) {
            JsonArray backupProjectsArray = new JsonArray();
            for (BackupProject project : backupProjects) {
                JsonObject projectJson = new JsonObject();
                projectJson.addProperty("projectId", project.getProjectId());
                projectJson.addProperty("projectName", project.getProjectName());
                projectJson.addProperty("description", project.getDescription());
                projectJson.addProperty("hostname", project.getHostname());
                projectJson.addProperty("username", project.getUsername());
                projectJson.addProperty("password", project.getPassword());
                projectJson.addProperty("backupProjectStatus", project.getBackupProjectStatus());

                JsonArray backupTasksArray = new JsonArray();
                for (BackupTask task : project.getBackupTasks()) {
                    JsonObject taskJson = new JsonObject();
                    taskJson.addProperty("backupTaskId", task.getBackupTaskId());
                    taskJson.addProperty("projectId", task.getProjectId());
                    taskJson.addProperty("name", task.getName());
                    taskJson.addProperty("localSchedular", task.getLocalSchedular());	
                    taskJson.addProperty("localPath", task.getLocalPath());
                    taskJson.addProperty("localRetention", task.getLocalRetention());
                    taskJson.addProperty("remoteSchedular", task.getRemoteSchedular());
                    taskJson.addProperty("remotePath", task.getRemotePath());
                    taskJson.addProperty("remoteRetention", task.getRemoteRetention());
                    taskJson.addProperty("backupTaskStatus", task.getBackupTaskStatus());

                    JsonArray backupFoldersArray = new JsonArray();
                    for (BackupFolder folder : task.getBackupFolders()) {
                        JsonObject folderJson = new JsonObject();
                        folderJson.addProperty("backupFolderId", folder.getBackupFolderId());
                        folderJson.addProperty("folderPath", folder.getFolderPath());
                        folderJson.addProperty("backupTaskId", folder.getBackupTaskId());
                        backupFoldersArray.add(folderJson);
                    }
                    taskJson.add("backupFolders", backupFoldersArray);
                    backupTasksArray.add(taskJson);
                }
                projectJson.add("backupTasks", backupTasksArray);
                backupProjectsArray.add(projectJson);
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("backupProjects", backupProjectsArray);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(jsonObject, writer);
            AlertMaker.successfulAlert("Success", "Project added successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            AlertMaker.errorAlert("Error", "Failed to add project. Please check the logs for more details.");
        }
    }
}
