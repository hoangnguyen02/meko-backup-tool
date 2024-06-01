package vn.mekosoft.backup.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import vn.mekosoft.backup.action.AlertMaker;
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
	@FXML
	private Button button_run;
	@FXML
	private VBox vbox_taskList;
	@FXML
	private Button button_stop;

	private BackupProject currentProject;
	private BackupTask currentTask;
	private Dashboard dashboard;

	@FXML
	private Circle cricle_task_status;

	public void run_action(ActionEvent event) {
	    String projectId = String.valueOf(currentProject.getProjectId());
	    String taskId = String.valueOf(currentTask.getBackupTaskId());

	    String localCommand = "/home/ubuntu/sftp_ver2/backup.sh --local --project_id=" + projectId + " --task_id="
	            + taskId + " --set_time";
	    System.out.println(localCommand);
	    String remoteCommand = "/home/ubuntu/sftp_ver2/backup.sh --remote --project_id=" + projectId + " --task_id="
	            + taskId + " --set_time";
	    System.out.println(remoteCommand);

	    try {
	        ProcessBuilder localProcessBuilder = new ProcessBuilder("bash", "-c", localCommand);
	        Process localProcess = localProcessBuilder.start();
	        int localExitCode = localProcess.waitFor();

	        ProcessBuilder remoteProcessBuilder = new ProcessBuilder("bash", "-c", remoteCommand);
	        Process remoteProcess = remoteProcessBuilder.start();
	        int remoteExitCode = remoteProcess.waitFor();

	        if (localExitCode == 0 && remoteExitCode == 0) {
	          
	            AlertMaker.successfulAlert("Successful", "Task started successfully");
	        } else {
	            AlertMaker.errorAlert("Error", "Failed to start task. Please check the logs for more details.");
	        }
	    } catch (IOException | InterruptedException e) {
	        e.printStackTrace();
	        AlertMaker.errorAlert("Error", "Failed to execute start commands. Please check the logs for more details.");
	    }
	}



	public void stop_action(ActionEvent event) {
		Optional<ButtonType> result = AlertMaker.showConfirmAlert("Confirmation", "Are you sure you want to stop the task?");
	    
	    result.ifPresent(response -> {
	        if (response == ButtonType.OK) {
	            try {
	                String projectId_stop = String.valueOf(currentProject.getProjectId());
	                String taskId_stop = String.valueOf(currentTask.getBackupTaskId());

	                String stopLocal = "/home/ubuntu/sftp_ver2/backup.sh --local --project_id=" + projectId_stop + " --task_id="
	                        + taskId_stop + " --remove_time";
	                System.out.println(stopLocal);

	                String stopRemote = "/home/ubuntu/sftp_ver2/backup.sh --remote --project_id=" + projectId_stop
	                        + " --task_id=" + taskId_stop + " --remove_time";
	                System.out.println(stopRemote);

	                try {
	                    ProcessBuilder localProcessBuilder = new ProcessBuilder("bash", "-c", stopLocal);
	                    Process localProcess = localProcessBuilder.start();
	                    int localExitCode = localProcess.waitFor();

	                    ProcessBuilder remoteProcessBuilder = new ProcessBuilder("bash", "-c", stopRemote);
	                    Process remoteProcess = remoteProcessBuilder.start();
	                    int remoteExitCode = remoteProcess.waitFor();

	                    if (localExitCode == 0 && remoteExitCode == 0) {
	                        AlertMaker.successfulAlert("Successful", "Task stopped successfully");
	                    } else {
	                        AlertMaker.errorAlert("Error", "Failed to stop task. Please check the logs for more details.");
	                    }
	                } catch (IOException | InterruptedException e) {
	                    e.printStackTrace();
	                    AlertMaker.errorAlert("Error", "Failed to execute stop commands. Please check the logs for more details.");
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	                AlertMaker.errorAlert("Error", "An unexpected error occurred!");
	            }
	        }
	    });
	}


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
		//details.onlyView();

		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.show();
	}

	public void log_action(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/showlog.fxml"));
		Parent root = loader.load();

		Log logController = loader.getController();
		// logController.setProject(currentProject);
		logController.setTask(currentProject, currentTask);

		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.show();
	}

	public void taskData(BackupTask task, BackupProject project) {
		this.currentTask = task;
		this.currentProject = project;
		task_name.setText("Task: " + " " + task.getName());

		BackupTaskStatus status = task.getBackupTaskStatusEnum();
		if (status == null) {
			status = BackupTaskStatus.DANG_BIEN_SOAN;
		}
		task_status.setText(status.getDescription());
		String colorCode = status.getColorTask();
		Color color = Color.web(colorCode);

		cricle_task_status.setFill(color);

	}
}
