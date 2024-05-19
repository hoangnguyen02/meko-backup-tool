package vn.mekosoft.backup.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;

public class ManagementTask implements Initializable {
	@FXML
	private Button button_details;

	@FXML
	private Button button_status;
	private BackupTask task;
	@FXML
	private Label task_name;

	

	public void status_action(ActionEvent event) {
		String currentText = button_status.getText();
		if (currentText.equals("Hoạt động")) {
			button_status.setText("Tạm dừng");
		} else {
			button_status.setText("Hoạt động");
		}
	}

	public void details_action(ActionEvent event) {
	    if(event.getSource() == button_details) {
	    }
	}


	@Override
	public void initialize(URL url, ResourceBundle resources) {

	}

	public void taskData(BackupTask task, BackupProject project) {
	
		task_name.setText(task.getName());
		
		
		
		
	}

}
