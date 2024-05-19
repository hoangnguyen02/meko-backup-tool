package vn.mekosoft.backup.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Node;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;

public class AddProject implements Initializable {
	@FXML
	private AnchorPane addProject_view;

	@FXML
	private Button buttonBack_addProject;

	@FXML
	private TextField create_backupProjectId_textField;

	@FXML
	private Button create_button_Save;

	@FXML
	private TextField create_description_textField;

	@FXML
	private TextField create_hostname_textField;

	@FXML
	private TextField create_name_textField;

	@FXML
	private TextField create_password_textField;

	@FXML
	private ComboBox<BackupProjectStatus> create_status_backupProject;

	@FXML
	private TextField create_username_textField;

	public void back_action(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/dashboard.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		create_status_backupProject.getItems().setAll(BackupProjectStatus.values());
    
	}

	public void save_action(ActionEvent event) {
		String projectId = create_backupProjectId_textField.getText();
		String hostname = create_hostname_textField.getText();
		String username = create_username_textField.getText();
		String password = create_password_textField.getText();
		BackupProjectStatus status = (BackupProjectStatus) create_status_backupProject.getValue();

		BackupProject project = new BackupProject();
		System.out.println("Backup Project Saved: " + project);
		 back_action(event);
	}
	
	
}
