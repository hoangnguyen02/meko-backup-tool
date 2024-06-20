package vn.mekosoft.backup.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import vn.mekosoft.backup.action.AlertMaker;
import vn.mekosoft.backup.config.Config;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Log implements Initializable {

	@FXML
	private TextArea content_log;

	@FXML
	private Label folderPath_log;

	@FXML
	private AnchorPane log_view;

	@FXML
	private Label infor_log;

	private volatile boolean running = true;
	private Config configLogFile;
	private BackupProject currentProject;
	private BackupTask currentTask;

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		new BackupServiceImpl();
		configLogFile = new Config();

		startUpdateThread();
		 content_log.addEventFilter(ScrollEvent.SCROLL, event -> stopUpdating());
		 content_log.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
	        if (event.getClickCount() == 1	) {
	            startUpdateThread();
	        }
	    });	}

	private void startUpdateThread() {
		running = true;
		Thread updateThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					updateLogData();
					try {
						Thread.sleep(1000); // Dừng 1 giây
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		updateThread.setDaemon(true);
		updateThread.start();
	}

	private void updateLogData() {
		if (currentTask != null && currentProject != null) {
			readLogV2(currentTask.getLocalPath(), currentProject.getProjectId(), currentTask.getBackupTaskId());
		} else {

		}
	}

	public void setTask(BackupProject project, BackupTask task) {
		this.currentTask = task;
		this.currentProject = project;
		infor_log.setText(project.getProjectName() + " / " + task.getName());
		String folderPath = task.getLocalPath();
		long projectId = currentProject.getProjectId();
		long taskId = task.getBackupTaskId();
		folderPath_log.setText(configLogFile.getConfigLog(projectId, taskId));
		readLogV2(folderPath, projectId, taskId);
	}

	public void readLogV2(String folderPath, long projectId, long taskId) {
		try {
			String logFilePath = configLogFile.getConfigLog(projectId, taskId);
			if (logFilePath != null && !logFilePath.isEmpty()) {
				try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
					StringBuilder content = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						content.append(line).append("\n");
					}
					Platform.runLater(() -> {
						content_log.setText(content.toString());
						content_log.positionCaret(content_log.getLength());
						content_log.setScrollTop(Double.MAX_VALUE);
					});
				} catch (IOException e) {
					e.printStackTrace();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void stopUpdating() {
		running = false;
	}
}