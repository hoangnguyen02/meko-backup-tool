package vn.mekosoft.backup.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import vn.mekosoft.backup.config.Config;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.service.BackupService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Log implements Initializable {

    @FXML
    private TextArea content_log;

    @FXML
    private Label folderPath_log;

    @FXML
    private AnchorPane log_view;

    @FXML
    private Label projectName_log;

    @FXML
    private Label taskName_log;

    private Config configLogFile;
    private BackupService backupService;
    private static final Logger LOGGER = Logger.getLogger(Log.class.getName());

    private BackupProject currentProject;
    private BackupTask currentTask;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        backupService = new BackupServiceImpl();
        configLogFile = new Config();
    }

    public void setProject(BackupProject project) {
        this.currentProject = project;
        projectName_log.setText(project.getProjectName());
    }

    public void setTask(BackupTask task) {
        this.currentTask = task;
        taskName_log.setText(task.getName());
        String folderPath = task.getLocalPath();
        long projectId = currentProject.getProjectId();
        long taskId = task.getBackupTaskId();
        readLogV2(folderPath, projectId, taskId);
    }

    public void readLogV2(String folderPath, long projectId, long taskId) {
        String logFilePath = configLogFile.getConfigLog(projectId, taskId);
        System.out.print(logFilePath);
        if (logFilePath != null && !logFilePath.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                content_log.setText(content.toString());
            } catch (IOException e) {
                LOGGER.severe("Error : " + e.getMessage());
                content_log.setText("Error: " + e.getMessage());
            }
        } else {
            content_log.setText("Error: empty.");
        }
    }
}
