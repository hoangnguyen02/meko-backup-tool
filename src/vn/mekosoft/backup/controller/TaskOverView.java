package vn.mekosoft.backup.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import vn.mekosoft.backup.impl.BackupProjectServiceImpl;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;
import vn.mekosoft.backup.service.BackupProjectService;

public class TaskOverView implements Initializable {
    @FXML
    private ImageView image_running;

    @FXML
    private ImageView image_stop;

    @FXML
    private ImageView image_total;
    @FXML
    private Label numberOfRunning;

    @FXML
    private Label numberOfStop;

    @FXML
    private Label numberOfTotal;
	private Dashboard dashboardController;
	public void setDashboardController(Dashboard dashboardController) {
		this.dashboardController = dashboardController;
	}
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		loadData();
	}
	public void loadData() {

		BackupProjectService backupService = new BackupProjectServiceImpl();
		List<BackupProject> projects = backupService.loadProjectData();

		int totalTasks = 0;
		int runningTasks = 0;
		int stoppedTasks = 0;
		for (BackupProject project : projects) {
			List<BackupTask> tasks = project.getBackupTasks();
			totalTasks += tasks.size();

			for (BackupTask task : tasks) {
				switch (BackupTaskStatus.fromId(task.getBackupTaskStatus())) {
				case KHONG_HOAT_DONG:
				case DANG_BIEN_SOAN:
					stoppedTasks++;
					break;
				case DA_DAT_LICH:
					runningTasks++;
					break;
				default:
					break;
				}
			}
		}

		updateLabels(totalTasks, runningTasks, stoppedTasks);
		//loadDataTable();
	}

	private void updateLabels(int totalTasks, int runningTasks, int stoppedTasks) {
		Platform.runLater(() -> {
			numberOfTotal.setText(String.valueOf(totalTasks));
			numberOfStop.setText(String.valueOf(stoppedTasks));
			numberOfRunning.setText(String.valueOf(runningTasks));
		});

	}
	public void updateData() {
        loadData();
    }
}
