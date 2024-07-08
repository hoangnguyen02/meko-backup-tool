package vn.mekosoft.backup.controller;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
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
import vn.mekosoft.backup.config.ConfigReader;
import vn.mekosoft.backup.impl.BackupProjectServiceImpl;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.impl.BackupTaskServiceImpl;
import vn.mekosoft.backup.impl.CoreScriptServiceImpl;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;
import vn.mekosoft.backup.model.TableModel;
import vn.mekosoft.backup.model.Time;
import vn.mekosoft.backup.service.BackupProjectService;
import vn.mekosoft.backup.service.CoreScriptService;
import vn.mekosoft.backup.service.BackupTaskService;

public class Dashboard implements Initializable {
	@FXML
	private AnchorPane addProject_view, addTask_view, backupProject_view, backupTask_view, content_layout, content_view,
			createProject_view, dashboard_view, scheduler_view, tableDashboard_action, dataProtection_view, config_view;
	@FXML
	private Button button_addProject, button_back_addProject, button_backupProject, button_backupTask, button_dashboard,
			button_generate, button_save_addProject, button_scheduler, button_config, button_sumary,
			button_dataProtection, save_config, button_start, button_refresh, button_StartRefreshEvery,
			button_stopRefreshEvery, clearPicker_action;
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
	private CheckBox showPassword;
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
	private HBox date_range, range_afterPick, ranges;
	private ScheduledExecutorService scheduler;
	@FXML
	private PasswordField password_create;

	@FXML
	private ImageView icon_dashboard_1, icon_dashboard_2, icon_config_1, icon_config_2, icon_backup_1, icon_backup_2;
	@FXML
	private ScrollPane scroll_Dashboard;
	private ManagementTask managementTaskController;

	public void setManagementTaskController(ManagementTask managementTaskController) {
		this.managementTaskController = managementTaskController;
	}

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		title.setText("Dashboard");
		button_dashboard.setStyle(
				"-fx-background-color: #154e78; -fx-text-fill:#FFFFFF; -fx-background-radius:  1.0em; -fx-font-weight: bold; -fx-font-size: 16px;");
		icon_dashboard_1.setVisible(false);
		icon_dashboard_2.setVisible(true);
		config_menu();
		loadDashboard();
		logo.setVisible(true);
		loadData();

		// setupAutoRefresh(2, TimeUnit.SECONDS);
		create_status_backupProject.setValue(BackupProjectStatus.DANG_BIEN_SOAN);
		create_status_backupProject.setItems(FXCollections.observableArrayList(BackupProjectStatus.values()));

		comboBox_RefreshEvery.setValue(Time.SECOND);
		comboBox_RefreshEvery.setItems(FXCollections.observableArrayList(Time.values()));

		button_StartRefreshEvery.setOnAction(event -> startRefreshEvery());
		button_stopRefreshEvery.setOnAction(event -> stopAutoRefresh());
		setupDatePickers();
		range_afterPick.setVisible(false);


	}

	public void config_menu() {
		ConfigReader config = new ConfigReader();
		log_textField.setText(config.getLogFolderPath());
		config_textField.setText(config.getConfigFolderPath());
	}

	public void saveConfig_action() {
//	    String logPath = log_textField.getText();
//	    String configPath = config_textField.getText();
//
//	    Map<String, String> paths = new HashMap<>();
//	    paths.put("LOG_FOLDER_PATH", logPath);
//	    paths.put("CONFIG_FOLDER_PATH", configPath);
//
//	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
//	    String json = gson.toJson(paths);
//
//	    String fileName = "path.json";
//	    try (FileWriter writer = new FileWriter(fileName)) {
//	        writer.write(json);
//
//	        ConfigReader config = new ConfigReader();
//	        config.setLogFolderPath(logPath);
//	        config.setConfigFolderPath(configPath);
//
//	        config_menu();
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
	}

	public void showPass_action() {
		if (showPassword.isSelected()) {
			create_password_textField.setText(password_create.getText());
			password_create.setVisible(false);
			create_password_textField.setVisible(true);

		} else {
			password_create.setText(create_password_textField.getText());
			password_create.setVisible(true);
			create_password_textField.setVisible(false);
		}
	}

	private boolean isBackupStarted = false;

	public void startBackupTool_action(ActionEvent event) {
		if (!isBackupStarted) {
			Optional<ButtonType> result = AlertMaker.showConfirmAlert("Confirm",
					"Are you sure you want to run all backup processes?");
			if (result.isPresent() && result.get() == ButtonType.OK) {
				isBackupStarted = true;
				CoreScriptService coreScriptService = new CoreScriptServiceImpl();
				try {
					String backupToolPaString = System.getenv("BACKUPTOOL");
					String command = backupToolPaString + "/backup.sh --execute_all";
					coreScriptService.executeAll(command);

					AlertMaker.successfulAlert("Success", "Instant Backup Successful and Tasks Updated");
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
					AlertMaker.errorAlert("Error", "Failed to execute backup processes or update tasks");
				} finally {
					isBackupStarted = false;
				}
			}
		}
	}
//
//	private void updateTaskStatusToScheduled() {
//		String backupToolPath = System.getenv("BACKUPTOOL");
//		String configFilePath = backupToolPath + "/config.json";
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//		try (FileReader reader = new FileReader(configFilePath)) {
//			JsonElement jsonElement = JsonParser.parseReader(reader);
//			JsonObject rootObject = jsonElement.getAsJsonObject();
//			JsonArray projectsArray = rootObject.getAsJsonArray("backupProjects");
//
//			boolean hasChanges = false;
//
//			for (JsonElement projectElement : projectsArray) {
//				JsonObject projectObject = projectElement.getAsJsonObject();
//				JsonArray tasksArray = projectObject.getAsJsonArray("backupTasks");
//
//				for (JsonElement taskElement : tasksArray) {
//					JsonObject taskObject = taskElement.getAsJsonObject();
//					int currentStatus = taskObject.get("backupTaskStatus").getAsInt();
//
//					if (currentStatus != BackupTaskStatus.DA_DAT_LICH.getId()) {
//						taskObject.addProperty("backupTaskStatus", BackupTaskStatus.DA_DAT_LICH.getId());
//						hasChanges = true;
//					}
//				}
//			}
//
//			if (hasChanges) {
//				try (FileWriter writer = new FileWriter(configFilePath)) {
//					gson.toJson(rootObject, writer);
//				}
//			} else {
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

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
				applyDateRange_action();
			}
		});

		endRange_date.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				endPicker.setText(dateFormatter.format(newValue));
				applyDateRange_action();

			}
		});
		updateDateRange1Visibility();
		range_afterPick.setVisible(true);

	}

	private boolean isAutoRefreshRunning = false;

	private void updateDateRange1Visibility() {
		boolean bothDatesSelected = startRange_date.getValue() != null && endRange_date.getValue() != null;
		range_afterPick.setVisible(bothDatesSelected);
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
		if (interval <= 0) {
			AlertMaker.errorAlert("Error", "Please enter a positive number for the interval.");
			return;
		}
		int intervalInSeconds = interval * selectedTimeUnit.toSeconds();
		updateAllData();
		stopAutoRefresh();
		setupAutoRefresh(intervalInSeconds, TimeUnit.SECONDS);
		button_StartRefreshEvery.setDisable(true);

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
		isAutoRefreshRunning = false;
		button_StartRefreshEvery.setDisable(isAutoRefreshRunning);
	}

	private void updateRefreshButtonStates() {
	}

	public void clearPicker_action() {
		startRange_date.setValue(null);
		endRange_date.setValue(null);
		startPicker.clear();
		endPicker.clear();
		startDate = null;
		endDate = null;
		loadDashboard();
		range_afterPick.setVisible(false);
	}

	public void loadDashboard() {
		vbox_Dashboard.getChildren().clear();
		taskOverView_layuout();
		backupDailyChartDaily_layout();
		backupTaskStatus_layout();

		storageSpace_layout();
		tableDashboard_layuout();
		updateAllData();
	}


	private void updateAllData() {
		if (taskOverViewController != null) {
			taskOverViewController.updateData();
		}
		if (backupDailyChartController != null) {
			backupDailyChartController.updateChart();
		}
		if (backupTaskStatusChartController != null) {
			backupTaskStatusChartController.updateChart();
		}
		if (storageSpaceController != null) {
			storageSpaceController.updateData();
		}
		if (tableDashboardController != null) {
			tableDashboardController.updateData();
		}
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
			updateAllData();
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

	public void switch_form(ActionEvent event) {
		dashboard_view.setVisible(false);
		backupProject_view.setVisible(false);
		// backupTask_view.setVisible(false);
		// scheduler_view.setVisible(false);
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

	public void clear() {
		create_backupProjectId_textField.clear();
		create_projectName_TextField.clear();
		create_description_textField.clear();
		create_hostname_textField.clear();
		create_username_textField.clear();
		create_password_textField.clear();
		password_create.clear();
		create_status_backupProject.setValue(BackupProjectStatus.DANG_BIEN_SOAN);
	}

	public void addProject_save_action(ActionEvent event) {
		ConfigReader config = new ConfigReader();
		String CONFIG_FOLDER_PATH = config.getConfigFolderPath();

		List<BackupProject> backupProjects = new BackupServiceImpl().loadData();

		long currentMaxProjectId = 0;
		for (BackupProject project : backupProjects) {
			if (project.getProjectId() > currentMaxProjectId) {
				currentMaxProjectId = project.getProjectId();
			}
		}
		long projectId = currentMaxProjectId + 1;

		String projectName = create_projectName_TextField.getText();
		String description = create_description_textField.getText();
		String hostname = create_hostname_textField.getText();
		String username = create_username_textField.getText();
		String password = password_create.getText();
		BackupProjectStatus projectStatus = create_status_backupProject.getValue();
		if (projectName.isEmpty() || hostname.isEmpty() || username.isEmpty() || password.isEmpty()
				|| projectStatus == null) {
			AlertMaker.errorAlert("Error", "All fields must be filled out.");
			return;
		}
		BackupProject newProject = new BackupProject();
		newProject.setProjectId(projectId);
		newProject.setProjectName(projectName);
		newProject.setDescription(description);
		newProject.setHostname(hostname);
		newProject.setUsername(username);
		newProject.setPassword(password);
		newProject.setBackupProjectStatusFromEnum(projectStatus);
		newProject.setBackupTasks(new ArrayList<>());

		backupProjects.add(newProject);

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
		}

		loadData();
		createProject_view.setVisible(false);
		backupProject_view.setVisible(true);
		refresh_action();
		clear();
	}

	public void connection() {

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
			e.printStackTrace();

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
				return;
			}

			tableDashboardController = loader.getController();
			tableDashboardController.setDashboardController(this);

			if (vbox_Dashboard != null) {
				vbox_Dashboard.getChildren().add(root);
				VBox.setVgrow(root, Priority.ALWAYS);
			} else {
			}

			applyDateRange_action();

		} catch (Exception e) {
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

	public void generate_action(ActionEvent event) {

	}

	public void applyDateRange_action() {
		startDate = startRange_date.getValue();
		endDate = endRange_date.getValue();

		if (startDate != null && endDate != null) {
			if (endDate.isBefore(startDate)) {
				AlertMaker.errorAlert("Invalid Date Range", "End date cannot be before start date.");
				return;
			}

			filterDataByDateRange(startDate, endDate);
			range_afterPick.setVisible(true);
			startPicker.setText(dateFormatter.format(startDate));
			endPicker.setText(dateFormatter.format(endDate));

		} else {
			range_afterPick.setVisible(false);
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

		// tableDashboardController.filterDataByDateRange(startDate, endDate);

	}
}
