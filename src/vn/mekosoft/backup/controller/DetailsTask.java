package vn.mekosoft.backup.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;

public class DetailsTask implements Initializable {

    @FXML
    private AnchorPane addTask_view;

    @FXML
    private TextField folder_path;

    @FXML
    private TextField folder_path1;

    @FXML
    private TextField folder_path2;

    @FXML
    private TextField local_cronTab;

    @FXML
    private TextField local_path;

    @FXML
    private TextField local_retention;

    @FXML
    private Label project_name_BP;

    @FXML
    private TextField remote_cronTab;

    @FXML
    private TextField remote_path;

    @FXML
    private TextField remote_retention;

    @FXML
    private TextField task_name;

    @FXML
    private Button button_save_folderPath;

    @FXML
    private Button button_save_inforTask;

    private BackupTask task;
    private BackupProject project;

    public void setProject(BackupProject project) {
        this.project = project;
        System.out.println("đẹp: " + project);
    }

    public void inforClear() {
        task_name.clear();
        local_path.clear();
        local_cronTab.clear();
        local_retention.clear();
        remote_path.clear();
        remote_cronTab.clear();
        remote_retention.clear();
        folder_path.setText("");
        folder_path1.setText("");
        folder_path2.setText("");
    }

    public void taskDetails(BackupTask task, BackupProject project) {
        this.task = task;
        this.project = project;
        if (task.getName() != null) {
            task_name.setText(task.getName());
            local_cronTab.setText(task.getLocalSchedular());
            remote_cronTab.setText(task.getRemoteSchedular());
            local_path.setText(task.getLocalPath());
            remote_path.setText(task.getRemotePath());
            local_retention.setText(String.valueOf(task.getLocalRetention()));
            remote_retention.setText(String.valueOf(task.getRemoteRetention()));

            List<BackupFolder> folders = task.getBackupFolders();
            if (folders.size() > 0) {
                folder_path.setText(folders.get(0).getFolderPath());
            }
            if (folders.size() > 1) {
                folder_path1.setText(folders.get(1).getFolderPath());
            }
            if (folders.size() > 2) {
                folder_path2.setText(folders.get(2).getFolderPath());
            }
        }
    }

    public void saveInforTask_action(ActionEvent event) throws IOException {
        System.out.println("lấy rồi: " + project);
        if (project != null) {
            BackupServiceImpl backupService = new BackupServiceImpl();
            List<BackupProject> backupProjects = backupService.loadData();
            boolean projectExists = false;

            for (BackupProject proj : backupProjects) {
                if (proj.getProjectId() == project.getProjectId()) {
                    projectExists = true;
                    proj.getBackupTasks().add(createBackupTask());
                    break;
                }
            }

            if (!projectExists) {
                project.getBackupTasks().add(createBackupTask());
                backupProjects.add(project);
            }

            saveBackupProjects(backupProjects);
        } else {
            System.out.println("null.");
        }
    }

    private BackupTask createBackupTask() {
        BackupTask newTask = new BackupTask();
        newTask.setName(task_name.getText());
        newTask.setLocalSchedular(local_cronTab.getText());
        newTask.setRemoteSchedular(remote_cronTab.getText());
        newTask.setLocalPath(local_path.getText());
        newTask.setRemotePath(remote_path.getText());
        newTask.setLocalRetention(Integer.parseInt(local_retention.getText()));
        newTask.setRemoteRetention(Integer.parseInt(remote_retention.getText()));

        List<BackupFolder> folders = new ArrayList<>();
        if (!folder_path.getText().isEmpty()) {
            folders.add(new BackupFolder(1, folder_path.getText(), newTask.getBackupTaskId()));
        }
        if (!folder_path1.getText().isEmpty()) {
            folders.add(new BackupFolder(2, folder_path1.getText(), newTask.getBackupTaskId()));
        }
        if (!folder_path2.getText().isEmpty()) {
            folders.add(new BackupFolder(3, folder_path2.getText(), newTask.getBackupTaskId()));
        }
        newTask.setBackupFolders(folders);

        return newTask;
    }

    private void saveBackupProjects(List<BackupProject> backupProjects) {
        JsonObject jsonObject = new JsonObject();
        JsonArray backupProjectsArray = new JsonArray();

        for (BackupProject proj : backupProjects) {
            JsonObject projectJson = new JsonObject();
            projectJson.addProperty("projectId", proj.getProjectId());
            projectJson.addProperty("hostname", proj.getHostname());
            projectJson.addProperty("username", proj.getUsername());
            projectJson.addProperty("password", proj.getPassword());

            JsonArray backupTasksArray = new JsonArray();

            for (BackupTask tsk : proj.getBackupTasks()) {
                JsonObject taskJson = new JsonObject();
                taskJson.addProperty("backupTaskId", tsk.getBackupTaskId());
                taskJson.addProperty("name", tsk.getName());
                taskJson.addProperty("localSchedular", tsk.getLocalSchedular());
                taskJson.addProperty("remoteSchedular", tsk.getRemoteSchedular());
                taskJson.addProperty("localPath", tsk.getLocalPath());
                taskJson.addProperty("remotePath", tsk.getRemotePath());
                taskJson.addProperty("localRetention", tsk.getLocalRetention());
                taskJson.addProperty("remoteRetention", tsk.getRemoteRetention());

                JsonArray backupFoldersArray = new JsonArray();
                for (BackupFolder folder : tsk.getBackupFolders()) {
                    JsonObject folderJson = new JsonObject();
                    folderJson.addProperty("folderId", folder.getBackupFolderId());
                    folderJson.addProperty("folderPath", folder.getFolderPath());
                    backupFoldersArray.add(folderJson);
                }
                taskJson.add("backupfolders", backupFoldersArray);
                backupTasksArray.add(taskJson);
            }

            projectJson.add("backupTasks", backupTasksArray);
            backupProjectsArray.add(projectJson);
        }

        jsonObject.add("backupProjects", backupProjectsArray);

        try (FileWriter writer = new FileWriter("new_project.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(jsonObject, writer);
            System.out.println("Ngon.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        inforClear();
    }
}
