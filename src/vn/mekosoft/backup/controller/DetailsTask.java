package vn.mekosoft.backup.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

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
		long backupFolderId = Long.parseLong(folder_id.getText());
		String folderPath = folder_path.getText();

		if (task != null) {
			BackupFolder newFolder = new BackupFolder(backupFolderId, folderPath, task.getBackupTaskId());
			task.getBackupFolders().add(newFolder);
			addFolderLayout(newFolder);

			saveTaskFolders();

			folder_id.clear();
			folder_path.clear();
		}
		refreshView();
	}

	private void saveTaskFolders() {
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
			task.setBackupFolders(new ArrayList<>());

			// Lấy dữ liệu từ service
			BackupServiceImpl backupService = new BackupServiceImpl();
			List<BackupProject> backupProjects = backupService.loadData();

			// Tìm dự án hiện tại và thêm nhiệm vụ mới vào danh sách nhiệm vụ của dự án
			for (BackupProject proj : backupProjects) {
				if (proj.getProjectId() == project.getProjectId()) {
					List<BackupTask> projectTasks = proj.getBackupTasks();
					if (projectTasks == null) {
						projectTasks = new ArrayList<>();
						proj.setBackupTasks(projectTasks);
					}
					// Thêm nhiệm vụ mới vào danh sách nhiệm vụ của dự án
					projectTasks.add(task);
					proj.setBackupTasks(projectTasks); // Cập nhật danh sách nhiệm vụ cho dự án
					break; // Thoát khỏi vòng lặp khi tìm thấy dự án
				}
			}

			// Lưu lại dữ liệu đã cập nhật
			backupService.saveData(backupProjects);

			// Làm mới giao diện
			refreshView();

			// Đóng cửa sổ hiện tại
			((Node) (event.getSource())).getScene().getWindow().hide();
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resources) {

		inforClear();

	}

	public void onlyView() {
		task_id.setEditable(false);
		task_name.setEditable(false);
		local_cronTab.setEditable(false);
		remote_cronTab.setEditable(false);
		local_path.setEditable(false);
		remote_path.setEditable(false);
		local_retention.setEditable(false);
		remote_retention.setEditable(false);

		// Hide or disable save button
		button_save_inforTask.setVisible(false);
		folder_path.setEditable(false);
		folder_id.setEditable(false);
		button_saveFolder.setVisible(false);

	}

	public void addFolderLayout(BackupFolder folder) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/folder.fxml"));
			content_folder = loader.load();
			Folder folderController = loader.getController();
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
