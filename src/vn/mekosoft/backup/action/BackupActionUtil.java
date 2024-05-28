package vn.mekosoft.backup.action;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;

public class BackupActionUtil {

	public static void addProject(String projectIdStr, String projectName, String description, String hostname,
	        String username, String password, BackupProjectStatus projectStatus) {
	    long projectId = Long.parseLong(projectIdStr);


	    List<BackupProject> backupProjects = new BackupServiceImpl().loadData();

	    // Kiểm tra xem Project mới có tồn tại trong danh sách hay không
	    boolean projectExists = false;
	    for (BackupProject existingProject : backupProjects) {
	        if (existingProject.getProjectId() == projectId) {
	            // Nếu Project đã tồn tại, cập nhật thông tin
	            existingProject.setProjectName(projectName);
	            existingProject.setDescription(description);
	            existingProject.setHostname(hostname);
	            existingProject.setUsername(username);
	            existingProject.setPassword(password);
	            existingProject.setBackupProjectStatusFromEnum(projectStatus);
	            projectExists = true;
	            break;
	        }
	    }

	    // Nếu Project không tồn tại, tạo Project mới và thêm vào danh sách
	    if (!projectExists) {
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
	    }

	    // Ghi danh sách đã cập nhật lại vào file 	/home/ubuntu/sftp_ver2/config.json
	    try (FileWriter writer = new FileWriter("config.json")) {
	        // Tạo JsonArray chứa thông tin các BackupProject
	        JsonArray backupProjectsArray = new JsonArray();
	        for (BackupProject project : backupProjects) {
	            // Tạo JsonObject chứa thông tin của một BackupProject
	            JsonObject projectJson = new JsonObject();
	            projectJson.addProperty("projectId", project.getProjectId());
	            projectJson.addProperty("projectName", project.getProjectName());
	            projectJson.addProperty("hostname", project.getHostname());
	            projectJson.addProperty("username", project.getUsername());
	            projectJson.addProperty("password", project.getPassword());
	            projectJson.addProperty("backupProjectStatus", project.getBackupProjectStatus());

	            // Tạo JsonArray chứa thông tin các BackupTask của Project
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

	                // Tạo JsonArray chứa thông tin các BackupFolder của Task
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
	        System.out.println("Save ok");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
