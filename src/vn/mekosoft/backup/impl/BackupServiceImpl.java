package vn.mekosoft.backup.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;
import vn.mekosoft.backup.service.BackupService;

public class BackupServiceImpl implements BackupService {
	// String JSON_FILE_PATH = "C:\\Users\\Hoang Nguyen\\eclipse-workspace\\meko-backup-tool\\src\\vn\\mekosoft\\backup\\json\\backup_data.json";
	String JSON_FILE_PATH = "C:\\Users\\Hoang Nguyen\\eclipse-workspace\\meko-backup-tool\\new_project.json";
	 @Override
	    public List<BackupProject> loadData() {
	        List<BackupProject> backupProjects = new ArrayList<>();
	        try {
	            File file = new File(JSON_FILE_PATH);
	            if (!file.exists() || file.length() == 0) {
	                return backupProjects;
	            }

	            JsonReader jsonReader = new JsonReader(new FileReader(JSON_FILE_PATH));
	            JsonObject jsonObject = JsonParser.parseReader(jsonReader).getAsJsonObject();
	            JsonArray jsonArray = jsonObject.getAsJsonArray("backupProjects");

	            for (JsonElement projectElement : jsonArray) {
	                BackupProject backupProject = parseProject(projectElement.getAsJsonObject());
	                backupProjects.add(backupProject);
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }

	        return backupProjects;
	    }

	    private BackupProject parseProject(JsonObject projectObject) {
	        int projectId = projectObject.get("projectId").getAsInt();
	        //String projectName = projectObject.get("projectName").getAsString();
	        String hostname = projectObject.get("hostname").getAsString();
	        String username = projectObject.get("username").getAsString();
	        String password = projectObject.get("password").getAsString();

	        List<BackupTask> backupTasks = new ArrayList<>();
	        JsonArray tasksArray = projectObject.getAsJsonArray("backupTasks");
	        if (tasksArray != null) {
	            for (JsonElement taskElement : tasksArray) {
	                BackupTask backupTask = parseTask(taskElement.getAsJsonObject(), projectId);
	                backupTasks.add(backupTask);
	            }
	        }

	        return new BackupProject(projectId, null, null, hostname, username,
	                password, BackupProjectStatus.DANG_BIEN_SOAN.getId(), backupTasks);
	    }

	    private BackupTask parseTask(JsonObject taskObject, int projectId) {
	        int backupTaskId = taskObject.get("backupTaskId").getAsInt();
	        String name = taskObject.get("name").getAsString();
	        String localSchedular = taskObject.get("localSchedular").getAsString();
	        String remoteSchedular = taskObject.get("remoteSchedular").getAsString();
	        String localPath = taskObject.get("localPath").getAsString();
	        String remotePath = taskObject.get("remotePath").getAsString();
	        int localRetention = taskObject.get("localRetention").getAsInt();
	        int remoteRetention = taskObject.get("remoteRetention").getAsInt();

	        List<BackupFolder> backupFolders = new ArrayList<>();
	        JsonArray foldersArray = taskObject.getAsJsonArray("backupfolders");
	        if (foldersArray != null) {
	            for (JsonElement folderElement : foldersArray) {
	                BackupFolder backupFolder = parseFolder(folderElement.getAsJsonObject(), backupTaskId);
	                backupFolders.add(backupFolder);
	            }
	        }

	        return new BackupTask(backupTaskId, projectId, name, localSchedular, localPath,
	                localRetention, remoteSchedular, remotePath, remoteRetention,
	                BackupTaskStatus.HOAT_DONG.getId(), backupFolders);
	    }

	    private BackupFolder parseFolder(JsonObject folderObject, int backupTaskId) {
	        int folderId = folderObject.get("folderId").getAsInt();
	        String folderPath = folderObject.get("folderPath").getAsString();
	        return new BackupFolder(folderId, folderPath, backupTaskId);
	    }
	    
	    public List<BackupTask> getTasksForProject(int projectId) {
	        List<BackupTask> tasksForProject = new ArrayList<>();
	        List<BackupProject> backupProjects = loadData();

	        // Tìm dự án tương ứng với projectId
	        for (BackupProject project : backupProjects) {
	            if (project.getProjectId() == projectId) {
	                tasksForProject.addAll(project.getBackupTasks());
	                break;
	            }
	        }

	        return tasksForProject;
	    }


}
