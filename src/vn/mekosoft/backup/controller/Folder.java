package vn.mekosoft.backup.controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.service.BackupFolderService;
import vn.mekosoft.backup.impl.BackupFolderServiceImpl;
import vn.mekosoft.backup.action.AlertMaker;

public class Folder implements Initializable {

	@FXML
	private Button button_delete;
 

	@FXML
	private TextField get_FolderPath;

	@FXML
	private AnchorPane infor_folder;
	private BackupFolder folder;
	@FXML
	private Button button_edit;
	private Dashboard dashboardController;
	private DetailsTask detailsTaskController;

	private BackupFolderService folderService = new BackupFolderServiceImpl();

	public void setDashboardController(Dashboard dashboardController) {
		this.dashboardController = dashboardController;
	}

	public void setDetailsTaskController(DetailsTask detailsTaskController) {
		this.detailsTaskController = detailsTaskController;
	}

	public void folderData(BackupFolder folder) {
		this.folder = folder;
		get_FolderPath.setText(folder.getFolderPath());

	}

	public void delete_action() {
		if (folder == null) {
			AlertMaker.errorAlert("Error", "Folder object is null, unable to delete.");
			return;
		}

		Optional<ButtonType> result = AlertMaker.showConfirmDelete("Confirm",
				"Are you sure you want to delete this folder?");
		if (result.isPresent() && result.get() == ButtonType.OK) {
			try {
				folderService.deleteBackupFolder(detailsTaskController.getProjectId(),
						detailsTaskController.getTaskId(), folder.getBackupFolderId());
				refresh();
			} catch (Exception e) {

				AlertMaker.errorAlert("Error", "Failed to delete Folder!");
			}
		}
	}

	public void refresh() {
		System.out.println("Đang gọi refresh");
		if (detailsTaskController != null) {
			detailsTaskController.refreshFolder();
			System.out.println("call. " + detailsTaskController);

			if (dashboardController != null) {
				dashboardController.refresh_action();
				System.out.println("call. " + dashboardController);

			}
		} else {
		}
	}
 

	public void edit_action() {
		if (folder != null) {
			String newPath = get_FolderPath.getText().trim();
			if (!newPath.isEmpty()) {
				folder.setFolderPath(newPath);

				folderService.updateBackupFolder(detailsTaskController.getProjectId(),
						detailsTaskController.getTaskId(), folder.getBackupFolderId(), folder);

				refresh();
			} else {
				AlertMaker.errorAlert("Error!", "Please enter a new path for the folder.");
			}
		} else {
			AlertMaker.errorAlert("Error!", "The directory has not been initialized");
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}
}
