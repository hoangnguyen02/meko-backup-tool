package vn.mekosoft.backup.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import vn.mekosoft.backup.action.AlertMaker;
import vn.mekosoft.backup.action.BackupActionUtil;
import vn.mekosoft.backup.config.ConfigReader;
import vn.mekosoft.backup.impl.BackupProjectServiceImpl;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.impl.CoreScriptServiceImpl;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.TableModel;
import vn.mekosoft.backup.model.Time;
import vn.mekosoft.backup.service.BackupProjectService;
import vn.mekosoft.backup.service.CoreScriptService;

public class Dashboard implements Initializable {
	@FXML
	private AnchorPane addProject_view, addTask_view, backupProject_view, backupTask_view, content_layout, content_view,
			createProject_view, dashboard_view, scheduler_view, tableDashboard_action, dataProtection_view, config_view;
	@FXML
	private Button button_addProject, button_back_addProject, button_backupProject, button_backupTask, button_dashboard,
			button_generate, button_save_addProject, button_scheduler, button_config, button_sumary,
			button_dataProtection, save_config, button_start, button_refresh, button_apply, button_StartRefreshEvery,button_stopRefreshEvery;
	@FXML
	private TextField create_backupProjectId_textField, create_description_textField, create_hostname_textField,
			create_projectName_TextField, create_password_textField, create_username_textField, folder_path,
			log_textField, config_textField, refreshEvery_textField, endPicker, startPicker;
	@FXML
	private ComboBox<BackupProjectStatus> create_status_backupProject;
	@FXML
	private ComboBox<Time> comboBox_RefreshEvery;
	@FXML
	private AnchorPane over_form;
	@FXML
	private VBox vbox_container, vbox_Dashboard, vbox_range;
	@FXML
	private ImageView logo;
	@FXML
	private BarChart<String, Integer> barChartDaily, stackBarChartStatus;
	@FXML
	private NumberAxis yStatus, yDaily;
	@FXML
	private CategoryAxis xStatus, xDaily;
	@FXML
	private PieChart localPieChart, remotePieChart;

	@FXML
	private ProgressIndicator circle_Load;
	@FXML

	private Label totalLocal, detail_barChart, numberOfTotal, numberOfStop, numberOfRunning, totalRemote, title;
	@FXML
	private TableColumn<TableModel, String> projectName_col, taskName_col, status_col, crontabLocal_col,
			crontabRemote_col, successful_col, failed_col, countBackup_col;
	@FXML
	private TableColumn<TableModel, Integer> folder_col;
	@FXML
	private MenuButton menuDataRanges;
	@FXML
	private DatePicker startRange_date, endRange_date;

	private LocalDate startDate;
	private LocalDate endDate;

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    @FXML
    private HBox date_range,date_range1,ranges;
	private ScheduledExecutorService scheduler;

	@FXML
	private ImageView icon_dashboard_1, icon_dashboard_2, icon_config_1, icon_config_2, icon_backup_1, icon_backup_2;

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		title.setText("Dashboard");
		button_dashboard.setStyle(
				"-fx-background-color: #154e78; -fx-text-fill:#FFFFFF; -fx-background-radius:  1.0em; -fx-font-weight: bold; -fx-font-size: 16px;");
		icon_dashboard_1.setVisible(false);
		icon_dashboard_2.setVisible(true);

		loadDashboard();
		logo.setVisible(true);
		loadData();
		ConfigReader config = new ConfigReader();
		log_textField.setText(config.getLogFolderPath());
		log_textField.setEditable(false);
		config_textField.setText(config.getConfigFolderPath());
		config_textField.setEditable(false);
		save_config.setVisible(false);
		// setupAutoRefresh(2, TimeUnit.SECONDS);
		create_status_backupProject.setValue(BackupProjectStatus.DANG_BIEN_SOAN);
		create_status_backupProject.setItems(FXCollections.observableArrayList(BackupProjectStatus.values()));

		comboBox_RefreshEvery.setValue(Time.SECOND);
		comboBox_RefreshEvery.setItems(FXCollections.observableArrayList(Time.values()));

		button_StartRefreshEvery.setOnAction(event -> startRefreshEvery());
		//button_apply.setOnAction(event -> applyDateRange_action());
		setupDatePickers();
	}

	private void setupDatePickers() {
		StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
			
			@Override
			public String toString(LocalDate date) {
				return (date != null) ? dateFormatter.format(date) : "";
			}

			@Override
			public LocalDate fromString(String string) {
				return (string != null && !string.isEmpty()) ? LocalDate.parse(string, dateFormatter) : null;
			}
		};
		startRange_date.setConverter(converter);
		endRange_date.setConverter(converter);
		
		  startRange_date.valueProperty().addListener((observable, oldValue, newValue) -> {
		        if (newValue != null) {
		            startPicker.setText(dateFormatter.format(newValue));
		        } else {
		            startPicker.setText("");
		        }
		    });
		    
		    endRange_date.valueProperty().addListener((observable, oldValue, newValue) -> {
		        if (newValue != null) {
		            endPicker.setText(dateFormatter.format(newValue));
		        } else {
		            endPicker.setText("");
		        }
		    });
		    updateDateRange1Visibility();
		    button_apply.setOnAction(event -> {
		        date_range1.setVisible(true);
		    });
	}
	private void updateDateRange1Visibility() {
	    boolean bothDatesSelected = startRange_date.getValue() != null && endRange_date.getValue() != null;
	    date_range1.setVisible(bothDatesSelected);
	}
	private void startRefreshEvery() {
		Time selectedTimeUnit = comboBox_RefreshEvery.getValue();
		String intervalText = refreshEvery_textField.getText();
		int interval;
		try {
			interval = Integer.parseInt(intervalText);
		} catch (NumberFormatException e) {
			AlertMaker.errorAlert("Error", "Please enter a valid number for the interval.");
			return;
		}

		int intervalInSeconds = interval * selectedTimeUnit.toSeconds();
		stopAutoRefresh();
		setupAutoRefresh(intervalInSeconds, TimeUnit.SECONDS);
	}

	public void stopAutoRefresh() {
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
			try {
				if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
					scheduler.shutdownNow();
				}
			} catch (InterruptedException e) {
				scheduler.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}



	private void loadDashboard() {
		Platform.runLater(() -> {
			vbox_Dashboard.getChildren().clear();
			taskOverView_layuout();
			backupDailyChartDaily_layout();
			backupTaskStatus_layout();
			storageSpace_layout();
			tableDashboard_layuout();
			updateAllData();
		});
	}

	private void updateAllData() {
		if (taskOverViewController != null) {
			taskOverViewController.updateData();
		}
		if (backupDailyChartController != null) {
			backupDailyChartController.updateChart();
		}
//	    if (backupTaskStatusChartController != null) {
//	        backupTaskStatusChartController.updateChart();
//	    }
//	    if (storageSpaceController != null) {
//	        storageSpaceController.updateData();
//	    }
//	    if (tableDashboardController != null) {
//	        tableDashboardController.updateData();
//	    }
	}

	private void setupAutoRefresh(int interval, TimeUnit unit) {
	    scheduler = Executors.newScheduledThreadPool(1);
	    scheduler.scheduleAtFixedRate(() -> {
	        toggleComponents();
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
	        toggleComponents();
	    }, 0, interval, unit);
	}

    private void toggleComponents() {
        Platform.runLater(() -> {
            if (logo.isVisible()) {
                logo.setVisible(false);
                circle_Load.setVisible(true);
            } else {
                circle_Load.setVisible(false);
                logo.setVisible(true);
            }
        });
    }


	public void showAddProjectView() {
		addProject_view.setVisible(true);
	}

	public AnchorPane getContentView() {
		return content_view;
	}

	public void rf() {
		vbox_container.getChildren().clear();
	}

	public void refresh_action() {
		Platform.runLater(() -> {
			vbox_container.getChildren().clear();
			loadDashboard();
			loadData();
		});
	}

	public void showAddProject() {
		addProject_view.setVisible(true);
	}

	private boolean isBackupStarted = false;

	public void switch_form(ActionEvent event) {
		dashboard_view.setVisible(false);
		backupProject_view.setVisible(false);
	//	backupTask_view.setVisible(false);
	//	scheduler_view.setVisible(false);
		config_view.setVisible(false);
		createProject_view.setVisible(false);

		if (event.getSource() == button_dashboard) {
			dashboard_view.setVisible(true);
			button_dashboard.setStyle(
					"-fx-background-color: #154e78; -fx-text-fill:#FFFFFF; -fx-background-radius:  1.0em; -fx-font-weight: bold; -fx-font-size: 16px;");
			button_backupProject.setStyle("-fx-background-color: transparent");
			button_config.setStyle("-fx-background-color: transparent");
			icon_dashboard_1.setVisible(false);
			icon_dashboard_2.setVisible(true);

			icon_backup_1.setVisible(true);
			icon_backup_2.setVisible(false);

			icon_config_1.setVisible(true);
			icon_config_2.setVisible(false);
			title.setText("Dashboard");
			ranges.setVisible(true);
		} else if (event.getSource() == button_backupProject) {
			backupProject_view.setVisible(true);
			button_backupProject.setStyle(
					"-fx-background-color: #154e78; -fx-text-fill:#FFFFFF; -fx-background-radius:  1.0em;-fx-font-weight: bold; -fx-font-size: 16px;");
			button_dashboard.setStyle("-fx-background-color: transparent");
			button_config.setStyle("-fx-background-color: transparent");
			icon_backup_1.setVisible(false);
			icon_backup_2.setVisible(true);

			icon_dashboard_1.setVisible(true);
			icon_dashboard_2.setVisible(false);

			icon_config_1.setVisible(true);
			icon_config_2.setVisible(false);
			title.setText("Backup Project");
			ranges.setVisible(false);

		} else if (event.getSource() == button_backupTask) {
			backupTask_view.setVisible(true);
		} else if (event.getSource() == button_scheduler) {
			scheduler_view.setVisible(true);
		} else if (event.getSource() == button_addProject) {
			createProject_view.setVisible(true);
			backupProject_view.setVisible(false);

		} else if (event.getSource() == button_config) {
			config_view.setVisible(true);
			button_config.setStyle(
					"-fx-background-color: #154e78; -fx-text-fill:#FFFFFF; -fx-background-radius:  1.0em;-fx-font-weight: bold; -fx-font-size: 16px;");
			button_dashboard.setStyle("-fx-background-color: transparent");
			button_backupProject.setStyle("-fx-background-color: transparent");
			icon_config_1.setVisible(false);
			icon_config_2.setVisible(true);

			icon_dashboard_1.setVisible(true);
			icon_dashboard_2.setVisible(false);

			icon_backup_1.setVisible(true);
			icon_backup_2.setVisible(false);
			title.setText("Config");
			ranges.setVisible(false);

		}
	}

	public void startBackupTool_action(ActionEvent event) {
		if (!isBackupStarted) {
			isBackupStarted = true;
			CoreScriptService coreScriptService = new CoreScriptServiceImpl();

			try {
				String command = "/home/ubuntu/sftp_ver2/backup.sh --execute_all";
				coreScriptService.executeAll(command);
			} catch (IOException | InterruptedException e) {
			}
			AlertMaker.successfulAlert("Success", "Instant Backup Successful");
		}
	}

	public void clear() {
		create_backupProjectId_textField.clear();
		create_projectName_TextField.clear();
		create_description_textField.clear();
		create_hostname_textField.clear();
		create_username_textField.clear();
		create_password_textField.clear();
		create_status_backupProject.setValue(BackupProjectStatus.DANG_BIEN_SOAN);
	}

	public void addProject_save_action(ActionEvent event) {
		ConfigReader config = new ConfigReader();
		String CONFIG_FOLDER_PATH = config.getConfigFolderPath();

		// Load existing projects
		List<BackupProject> backupProjects = new BackupServiceImpl().loadData();

		// Find the current max project ID
		long currentMaxProjectId = 0;
		for (BackupProject project : backupProjects) {
			if (project.getProjectId() > currentMaxProjectId) {
				currentMaxProjectId = project.getProjectId();
			}
		}
		long projectId = currentMaxProjectId + 1;

		// Read values from text fields and combo box
		String projectName = create_projectName_TextField.getText();
		String description = create_description_textField.getText();
		String hostname = create_hostname_textField.getText();
		String username = create_username_textField.getText();
		String password = create_password_textField.getText();
		BackupProjectStatus projectStatus = create_status_backupProject.getValue();
		if (projectName.isEmpty() || description.isEmpty() || hostname.isEmpty() || username.isEmpty()
				|| password.isEmpty() || projectStatus == null) {
			AlertMaker.errorAlert("Error", "All fields must be filled out.");
			return;
		}
		// Create a new BackupProject object
		BackupProject newProject = new BackupProject();
		newProject.setProjectId(projectId);
		newProject.setProjectName(projectName);
		newProject.setDescription(description);
		newProject.setHostname(hostname);
		newProject.setUsername(username);
		newProject.setPassword(password);
		newProject.setBackupProjectStatusFromEnum(projectStatus);
		newProject.setBackupTasks(new ArrayList<>());

		// Add the new project to the list
		backupProjects.add(newProject);

		// Write the updated list of projects to the config file
		try (FileWriter writer = new FileWriter(CONFIG_FOLDER_PATH)) {
			JsonArray backupProjectsArray = new JsonArray();
			for (BackupProject project : backupProjects) {
				JsonObject projectJson = new JsonObject();
				projectJson.addProperty("projectId", project.getProjectId());
				projectJson.addProperty("projectName", project.getProjectName());
				projectJson.addProperty("description", project.getDescription());
				projectJson.addProperty("hostname", project.getHostname());
				projectJson.addProperty("username", project.getUsername());
				projectJson.addProperty("password", project.getPassword());
				projectJson.addProperty("backupProjectStatus", project.getBackupProjectStatus());

				JsonArray backupTasksArray = new JsonArray();
				for (BackupTask task : project.getBackupTasks()) {
					JsonObject taskJson = new JsonObject();
					taskJson.addProperty("backupTaskId", task.getBackupTaskId());
					taskJson.addProperty("projectId", task.getProjectId());
					taskJson.addProperty("name", task.getName());
					taskJson.addProperty("localSchedular", task.getLocalSchedular());
					taskJson.addProperty("localPath", task.getLocalPath());
					taskJson.addProperty("localRetention", task.getLocalRetention());
					taskJson.addProperty("remoteSchedular", task.getRemoteSchedular());
					taskJson.addProperty("remotePath", task.getRemotePath());
					taskJson.addProperty("remoteRetention", task.getRemoteRetention());
					taskJson.addProperty("backupTaskStatus", task.getBackupTaskStatus());

					JsonArray backupFoldersArray = new JsonArray();
					for (BackupFolder folder : task.getBackupFolders()) {
						JsonObject folderJson = new JsonObject();
						folderJson.addProperty("backupFolderId", folder.getBackupFolderId());
						folderJson.addProperty("folderPath", folder.getFolderPath());
						folderJson.addProperty("backupTaskId", folder.getBackupTaskId());
						backupFoldersArray.add(folderJson);
					}
					taskJson.add("backupFolders", backupFoldersArray);
					backupTasksArray.add(taskJson);
				}
				projectJson.add("backupTasks", backupTasksArray);
				backupProjectsArray.add(projectJson);
			}

			JsonObject jsonObject = new JsonObject();
			jsonObject.add("backupProjects", backupProjectsArray);

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(jsonObject, writer);
		} catch (IOException e) {
			e.printStackTrace();
			AlertMaker.errorAlert("Error", "Failed to add project.");
		}

		loadData();
		createProject_view.setVisible(false);
		backupProject_view.setVisible(true);
		refresh_action();
		clear();
	}

	public void addTask_Layout(BackupTask task, BackupProject project) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/managementTask.fxml"));
			content_layout = loader.load();
			ManagementTask controller = loader.getController();

			controller.taskData(task, project);
			controller.setDashboardController(this);

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
			controller.setDashboardController(this);
			controller.projectData(project);

			vbox_container.getChildren().add(content_layout);
			content_layout.setMaxHeight(Double.MAX_VALUE);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BackupDailyChart backupDailyChartController;

	public void backupDailyChartDaily_layout() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/vn/mekosoft/backup/view/backupDailyChart.fxml"));
			content_layout = loader.load();
			backupDailyChartController = loader.getController();
			backupDailyChartController.setDashboardController(this);
			// vbox_range.getChildren().add(content_layout);
			vbox_Dashboard.getChildren().add(content_layout);
			content_layout.setMaxHeight(Double.MAX_VALUE);
			applyDateRange_action();
		} catch (Exception e) {

		}
	}

	private BackupTaskStatusChart backupTaskStatusChartController;

	public void backupTaskStatus_layout() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/vn/mekosoft/backup/view/backupTaskStatusChart.fxml"));
			content_layout = loader.load();
			backupTaskStatusChartController = loader.getController();
			backupTaskStatusChartController.setDashboardController(this);
			// vbox_range.getChildren().add(content_layout);
			vbox_Dashboard.getChildren().add(content_layout);
			content_layout.setMaxHeight(Double.MAX_VALUE);
			applyDateRange_action();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private StorageSpace storageSpaceController;

	public void storageSpace_layout() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/storageSpace.fxml"));
			content_layout = loader.load();
			storageSpaceController = loader.getController();
			storageSpaceController.setDashboardController(this);

			vbox_Dashboard.getChildren().add(content_layout);
			content_layout.setMaxHeight(Double.MAX_VALUE);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TaskOverView taskOverViewController;

	public void taskOverView_layuout() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/taskOverView.fxml"));
			content_layout = loader.load();
			taskOverViewController = loader.getController();
			taskOverViewController.setDashboardController(this);
			vbox_Dashboard.getChildren().add(content_layout);
			content_layout.setMaxHeight(Double.MAX_VALUE);
			applyDateRange_action();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TableDashboard tableDashboardController;

	public void tableDashboard_layuout() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/tableDashboard.fxml"));
			Parent root = loader.load();

			TableView<TableModel> tableView = (TableView<TableModel>) loader.getNamespace().get("table_dashboard");
			if (tableView == null) {
				System.out.println("TableView not found in FXML");
				return;
			}

			tableDashboardController = loader.getController();
			tableDashboardController.setDashboardController(this);

			if (vbox_Dashboard != null) {
				vbox_Dashboard.getChildren().add(root);
				VBox.setVgrow(root, Priority.ALWAYS);
			} else {
				System.out.println("vbox_Dashboard is null");
			}

			System.out.println("TableView loaded successfully");
			applyDateRange_action();

		} catch (Exception e) {
			System.out.println("Error loading TableView: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void loadData() {
		vbox_container.getChildren().clear();
		BackupProjectService backupService = new BackupProjectServiceImpl();
		List<BackupProject> projects = backupService.loadProjectData();
		for (BackupProject project : projects) {
			addProject_Layout(project);
			List<BackupTask> tasks = project.getBackupTasks();
			for (BackupTask task : tasks) {
				addTask_Layout(task, project);
			}
		}
	}

	public void addProject_action(ActionEvent event) {
		if (event.getSource() == button_addProject) {
			backupProject_view.setVisible(false);
			createProject_view.setVisible(true);
		}

	}

	public void refreshChart() {

	}

	public void saveConfig_action() {
	}

	public void generate_action(ActionEvent event) {

	}

	public void applyDateRange_action() {
		startDate = startRange_date.getValue();
		endDate = endRange_date.getValue();

		if (startDate != null && endDate != null) {
			if (endDate.isBefore(startDate)) {
				return;
			}

			filterDataByDateRange(startDate, endDate);
		}
	}

	public void filterDataByDateRange(LocalDate startDate, LocalDate endDate) {
		if (backupDailyChartController != null) {
			backupDailyChartController.filterDataByDateRange(startDate, endDate);
		}
		if (backupTaskStatusChartController != null) {
			backupTaskStatusChartController.filterDataByDateRange(startDate, endDate);
		}
//		if (tableDashboardController != null) {
//			tableDashboardController.filterDataByDateRange(startDate, endDate);
//		}

		backupDailyChartController.filterDataByDateRange(startDate, endDate);
		backupTaskStatusChartController.filterDataByDateRange(startDate, endDate);
		// tableDashboardController.filterDataByDateRange(startDate, endDate);

	}

}
