package vn.mekosoft.backup.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import vn.mekosoft.backup.action.AlertMaker;
import vn.mekosoft.backup.impl.BackupProjectServiceImpl;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;
import vn.mekosoft.backup.service.BackupProjectService;

public class EditProject implements Initializable {
	@FXML
	private AnchorPane addProject_view;

	@FXML
	private Button button_save_addProject;

	@FXML
	private AnchorPane createProject_view;

	@FXML
	private TextField create_backupProjectId_textField;

	@FXML
	private TextField create_description_textField;

	@FXML
	private TextField create_hostname_textField;

	@FXML
	private TextField create_password_textField;

	@FXML
	private TextField create_projectName_TextField;

	@FXML
	private ComboBox<BackupProjectStatus> create_status_backupProject;

	@FXML
	private TextField create_username_textField;
    @FXML
    private PasswordField password_edit;
    @FXML
    private CheckBox showPass;
    
	private BackupProject project;
	private BackupProjectService backupProjectService = new BackupProjectServiceImpl();

	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		create_status_backupProject.getItems().setAll(BackupProjectStatus.values());
	}

	public void showPassword_action() {
		if (showPass.isSelected()) {
			create_password_textField.setText(password_edit.getText());
			password_edit.setVisible(false);
			create_password_textField.setVisible(true);

		} else {
			password_edit.setText(create_password_textField.getText());
			password_edit.setVisible(true);
			create_password_textField.setVisible(false);
		}
	}
	public void setProject(BackupProject project) {
		this.project = project;
		create_backupProjectId_textField.setText(String.valueOf(project.getProjectId()));
		create_projectName_TextField.setText(project.getProjectName());
		create_description_textField.setText(project.getDescription());
		create_hostname_textField.setText(project.getHostname());
		create_username_textField.setText(project.getUsername());
		password_edit.setText(project.getPassword());
		create_status_backupProject.setValue(BackupProjectStatus.fromId(project.getBackupProjectStatus()));
	}

	public void editProject_save_action(ActionEvent event) {

		String projectName = create_projectName_TextField.getText();
		String description = create_description_textField.getText();
		String hostname = create_hostname_textField.getText();
		String username = create_username_textField.getText();
		String password = create_password_textField.getText();
		BackupProjectStatus status = create_status_backupProject.getValue();
		if (projectName.isEmpty() || hostname.isEmpty() || username.isEmpty()
				|| password.isEmpty() ) {
			AlertMaker.errorAlert("Error", "All fields must be filled out.");
			return;
		}
		if (project != null) {
			project.setProjectName(projectName);
			project.setDescription(description);
			project.setHostname(hostname);
			project.setUsername(username);
			project.setPassword(password);
			project.setBackupProjectStatusFromEnum(status);

			backupProjectService.updateProject(project.getProjectId(), project);

			
			closeCurrentStage(event);
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error!");
			alert.setHeaderText(null);
			alert.setContentText("Unable to update project. Please try again!");
			alert.showAndWait();
		}
	}

	private void closeCurrentStage(ActionEvent event) {
		Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
		currentStage.hide();
	}

}
