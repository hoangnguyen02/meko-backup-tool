package vn.mekosoft.backup.controller;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.Node;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;

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

	private BackupProject project;
	private BackupTask task;
    private Dashboard dashboardController;

    public void setDashboardController(Dashboard dashboardController) {
        this.dashboardController = dashboardController;
    }
	public void setProject(BackupProject project) {
		this.project = project;

	}

	private void refreshView() {
	    vbox_folder.getChildren().clear();
	    taskDetails(task);
	}

	public void saveFolder_action() {
		long backupFolderId;
		try {
			backupFolderId = Long.parseLong(folder_id.getText());
		} catch (NumberFormatException e) {
			return;
		}
		String folderPath = folder_path.getText();

		if (task != null && task.getBackupFolders() != null) {
			boolean folderFound = false;
			for (BackupFolder folder : task.getBackupFolders()) {
				if (folder.getBackupFolderId() == backupFolderId) {
					folder.setFolderPath(folderPath);
					folderFound = true;
					break;
				}
			}

			if (!folderFound) {
				BackupFolder newFolder = new BackupFolder(backupFolderId, folderPath, task.getBackupTaskId());
				task.getBackupFolders().add(newFolder);
				addFolderLayout(newFolder);
			}

			saveTaskFolders(task);

			folder_id.clear();
			folder_path.clear();
		}
		refreshView();
	}

	private void saveTaskFolders(BackupTask task) {
		BackupServiceImpl backupService = new BackupServiceImpl();
		List<BackupProject> backupProjects = backupService.loadData();

		for (BackupProject proj : backupProjects) {
			if (proj.getProjectId() == project.getProjectId()) {
				for (BackupTask t : proj.getBackupTasks()) {
					if (t.getBackupTaskId() == task.getBackupTaskId()) {
						t.setBackupFolders(task.getBackupFolders());
						break;
					}
				}
				break;
			}
		}
		backupService.saveData(backupProjects);
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
			if (task.getBackupFolders() != null && !task.getBackupFolders().isEmpty()) {
				for (BackupFolder folder : task.getBackupFolders()) {
					addFolderLayout(folder);
				}
			}
		}

		if (project != null) {
			project_name_BP.setText(project.getProjectName());
		}
	}

	public void saveInforTask_action(ActionEvent event) throws IOException {
		if (project != null && task != null) {
			task.setBackupTaskId(Long.parseLong(task_id.getText()));
			task.setProjectId(project.getProjectId());
			task.setName(task_name.getText());
			task.setLocalSchedular(local_cronTab.getText());
			task.setRemoteSchedular(remote_cronTab.getText());
			task.setLocalPath(local_path.getText());
			task.setRemotePath(remote_path.getText());
			task.setLocalRetention(Long.parseLong(local_retention.getText()));
			task.setRemoteRetention(Long.parseLong(remote_retention.getText()));
			task.setBackupTaskStatus(BackupTaskStatus.DANG_BIEN_SOAN.getId());

			BackupServiceImpl backupService = new BackupServiceImpl();
			List<BackupProject> backupProjects = backupService.loadData();

			boolean projectFound = false;
			for (BackupProject proj : backupProjects) {
				if (proj.getProjectId() == project.getProjectId()) {
					List<BackupTask> projectTasks = proj.getBackupTasks();
					if (projectTasks == null) {
						projectTasks = new ArrayList<>();
						proj.setBackupTasks(projectTasks);
					}

					boolean taskFound = false;
					for (BackupTask existingTask : projectTasks) {
						if (existingTask.getBackupTaskId() == task.getBackupTaskId()) {
							existingTask.setName(task.getName());
							existingTask.setLocalSchedular(task.getLocalSchedular());
							existingTask.setRemoteSchedular(task.getRemoteSchedular());
							existingTask.setLocalPath(task.getLocalPath());
							existingTask.setRemotePath(task.getRemotePath());
							existingTask.setLocalRetention(task.getLocalRetention());
							existingTask.setRemoteRetention(task.getRemoteRetention());
							existingTask.setBackupTaskStatus(task.getBackupTaskStatus());
							existingTask.setBackupFolders(task.getBackupFolders());
							taskFound = true;
							break;
						}
					}

					if (!taskFound) {
						projectTasks.add(task);
					}
					projectFound = true;
					break;
				}
			}

			if (!projectFound) {
				project.setBackupTasks(new ArrayList<>(List.of(task)));
				backupProjects.add(project);
			}
			backupService.saveData(backupProjects);


			refreshView();
		
			((Node) (event.getSource())).getScene().getWindow().hide();
		}
		   
	}

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		inforClear();

	}

	public void addFolderLayout(BackupFolder folder) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/folder.fxml"));
			content_folder = loader.load();
			Folder folderController = loader.getController();
			System.out.print("load");
			folderController.folderData(folder);

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
