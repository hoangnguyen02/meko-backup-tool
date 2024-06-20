package vn.mekosoft.backup.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import vn.mekosoft.backup.action.AlertMaker;
import vn.mekosoft.backup.impl.BackupFolderServiceImpl;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.impl.BackupTaskServiceImpl;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;
import vn.mekosoft.backup.service.BackupFolderService;

public class DetailsTask implements Initializable {
	@FXML
	private AnchorPane addTask_view;

	@FXML
	private Button button_saveFolder;

	@FXML
	private Button button_save_inforTask;

	@FXML
	private AnchorPane folderPath_view;
	@FXML
	private AnchorPane folder_pane;

	@FXML
	private TextField folder_id;

	@FXML
	private TextField folder_path;

	@FXML
	private AnchorPane listFolder;

	@FXML
	private VBox vbox_folder;

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
	private TextField task_id;

	@FXML
	private TextField task_name;

	@FXML
	private TextField config_textField;

	@FXML
	private Button save_config;

	@FXML
	private TextField get_FolderId;

	@FXML
	private TextField get_FolderPath;

	@FXML
	private AnchorPane content_folder;

	@FXML
	private Button button_delete;

	private BackupProject project;
	private BackupTask task;
	private BackupFolder folder;
	private static long currentMaxTaskId = 0;
	private static long currentMaxFolderId = 0;
	private Dashboard dashboardController;
	private BackupFolderService folderService = new BackupFolderServiceImpl();
	private Folder folderController;

	public void setDashboardController(Dashboard dashboardController) {
		this.dashboardController = dashboardController;
		this.folderService = new BackupFolderServiceImpl();
	}

	public void setProject(BackupProject project) {
		this.project = project;
	}

	public void setFolderController(Folder folderController) {
		this.folderController = folderController;
	}

	public void refreshView() {
		vbox_folder.getChildren().clear();
		taskDetails(task);
		if (dashboardController != null) {
			dashboardController.loadData();
			dashboardController.refresh_action();

		}
	}

	public void loadFolderDataForTask() {
		if (project != null && task != null) {
			List<BackupFolder> folders = folderService.loadFolderData(project.getProjectId(), task.getBackupTaskId());
			vbox_folder.getChildren().clear();

			for (BackupFolder folder : folders) {
				addFolderLayout(folder);
			}
		}
	}

	public void refreshFolder() {
	    List<BackupFolder> newFolders = folderService.loadFolderData(project.getProjectId(), task.getBackupTaskId());
	    
	    newFolders.removeIf(f -> task.getBackupFolders().stream()
	        .noneMatch(existingFolder -> existingFolder.getBackupFolderId() == f.getBackupFolderId()));
	    
	    task.setBackupFolders(newFolders);
	    vbox_folder.getChildren().clear();
	    for (BackupFolder folder : newFolders) {
	        addFolderLayout(folder);
	    }
	}


	public long getProjectId() {
		return project.getProjectId();
	}

	public long getTaskId() {
		return task.getBackupTaskId();
	}

	public void saveFolder_action() {
		try {
			if (task != null) {
				long maxBackupFolderId = task.getBackupFolders().stream().mapToLong(BackupFolder::getBackupFolderId)
						.max().orElse(0);

				long newBackupFolderId = maxBackupFolderId + 1;
				String folderPath = folder_path.getText();
				BackupFolder newFolder = new BackupFolder(newBackupFolderId, folderPath, task.getBackupTaskId());

				boolean isEditing = false;
				if (folder != null) {
					isEditing = true;
					task.getBackupFolders().removeIf(f -> f.getBackupFolderId() == folder.getBackupFolderId());
				}

				task.getBackupFolders().add(newFolder);

				BackupServiceImpl backupService = new BackupServiceImpl();
				List<BackupProject> backupProjects = backupService.loadData();
				for (BackupProject proj : backupProjects) {
					if (proj.getProjectId() == project.getProjectId()) {
						for (BackupTask t : proj.getBackupTasks()) {
							if (t.getBackupTaskId() == task.getBackupTaskId()) {
								t.setBackupFolders(new ArrayList<>(task.getBackupFolders()));
								break;
							}
						}
						break;
					}
				}

				backupService.saveData(backupProjects);

				refreshView();

				if (!isEditing) {
					addFolderLayout(newFolder);
					folder_id.setText(String.valueOf(newBackupFolderId));
					folder_path.clear();
				}

				refreshView();
			}
		} catch (Exception e) {
			e.printStackTrace();
			AlertMaker.errorAlert("Error", "Failed to save Folder!");
		}
	}

	public void hideFolderPane() {
		folder_pane.setVisible(false);

	}

	public void saveInforTask_action(ActionEvent event) {
		try {
			BackupTaskServiceImpl backupTaskService = new BackupTaskServiceImpl();
			List<BackupProject> backupProjects = backupTaskService.loadProjectData();

			if (task.getBackupTaskId() != 0) {
				updateTaskInformation(backupTaskService, backupProjects);
			} else {
				createNewTask(backupTaskService, backupProjects);
				Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				currentStage.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			AlertMaker.errorAlert("Error", "Failed to save Task!");
		}
		refreshView();
	}

	private void updateTaskInformation(BackupTaskServiceImpl backupTaskService, List<BackupProject> backupProjects) {
		task.setName(task_name.getText());
		task.setLocalSchedular(local_cronTab.getText());
		task.setRemoteSchedular(remote_cronTab.getText());
		task.setLocalPath(local_path.getText());
		task.setRemotePath(remote_path.getText());
		task.setLocalRetention(Long.parseLong(local_retention.getText()));
		task.setRemoteRetention(Long.parseLong(remote_retention.getText()));

		for (BackupProject proj : backupProjects) {
			if (proj.getProjectId() == project.getProjectId()) {
				List<BackupTask> tasks = proj.getBackupTasks();
				for (int i = 0; i < tasks.size(); i++) {
					if (tasks.get(i).getBackupTaskId() == task.getBackupTaskId()) {
						tasks.set(i, task);
						break;
					}
				}
				break;
			}
		}

		backupTaskService.saveProjectsToFile(backupProjects);
	}

	private void createNewTask(BackupTaskServiceImpl backupTaskService, List<BackupProject> backupProjects) {
		long maxTaskId = backupProjects.stream().flatMap(proj -> proj.getBackupTasks().stream())
				.mapToLong(BackupTask::getBackupTaskId).max().orElse(0) + 1;

		task.setBackupTaskId(maxTaskId);
		task.setProjectId(project.getProjectId());
		task.setName(task_name.getText());
		task.setLocalSchedular(local_cronTab.getText());
		task.setRemoteSchedular(remote_cronTab.getText());
		task.setLocalPath(local_path.getText());
		task.setRemotePath(remote_path.getText());
		task.setLocalRetention(Long.parseLong(local_retention.getText()));
		task.setRemoteRetention(Long.parseLong(remote_retention.getText()));
		task.setBackupTaskStatus(BackupTaskStatus.DANG_BIEN_SOAN.getId());
		task.setBackupFolders(new ArrayList<>());
		for (BackupProject proj : backupProjects) {
			if (proj.getProjectId() == project.getProjectId()) {
				List<BackupTask> tasks = proj.getBackupTasks();
				if (tasks == null) {
					tasks = new ArrayList<>();
					proj.setBackupTasks(tasks);
				}
				tasks.add(task);
				break;
			}
		}
		backupTaskService.saveProjectsToFile(backupProjects);
	}

	public void taskDetails(BackupTask task) {
		this.task = task;

		if (task != null) {
			task_id.setText(String.valueOf(task.getBackupTaskId()));
			task_name.setText(task.getName());
			local_cronTab.setText(task.getLocalSchedular());
			remote_cronTab.setText(task.getRemoteSchedular());
			local_path.setText(task.getLocalPath());
			remote_path.setText(task.getRemotePath());
			local_retention.setText(String.valueOf(task.getLocalRetention()));
			remote_retention.setText(String.valueOf(task.getRemoteRetention()));
			refreshFolder();
		}

		if (project != null) {
			project_name_BP.setText("Project: " + " " + project.getProjectName());
		}

	}

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		task_id.setText(String.valueOf(++currentMaxTaskId));
		folder_id.setText(String.valueOf(++currentMaxFolderId));
		inforClear();
	}

	public void addFolderLayout(BackupFolder folder) {
		try {
 
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/folder.fxml"));
			content_folder = loader.load();
			Folder folderController = loader.getController();
			folderController.folderData(folder);
			folderController.setDetailsTaskController(this);
			folderController.setDashboardController(dashboardController);
			vbox_folder.getChildren().add(content_folder);
			content_folder.setMaxHeight(Double.MAX_VALUE);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void inforClear() {
		task_id.setText("");
		task_name.clear();
		local_path.clear();
		local_cronTab.clear();
		local_retention.clear();
		remote_path.clear();
		remote_cronTab.clear();
		remote_retention.clear();
	}

}
