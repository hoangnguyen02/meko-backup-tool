package vn.mekosoft.backup.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

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
	private AnchorPane addProject_view;

	@FXML
	private AnchorPane addTask_view;

	@FXML
	private AnchorPane backupProject_view;

	@FXML
	private AnchorPane backupTask_view;

	@FXML
	private Button button_addProject;

	@FXML
	private Button button_back_addProject;

	@FXML
	private Button button_backupProject;

	@FXML
	private Button button_backupTask;

	@FXML
	private Button button_dashboard;

	@FXML
	private Button button_generate;

	@FXML
	private Button button_save_addProject;

	@FXML
	private Button button_scheduler;

	@FXML
	private AnchorPane content_layout;

	@FXML
	private AnchorPane content_view;

	@FXML
	private AnchorPane createProject_view;

	@FXML
	private TextField create_backupProjectId_textField;

	@FXML
	private TextField create_description_textField;

	@FXML
	private TextField create_hostname_textField;

	@FXML
	private TextField create_name;

	@FXML
	private TextField create_password_textField;

	@FXML
	private ComboBox<?> create_status_backupProject;

	@FXML
	private TextField create_username_textField;

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

	public void generate_action(ActionEvent event) {
		System.out.println("Generate");

		BackupService backupService = new BackupServiceImpl();
		List<BackupProject> projects = backupService.loadData();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		JsonObject jObject = new JsonObject();
		JsonArray jsonArray = gson.toJsonTree(projects).getAsJsonArray();
		jObject.add("backupProjects", jsonArray);

		String jsonData = gson.toJson(jObject);

		try {
			FileWriter fileWriter = new FileWriter("backup_data_final.json");
			fileWriter.write(jsonData);
			fileWriter.close();
			System.out.println("JSON file generated successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

//	public void addProject_save_action(ActionEvent event) throws IOException {
//	    long projectId = Long.parseLong(create_backupProjectId_textField.getText());
//	    String projectName = create_name_textField.getText();
//	    String hostname = create_hostname_textField.getText();
//	    String username = create_username_textField.getText();
//	    String password = create_password_textField.getText();
//
//	    // Tạo đối tượng BackupProject mới
//	    BackupProject newProject = new BackupProject();
//	    newProject.setProjectId(projectId);
//	    newProject.setProjectName(projectName);
//	    newProject.setHostname(hostname);
//	    newProject.setUsername(username);
//	    newProject.setPassword(password);
//
//	    // Load danh sách các dự án từ tệp JSON
//	    List<BackupProject> backupProjects = new BackupServiceImpl().loadData();
//
//
//	    // Thêm dự án mới vào danh sách
//	    backupProjects.add(newProject);
//
//	    // Lưu lại danh sách các dự án vào tệp JSON
//	    try (FileWriter writer = new FileWriter("new_project.json")) {
//	        JsonObject jsonObject = new JsonObject();
//	        JsonArray jsonArray = new Gson().toJsonTree(backupProjects).getAsJsonArray();
//	        jsonObject.add("backupProjects", jsonArray);
//	        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//	        gson.toJson(jsonObject, writer);
//	        System.out.println("Dự án đã được lưu");
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//	}


	 public void addProject_save_action(ActionEvent event) throws IOException {
	        long projectId = Long.parseLong(create_backupProjectId_textField.getText());
	        String projectName = create_name.getText();
	        String hostname = create_hostname_textField.getText();
	        String username = create_username_textField.getText();
	        String password = create_password_textField.getText();

	        // Tạo đối tượng BackupProject mới
	        BackupProject newProject = new BackupProject();
	        newProject.setProjectId(projectId);
	        newProject.setProjectName(projectName);
	        newProject.setHostname(hostname);
	        newProject.setUsername(username);
	        newProject.setPassword(password);
	        newProject.setBackupTasks(new ArrayList<>());

	        // Load danh sách các dự án từ tệp JSON
	        List<BackupProject> backupProjects = new BackupServiceImpl().loadData();

	        // Thêm dự án mới vào danh sách
	        backupProjects.add(newProject);

	        try (FileWriter writer = new FileWriter("new_project.json")) {
	            JsonArray backupProjectsArray = new JsonArray();
	            for (BackupProject project : backupProjects) {
	                JsonObject projectJson = new JsonObject();
	                projectJson.addProperty("projectId", project.getProjectId());
	                projectJson.addProperty("projectName", project.getProjectName());
	                projectJson.addProperty("hostname", project.getHostname());
	                projectJson.addProperty("username", project.getUsername());
	                projectJson.addProperty("password", project.getPassword());

	                JsonArray backupTasksArray = new JsonArray();
	                for (BackupTask task : project.getBackupTasks()) {
	                    JsonObject taskJson = new JsonObject();
	                    taskJson.addProperty("backupTaskId", task.getBackupTaskId());
	                    taskJson.addProperty("name", task.getName());
	                    taskJson.addProperty("localSchedular", task.getLocalSchedular());
	                    taskJson.addProperty("remoteSchedular", task.getRemoteSchedular());
	                    taskJson.addProperty("localPath", task.getLocalPath());
	                    taskJson.addProperty("remotePath", task.getRemotePath());
	                    taskJson.addProperty("localRetention", task.getLocalRetention());
	                    taskJson.addProperty("remoteRetention", task.getRemoteRetention());

	                    JsonArray backupFoldersArray = new JsonArray();
	                    for (BackupFolder folder : task.getBackupFolders()) {
	                        JsonObject folderJson = new JsonObject();
	                        folderJson.addProperty("folderId", folder.getBackupFolderId());
	                        folderJson.addProperty("folderPath", folder.getFolderPath());
	                        backupFoldersArray.add(folderJson);
	                    }
	                    taskJson.add("backupfolders", backupFoldersArray);

	                    backupTasksArray.add(taskJson);
	                }
	                projectJson.add("backupTasks", backupTasksArray);

	                backupProjectsArray.add(projectJson);
	            }

	            JsonObject jsonObject = new JsonObject();
	            jsonObject.add("backupProjects", backupProjectsArray);

	            Gson gson = new GsonBuilder().setPrettyPrinting().create();
	            gson.toJson(jsonObject, writer);
	            System.out.println("Dự án đã được lưu");

	            addProject_Layout(newProject);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }


	public void addTask_Layout(BackupTask task, BackupProject project) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/managementTask.fxml"));
			content_layout = loader.load();
			ManagementTask controller = loader.getController();
			controller.taskData(task, project);
			vbox_container.getChildren().add(content_layout);
			vbox_container.setMaxHeight(Double.MAX_VALUE);
			content_layout.setMaxHeight(Double.MAX_VALUE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void addProject_Layout(BackupProject project) {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/managementProject.fxml"));
	        content_layout = loader.load();
	        ManagementProject controller = loader.getController();
	        controller.projectData(project);
	        vbox_container.getChildren().add(content_layout);
	        content_layout.setMaxHeight(Double.MAX_VALUE);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	public void loadData() {
		BackupService backupService = new BackupServiceImpl();
		List<BackupProject> projects = backupService.loadData();

		for (BackupProject project : projects) {
			addProject_Layout(project);
			List<BackupTask> tasks = project.getBackupTasks();
			for (BackupTask task : tasks) {
				addTask_Layout(task, project);
			}
		}
	}

	public void switch_form(ActionEvent event) {
		if (event.getSource() == button_dashboard) {
			dashboard_view.setVisible(true);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(false);
			
			createProject_view.setVisible(false);

		} else if (event.getSource() == button_backupProject) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(true);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(false);

			createProject_view.setVisible(false);

		} else if (event.getSource() == button_backupTask) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(true);
			scheduler_view.setVisible(false);
		
			createProject_view.setVisible(false);

		} else if (event.getSource() == button_scheduler) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(true);
			
			createProject_view.setVisible(false);

		} else if (event.getSource() == button_addProject) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(false);
		 
			createProject_view.setVisible(true);
		}
	}

	public void addProject_action(ActionEvent event) {
		if (event.getSource() == button_addProject) {
			backupProject_view.setVisible(false);
			createProject_view.setVisible(true);
		}

	}

	public void addTaskLayout() {
		addTask_view.setVisible(true);
	}

	
	@Override
	public void initialize(URL url, ResourceBundle resources) {

		loadData();
	}

}
