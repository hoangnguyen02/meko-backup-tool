package vn.mekosoft.backup.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import vn.mekosoft.backup.action.AlertMaker;
import vn.mekosoft.backup.config.ConfigReader;
import vn.mekosoft.backup.impl.BackupTaskServiceImpl;
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
	private Button button_log, button_delete;
	@FXML
	private Button button_run;
	@FXML
	private VBox vbox_taskList;
	@FXML
	private Button button_stop;
	@FXML
	private Circle cricle_task_status;

	private BackupProject currentProject;
	private BackupTask currentTask;
	private Dashboard dashboardController;
	private BackupTaskServiceImpl backupTaskService;
	private boolean isSchedulerRunning = false;
	private BackupProject project;
    @FXML
    private MenuItem menuItem_delete;
    @FXML
    private MenuButton menu_action;
    
    
	public void clearPane() {
		infor_task.getChildren().clear();
	}

	public void setDashboardController(Dashboard dashboardController) {
		this.dashboardController = dashboardController;
	}

	public void setProject(BackupProject project) {
		this.project = project;
	}



	public void refreshView() {
		vbox_taskList.getChildren().clear();
		if (currentTask != null && currentProject != null) {
			taskData(currentTask, currentProject);
			dashboardController.refresh_action();
		}
		if (dashboardController != null) {
			dashboardController.refresh_action();
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		backupTaskService = new BackupTaskServiceImpl();
		 if (menu_action != null) {
		        checkMenuItemState();
		    } else {
		        System.out.println("Menu action is null.");
		    }
	}

	public void details_action(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/detailsTask.fxml"));
		Parent root = loader.load();
		DetailsTask details = loader.getController();
		details.setDashboardController(dashboardController);
		details.setProject(currentProject);
		details.taskDetails(currentTask);
		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.show();
		stage.setTitle("Edit Task");
		stage.getIcons().add(new Image("/vn/mekosoft/backup/view/img/company_logo.png"));
		if (currentTask != null) {
			currentTask.setBackupTaskStatus(BackupTaskStatus.DANG_BIEN_SOAN.getId());
			backupTaskService.updateBackupTask(currentProject.getProjectId(), currentTask.getBackupTaskId(),
					currentTask);

			task_status.setText(BackupTaskStatus.DANG_BIEN_SOAN.getDescription());
			cricle_task_status.setFill(Color.web(BackupTaskStatus.DANG_BIEN_SOAN.getColorTask()));
		}
		button_details.setText("Save");
	}

	public void log_action(ActionEvent event) throws IOException {
		ConfigReader config = new ConfigReader();
		String logFilePath = config.getConfigLog(currentProject.getProjectId(), currentTask.getBackupTaskId());
		if (logFilePath != null && !logFilePath.isEmpty()) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/showlog.fxml"));
			Parent root = loader.load();
			Log logController = loader.getController();
			logController.setTask(currentProject, currentTask);
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Logs");
			stage.getIcons().add(new Image("/vn/mekosoft/backup/view/img/company_logo.png"));
			stage.show();
		} else {
			AlertMaker.errorAlert("Error", "Log file does not exist!");
		}
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
		if (status == BackupTaskStatus.DA_DAT_LICH) {
			button_details.setVisible(false);
			button_run.setVisible(false);
		} else {
			button_details.setVisible(true);
			button_run.setVisible(true);

		}
	}

	public void setBackupTaskService(BackupTaskServiceImpl backupTaskService) {
		this.backupTaskService = backupTaskService;
	}

	private String getStatusStop() {
		return BackupTaskStatus.KHONG_HOAT_DONG.getDescription();
	}

	private String getStatusActive() {
		return BackupTaskStatus.DA_DAT_LICH.getDescription();
	}

	private void saveTaskStatus(BackupTaskStatus status) {
		if (currentTask != null && currentProject != null) {
			currentTask.setBackupTaskStatus(status.getId());
			backupTaskService.updateBackupTask(currentProject.getProjectId(), currentTask.getBackupTaskId(),
					currentTask);
		} else {
		}
	}

//	public void scheduler_action(ActionEvent event) {
//		if (isSchedulerRunning) {
//			stop_action();
//
//			button_run.setVisible(true);
//			button_stop.setVisible(false);
//			task_status.setText(getStatusStop());
//			cricle_task_status.setFill(Color.web(BackupTaskStatus.KHONG_HOAT_DONG.getColorTask()));
//			saveTaskStatus(BackupTaskStatus.KHONG_HOAT_DONG);
//			button_details.setVisible(true);
//			refreshView();
//
//			System.out.println("stop: " + task_status.getText());
//		} else {
//			run_action();
//
//			button_run.setVisible(false);
//			button_stop.setVisible(true);
//			task_status.setText(getStatusActive());
//			cricle_task_status.setFill(Color.web(BackupTaskStatus.DA_DAT_LICH.getColorTask()));
//			saveTaskStatus(BackupTaskStatus.DA_DAT_LICH);
//			button_details.setVisible(false);
//
//			System.out.println("run: " + task_status.getText());
//		}
//		isSchedulerRunning = !isSchedulerRunning;
//	}

	public void delete_action() {
		if (currentTask != null && currentProject != null) {
			Optional<ButtonType> result = AlertMaker.showConfirmAlert("Confirmation",
					"Are you sure you want to delete this task?");
			result.ifPresent(buttonType -> {
				if (buttonType == ButtonType.OK) {
					backupTaskService.deleteBackupTask(currentProject.getProjectId(), currentTask.getBackupTaskId());
					refreshView();
				}
			});
		} else {
			refreshView();
		}
	}

	public void scheduler_action(ActionEvent event) {
		if (event.getSource() == button_run) {
			run_action();
			updateUIForRunningTask();
		
		} else if (event.getSource() == button_stop) {
			stop_action(this::updateUIForStoppedTask);
		}
	}
	private void checkMenuItemState() {
    }
	private void updateUIForRunningTask() {
		button_run.setVisible(false);
		button_stop.setVisible(true);
		button_delete.setVisible(false);
		menuItem_delete.setDisable(true);;
		task_status.setText(getStatusActive());
		cricle_task_status.setFill(Color.web(BackupTaskStatus.DA_DAT_LICH.getColorTask()));
		saveTaskStatus(BackupTaskStatus.DA_DAT_LICH);
		button_details.setVisible(false);
		  menuItem_delete.setVisible(menuItem_delete.isVisible() && !menuItem_delete.isDisable());
	}

	private void updateUIForStoppedTask() {
		button_run.setVisible(true);
		button_stop.setVisible(false);
		task_status.setText(getStatusStop());
		cricle_task_status.setFill(Color.web(BackupTaskStatus.KHONG_HOAT_DONG.getColorTask()));
		saveTaskStatus(BackupTaskStatus.KHONG_HOAT_DONG);
		button_details.setVisible(true);
	}

	public void run_action() {
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
			} else {
				AlertMaker.errorAlert("Error", "Failed to start task. Please check the logs for more details.");
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			AlertMaker.errorAlert("Error", "Failed to execute start commands. Please check the logs for more details.");
		}
	}

	public void stop_action(ActionEvent event) {
		stop_action(this::updateUIForStoppedTask);
	}

	public void stop_action(Runnable callback) {
		Optional<ButtonType> result = AlertMaker.showConfirmAlert("Confirmation",
				"Are you sure you want to stop the task?");
		result.ifPresent(response -> {
			if (response == ButtonType.OK) {
				try {
					String projectId_stop = String.valueOf(currentProject.getProjectId());
					String taskId_stop = String.valueOf(currentTask.getBackupTaskId());

					String stopLocal = "/home/ubuntu/sftp_ver2/backup.sh --local --project_id=" + projectId_stop
							+ " --task_id=" + taskId_stop + " --remove_time";
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
							if (callback != null) {
								callback.run();
							}
						} else {
							AlertMaker.errorAlert("Error",
									"Failed to stop task. Please check the logs for more details.");
						}
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
						AlertMaker.errorAlert("Error",
								"Failed to execute stop commands. Please check the logs for more details.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					AlertMaker.errorAlert("Error", "An unexpected error occurred!");
				}
			}
		});
	}

	public void updateData() {
		// TODO Auto-generated method stub
		
	}
}
