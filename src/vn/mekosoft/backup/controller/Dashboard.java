package vn.mekosoft.backup.controller;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.google.gson.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;
import vn.mekosoft.backup.service.BackupService;

public class Dashboard implements Initializable {
	 @FXML
	    private AnchorPane addTask_view;

	    @FXML
	    private AnchorPane backupProject_view;

	    @FXML
	    private AnchorPane backupTask_view;

	    @FXML
	    private Button button_addProject;

	    @FXML
	    private Button button_backupProject;

	    @FXML
	    private Button button_backupTask;

	    @FXML
	    private Button button_dashboard;

	    @FXML
	    private Button button_generate;

	    @FXML
	    private Button button_scheduler;

	    @FXML
	    private AnchorPane content_layout;

	    @FXML
	    private AnchorPane content_view;

	    @FXML
	    private AnchorPane dashboard_view;

	    @FXML
	    private TextField folder_path;

	    @FXML
	    private TextField folder_path1;

	    @FXML
	    private TextField folder_path2;

	    @FXML
	    private AnchorPane list_backupProject;

	    @FXML
	    private AnchorPane list_menu;

	    @FXML
	    private TextField local_cronTab;

	    @FXML
	    private TextField local_path;

	    @FXML
	    private TextField local_retention;

	    @FXML
	    private AnchorPane over_form;

	    @FXML
	    private Label project_name_BP;

	    @FXML
	    private TextField remote_cronTab;

	    @FXML
	    private TextField remote_path;

	    @FXML
	    private TextField remote_retention;

	    @FXML
	    private AnchorPane scheduler_view;

	    @FXML
	    private TextField task_name;

	    @FXML
	    private VBox vbox_container;
	    
	    @FXML
	    private Button button_addTask; 
	    
	    @FXML
	    private Button addTask_action;
	    

	private List<BackupProject> projects;
	private List<BackupTask> tasks;

	public void generate_action(ActionEvent event) {
		System.out.println("Generate");	

		BackupService backupService = new BackupServiceImpl();
		List<BackupProject> projects = backupService.loadData();

//	    Gson gson = new Gson();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonData = gson.toJson(projects);

		try {
			FileWriter fileWriter = new FileWriter("backup_data_1.json");
			fileWriter.write(jsonData);
			fileWriter.close();
			System.out.println("JSON file generated successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void switch_form(ActionEvent event) {
		if (event.getSource() == button_dashboard) {
			dashboard_view.setVisible(true);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(false);
			addTask_view.setVisible(false);

		} else if (event.getSource() == button_backupProject) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(true);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(false);
			addTask_view.setVisible(false);


		} else if (event.getSource() == button_backupTask) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(true);
			scheduler_view.setVisible(false);
			addTask_view.setVisible(false);


		} else if (event.getSource() == button_scheduler) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(true);
			addTask_view.setVisible(false);

		}
	}
	
	public void addTask_action(ActionEvent event) {
		if(event.getSource() == button_addTask) {
			backupProject_view.setVisible(false);
			addTask_view.setVisible(true);
		}			
	}
	
	public void addProject_action(ActionEvent event) {
		if (event.getSource() == button_addProject) {
			backupProject_view.setVisible(false);
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/addProject.fxml"));
				Parent root = loader.load();
				content_view.getChildren().removeAll();
				content_view.getChildren().setAll(root);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
//
//	public void addTask_Layout(BackupTask task, BackupProject project) {
//		try {
//			FXMLLoader loader = new FXMLLoader(
//					getClass().getResource("/vn/mekosoft/backup/view/managementTask.fxml"));
//			content_layout = loader.load();
//			ManagementTask controller = loader.getController();
//			controller.taskData(task, project);
//			vbox_container.getChildren().add(content_layout);
//			vbox_container.setMaxHeight(Double.MAX_VALUE);
//			content_layout.setMaxHeight(Double.MAX_VALUE);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

//	public void addProject_Layout(BackupProject project) {
//		try {
//			FXMLLoader loader = new FXMLLoader(
//					getClass().getResource("/vn/mekosoft/backup/view/managementProject.fxml"));
//			content_layout = loader.load();
//			ManagementProject controller = loader.getController();
//			controller.projectData(project);
//			vbox_container.getChildren().add(content_layout);
//			content_layout.setMaxHeight(Double.MAX_VALUE);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}



//	public void loadData() {
//		BackupService backupService = new BackupServiceImpl();
//		List<BackupProject> projects = backupService.loadData();
//
//		for (BackupProject project : projects) {
//			addProject_Layout(project);
//			List<BackupTask> tasks = project.getBackupTasks();
//			for (BackupTask task : tasks) {
//				addTask_Layout(task, project);
//			}
//		}
//	}

	@Override
	public void initialize(URL url, ResourceBundle resources) {
//		loadData();
	}

}

