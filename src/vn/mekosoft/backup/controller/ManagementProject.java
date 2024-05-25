package vn.mekosoft.backup.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;
import vn.mekosoft.backup.model.BackupTask;

public class ManagementProject implements Initializable {
    @FXML
    private Button button_addTask;

    @FXML
    private AnchorPane infor_project;

    @FXML
    private Label project_activity;

    @FXML
    private Label project_hostname;

    @FXML
    private Label project_name_main;

    @FXML
    private Label project_username;

    private BackupProject project;

    
    public void addTask_action(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/detailsTask.fxml"));
        Parent root = loader.load();

        DetailsTask controller = loader.getController();

        BackupTask newTask = new BackupTask();
        controller.setProject(project);
        controller.taskDetails(newTask);
        controller.inforClear();
        
        // Add the newly created task to the project
        project.getBackupTasks().add(newTask);
        Dashboard dashboard = new Dashboard();
      //  dashboard.addTask_Layout(newTask, project);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resources) {

    }

    public void projectData(BackupProject project) {
        this.project = project;
        project_name_main.setText(project.getProjectName());
        project_hostname.setText(project.getHostname());
        project_username.setText(project.getUsername());
        project_activity.setText(BackupProjectStatus.fromId(project.getBackupProjectStatus()).getDescriptionStatusProject());
    }
}
