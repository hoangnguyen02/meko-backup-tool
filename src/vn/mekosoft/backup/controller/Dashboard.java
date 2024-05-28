package vn.mekosoft.backup.controller;

import java.io.FileNotFoundException;
import vn.mekosoft.backup.service.CoreScriptService;
import vn.mekosoft.backup.impl.CoreScriptServiceImpl;

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

import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vn.mekosoft.backup.action.BackupActionUtil;
import vn.mekosoft.backup.config.Config;
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
	private TextField create_projectName_TextField;

	@FXML
	private TextField create_password_textField;

	@FXML
	private ComboBox<BackupProjectStatus> create_status_backupProject;

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

	@FXML
	private Label folderPath_log;
	@FXML
	private AnchorPane log_view;

	@FXML
	private TextArea content_log;

	@FXML
	private Label inforProject_Task_log;
	@FXML
	private Button button_config;
	@FXML
	private AnchorPane config_view;

	@FXML
	private TextField log_textField;
	@FXML
	private TextField config_textField;
	private BackupProject project;
	@FXML
	private Button save_config;
	@FXML
	private Button button_start;
	@FXML
	private Button button_refresh;

	public void refresh_action(ActionEvent event) {
		vbox_container.getChildren().clear();
		loadData();
	}

	public void startBackupTool_action(ActionEvent event) {
		System.out.println("Starting backup tool...");
		CoreScriptService coreScriptService = new CoreScriptServiceImpl();

		try {
			String command = "/home/ubuntu/sftp_ver2/backup.sh --execute_all";
			coreScriptService.execute(command);
		} catch (IOException | InterruptedException e) {
			System.err.println("Error executing backup tool: " + e.getMessage());
		}
	}

	public void showLog() {
	}

	public void clear() {
		create_backupProjectId_textField.clear();
		create_projectName_TextField.clear();
		create_description_textField.clear();
		create_hostname_textField.clear();
		create_username_textField.clear();
		create_password_textField.clear();
		create_status_backupProject.setValue(null);
	}

	public void generate_action(ActionEvent event) {
//		System.out.println("Generate");
//
//		BackupService backupService = new BackupServiceImpl();
//		List<BackupProject> projects = backupService.loadData();
//
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//		JsonObject jObject = new JsonObject();
//		JsonArray jsonArray = gson.toJsonTree(projects).getAsJsonArray();
//		jObject.add("backupProjects", jsonArray);
//
//		String jsonData = gson.toJson(jObject);
//		// /home/ubuntu/sftp_ver2/config.json
//		try {
//			FileWriter fileWriter = new FileWriter("config.json");
//			fileWriter.write(jsonData);
//			fileWriter.close();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	public void addProject_save_action(ActionEvent event) {
		BackupActionUtil.addProject(create_backupProjectId_textField.getText(), create_projectName_TextField.getText(),
				create_description_textField.getText(), create_hostname_textField.getText(),
				create_username_textField.getText(), create_password_textField.getText(),
				create_status_backupProject.getValue());
		loadData(); // Refresh UI after saving the project
		createProject_view.setVisible(false);
		backupProject_view.setVisible(true);
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
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/vn/mekosoft/backup/view/managementProject.fxml"));
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
			config_view.setVisible(false);
			createProject_view.setVisible(false);

		} else if (event.getSource() == button_backupProject) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(true);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(false);
			config_view.setVisible(false);
			createProject_view.setVisible(false);

		} else if (event.getSource() == button_backupTask) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(true);
			scheduler_view.setVisible(false);
			config_view.setVisible(false);
			createProject_view.setVisible(false);

		} else if (event.getSource() == button_scheduler) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(true);
			config_view.setVisible(false);
			createProject_view.setVisible(false);

		} else if (event.getSource() == button_addProject) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(false);
			config_view.setVisible(false);

			createProject_view.setVisible(true);
		} else if (event.getSource() == button_config) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(false);

			createProject_view.setVisible(false);
			config_view.setVisible(true);
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
		Config config = new Config();

		log_textField.setText(config.getLogFolderPath());
		config_textField.setText(config.getConfigFolderPath());

		saveConfig_action();
		create_status_backupProject.setItems(FXCollections.observableArrayList(BackupProjectStatus.values()));
		loadData();
	}

	public void saveConfig_action() {

		String logFolderPath = log_textField.getText();
		String configFolderPath = config_textField.getText();

		Config config = new Config();

		config.setLogFolderPath(logFolderPath);
		config.setConfigFolderPath(configFolderPath);

		System.out.println("Cấu hình đã được lưu.");
	}

}
