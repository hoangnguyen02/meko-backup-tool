package vn.mekosoft.backup.controller;

import java.io.IOException;
import java.net.URL;
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
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;

public class ManagementTask implements Initializable {

    @FXML
    private Button button_details;

    @FXML
    private Button button_status;

    @FXML
    private AnchorPane infor_task;

    @FXML
    private Label task_name;
    @FXML
    private Label task_status;

    @FXML
    private Button button_log;

    private BackupProject currentProject;
    private BackupTask currentTask;
    private Dashboard dashboard;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public void details_action(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/detailsTask.fxml"));
        Parent root = loader.load();

        DetailsTask details = loader.getController();
        details.setProject(currentProject);
        details.taskDetails(currentTask);
        details.onlyView();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void log_action(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/showlog.fxml"));
        Parent root = loader.load();

        Log logController = loader.getController();
        logController.setProject(currentProject);
        logController.setTask(currentTask);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void taskData(BackupTask task, BackupProject project) {
        this.currentTask = task;
        this.currentProject = project;
        task_name.setText(task.getName());

        BackupTaskStatus status = task.getBackupTaskStatusEnum();
        if (status == null) {
            status = BackupTaskStatus.DANG_BIEN_SOAN;
        }
        task_status.setText(status.getDescription());
    }
}
