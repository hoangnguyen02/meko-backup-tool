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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.service.BackupService;

public class ManagementTask implements Initializable{
    @FXML
    private Button button_details;

    @FXML
    private Button button_status;

    @FXML
    private AnchorPane infor_task;

    @FXML
    private Label name_task;
    private BackupProject currentProject;

    private BackupTask currentTask;
    
    public void status_action(ActionEvent event) {
    	String currentText = button_status.getText();
		if (currentText.equals("Hoạt động")) {
			button_status.setText("Tạm dừng");
		} else {
			button_status.setText("Hoạt động");
		}
    }
    
    public void details_action(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/detailsTask.fxml"));
        Parent root = loader.load();
        
        DetailsTask details = loader.getController();
        details.taskDetails(currentTask,currentProject); 
        
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		
	}
	
	 public void taskData(BackupTask task, BackupProject project) {
	        this.currentTask = task;
	        this.currentProject = project;
	        name_task.setText(task.getName());
	    }
}
