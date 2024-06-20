package vn.mekosoft.backup.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import vn.mekosoft.backup.action.BackupActionUtil;
import vn.mekosoft.backup.config.Config;
import vn.mekosoft.backup.impl.BackupProjectServiceImpl;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.impl.CoreScriptServiceImpl;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;
import vn.mekosoft.backup.model.TableModel;
import vn.mekosoft.backup.model.Time;
import vn.mekosoft.backup.service.BackupService;
import vn.mekosoft.backup.service.CoreScriptService;
import vn.mekosoft.backup.service.BackupProjectService;

public class Dashboard implements Initializable {
	@FXML
	private AnchorPane addProject_view, addTask_view, backupProject_view, backupTask_view, content_layout, content_view,
			createProject_view, dashboard_view, scheduler_view, tableDashboard_action, dataProtection_view;

	@FXML
	private Button button_addProject, button_back_addProject, button_backupProject, button_backupTask, button_dashboard,
			button_generate, button_save_addProject, button_scheduler;

	@FXML
	private TextField create_backupProjectId_textField, create_description_textField, create_hostname_textField,
			create_projectName_TextField, create_password_textField, create_username_textField, folder_path;

	@FXML
	private ComboBox<BackupProjectStatus> create_status_backupProject;

	@FXML
	private AnchorPane over_form;

	@FXML
	private VBox vbox_container;

	@FXML
	private Button button_config, button_sumary, button_dataProtection;
	@FXML
	private AnchorPane config_view;

	@FXML
	private TextField log_textField;
	@FXML
	private TextField config_textField;
	@FXML
	private Button save_config;
	@FXML
	private Button button_start;
	@FXML
	private Button button_refresh;
	
	@FXML
	private DetailsTask detailsTaskController;
	@FXML
	private ImageView logo;

	@FXML
	private BarChart<String, Integer> barChartDaily;
	@FXML
	private BarChart<String, Integer> stackBarChartStatus;
	@FXML
	private NumberAxis yStatus;
	@FXML
	private CategoryAxis xStatus;
	@FXML
	private CategoryAxis xDaily;

	@FXML
	private NumberAxis yDaily;
	@FXML
	private PieChart localPieChart;
	@FXML
	private PieChart remotePieChart;
	@FXML

	private TableView<TableModel> table_dashboard;

	@FXML
	private Label totalLocal, detail_barChart, numberOfTotal, numberOfStop, numberOfRunning;

	@FXML
	private Label totalRemote;

	@FXML
	private TableColumn<TableModel, String> projectName_col;

	@FXML
	private TableColumn<TableModel, String> taskName_col;

	@FXML
	private TableColumn<TableModel, Integer> folder_col;

	@FXML
	private TableColumn<TableModel, String> status_col;

	@FXML
	private TableColumn<TableModel, String> crontabLocal_col;

	@FXML
	private TableColumn<TableModel, String> crontabRemote_col;

	@FXML
	private TableColumn<TableModel, String> successful_col;

	@FXML
	private TableColumn<TableModel, String> failed_col;

	@FXML
	private TableColumn<TableModel, String> countBackup_col;

	@FXML
	private MenuButton menuDataRanges;
	@FXML
	private DatePicker startRange_date;
	@FXML
	private DatePicker endRange_date;
	@FXML
	private Button button_apply;

	@FXML
	private TextField refreshEvery_textField;

	@FXML
	private Button button_StartRefreshEvery;

	@FXML
	private ComboBox<Time> comboBox_RefreshEvery;
	private LocalDate startDate;
	private LocalDate endDate;

	private File logDir;
	private final Map<String, Integer> backupLocalCount = new TreeMap<>();
	private final Map<String, Integer> cleanupLocalCount = new TreeMap<>();
	private final Map<String, Integer> backupRemoteCount = new TreeMap<>();
	private final Map<String, Integer> cleanupRemoteCount = new TreeMap<>();
	private final Object lock = new Object();
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final Map<String, Integer> successLocalCount = new HashMap<>();
	private final Map<String, Integer> failLocalCount = new HashMap<>();
	private final Map<String, Integer> successRemoteCount = new HashMap<>();
	private final Map<String, Integer> failRemoteCount = new HashMap<>();

	@Override
	public void initialize(URL url, ResourceBundle resources) {

		logo.setVisible(true);
//		
		loadData();
		Config config = new Config();
		log_textField.setText(config.getLogFolderPath());
		log_textField.setEditable(false);
		config_textField.setText(config.getConfigFolderPath());
		config_textField.setEditable(false);
		save_config.setVisible(false);
		setupAutoRefresh(1, TimeUnit.SECONDS);
		// setupAuto(1, TimeUnit.SECONDS);
		create_status_backupProject.setValue(BackupProjectStatus.DANG_BIEN_SOAN);
		create_status_backupProject.setItems(FXCollections.observableArrayList(BackupProjectStatus.values()));
		// refresh_action();

		dataChartDaily();
		dataChartStatus();
		localPie();
		remotePie();
		// tableDashboard();
		comboBox_RefreshEvery.setValue(Time.SECOND);
		comboBox_RefreshEvery.setItems(FXCollections.observableArrayList(Time.values()));

		button_StartRefreshEvery.setOnAction(event -> startRefreshEvery());
		button_apply.setOnAction(event -> applyDateRange_action());

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

	}

	private void startRefreshEvery() {
		Time selectedTimeUnit = (Time) comboBox_RefreshEvery.getValue();

		String intervalText = refreshEvery_textField.getText();
		int interval;
		try {
			interval = Integer.parseInt(intervalText);
		} catch (NumberFormatException e) {
			return;
		}

		int intervalInSeconds = interval * selectedTimeUnit.toSeconds();

		stopAutoRefresh();
		setupAutoRefresh(intervalInSeconds, java.util.concurrent.TimeUnit.SECONDS);
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

	private ScheduledExecutorService scheduler;

	private void setupAutoRefresh(int interval, TimeUnit unit) {
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(this::refreshDashboard, 1, interval, unit);

	}

	private void toggleComponents() {
		Platform.runLater(() -> {
			boolean logoVisible = logo.isVisible();
			logo.setVisible(!logoVisible);
		});
	}

	private void setupAuto(int interval, TimeUnit unit) {
		scheduler = Executors.newScheduledThreadPool(1);

		scheduler.scheduleAtFixedRate(this::toggleComponents, 1, interval, unit);

	}

	private void refreshDashboard() {
		synchronized (lock) {
			dataChartDaily();
			dataChartStatus();
			// tableDashboard();
		}
	}

	public void showAddProjectView() {
		addProject_view.setVisible(true);
	}

	public AnchorPane getContentView() {
		return content_view;
	}

//	public void setFolderController(Folder folderController) {
//		this.folderController = folderController;
//	}
//
//	public void refreshFolder() {
//		if (folderController != null) {
//			folderController.refresh();
//		}
//	}

	public void rf() {
		vbox_container.getChildren().clear();
	}

	public void refresh_action() {
		vbox_container.getChildren().clear();
		loadData();
	}

	public void showAddProject() {
		addProject_view.setVisible(true);
	}

	private boolean isBackupStarted = false;

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

	public void startBackupTool_action(ActionEvent event) {
		if (!isBackupStarted) {
			isBackupStarted = true;
			CoreScriptService coreScriptService = new CoreScriptServiceImpl();

			try {
				String command = "/home/ubuntu/sftp_ver2/backup.sh --execute_all";
				coreScriptService.executeAll(command);
			} catch (IOException | InterruptedException e) {
			}
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
		vbox_container.getChildren().clear();

		BackupActionUtil.addProject(create_projectName_TextField.getText(), create_description_textField.getText(),
				create_hostname_textField.getText(), create_username_textField.getText(),
				create_password_textField.getText(), create_status_backupProject.getValue());
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

	public void loadData() {
		if (vbox_container != null) {

			vbox_container.getChildren().clear();
			tableDashboard();
			BackupProjectService backupService = new BackupProjectServiceImpl();
			List<BackupProject> projects = backupService.loadProjectData();

			int totalTasks = 0;
			int runningTasks = 0;
			int stoppedTasks = 0;

			for (BackupProject project : projects) {
				addProject_Layout(project);
				List<BackupTask> tasks = project.getBackupTasks();
				totalTasks += tasks.size();

				for (BackupTask task : tasks) {
					addTask_Layout(task, project);
					switch (BackupTaskStatus.fromId(task.getBackupTaskStatus())) {
					case KHONG_HOAT_DONG:
						stoppedTasks++;
						break;
					case DA_DAT_LICH:
						runningTasks++;
						break;
					default:
						break;
					}
				}
			}

			updateLabels(totalTasks, runningTasks, stoppedTasks);
		}
	}

	private void updateLabels(int totalTasks, int runningTasks, int stoppedTasks) {
		numberOfTotal.setText(String.valueOf(totalTasks));
		numberOfStop.setText(String.valueOf(stoppedTasks));
		numberOfRunning.setText(String.valueOf(runningTasks));
	}

	public void show_projectView() {
		backupProject_view.setVisible(true);
	}

	public void addProject_action(ActionEvent event) {
		if (event.getSource() == button_addProject) {
			backupProject_view.setVisible(false);
			createProject_view.setVisible(true);
		}

	}

//	private String getRemoteStorageInfo() throws IOException, InterruptedException {
//		String totalCommand = "rclone about onedrive: | awk 'NR==1 {print $2}'";
//		String usedCommand = "rclone about onedrive: | awk 'NR==2 {print $2}'";
//		String availableCommand = "rclone about onedrive: | awk 'NR==3 {print $2}'";
//		String trashedCommand = "rclone about onedrive: | awk 'NR==4 {print $2}'";
//
//		String total = executeCommand(totalCommand);
//		String used = executeCommand(usedCommand);
//		String available = executeCommand(availableCommand);
//		String trashed = executeCommand(trashedCommand);
//
//		return total + " " + used + " " + available + " " + trashed;
//	}

	public void refreshChart() {

	}

	private String executeCommand(String command) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command("bash", "-c", command);
		Process process = builder.start();
		process.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder output = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			output.append(line);
		}
		return output.toString();
	}

	private String getLocalStorageInfo() throws IOException, InterruptedException {
		String command = "df -h /home/ubuntu/sftp_ver2/ | awk 'NR==2 {print $2, $3, $4}'";
		String[] info = executeCommand(command).split(" ");
		double total = parseAndConvertValue(info[0]);
		double used = parseAndConvertValue(info[1]);
		double available = parseAndConvertValue(info[2]);
		return formatValueWithUnit(total) + " " + formatValueWithUnit(used) + " " + formatValueWithUnit(available);
	}

	private String getRemoteStorageInfo() throws IOException, InterruptedException {
		String totalCommand = "rclone about pve: | awk 'NR==1 {print $2}'";
		String usedCommand = "rclone about pve: | awk 'NR==2 {print $2}'";
		String availableCommand = "rclone about pve: | awk 'NR==3 {print $2}'";
		String trashedCommand = "rclone about pve: | awk 'NR==4 {print $2}'";

		double total = parseAndConvertValue(executeCommand(totalCommand));
		double used = parseAndConvertValue(executeCommand(usedCommand));
		double available = parseAndConvertValue(executeCommand(availableCommand));
		double trashed = parseAndConvertValue(executeCommand(trashedCommand));

		return formatValueWithUnit(total) + " " + formatValueWithUnit(used) + " " + formatValueWithUnit(available) + " "
				+ formatValueWithUnit(trashed);
	}

	private double parseAndConvertValue(String str) {
		String numericValue = str.replaceAll("[^\\d.,]", "").replace(',', '.');
		double value = Double.parseDouble(numericValue);
		if (str.contains("T")) {
			return value * 1024 * 1024;
		} else if (str.contains("G")) {
			return value * 1024;
		} else if (str.contains("M")) {
			return value;
		} else if (str.contains("K")) {
			return value / 1024;
		} else {
			return value;
		}
	}

	private String formatValueWithUnit(double value) {
		if (value < 1024) {
			return String.format("%.1fM", value);
		} else if (value < 1024 * 1024) {
			return String.format("%.1fG", value / 1024);
		} else {
			return String.format("%.1fT", value / (1024 * 1024));
		}
	}

	public void localPie() {
		try {
			String[] localInfo = getLocalStorageInfo().split(" ");
			double totalValue = parseAndConvertValue(localInfo[0]);
			double usedValue = parseAndConvertValue(localInfo[1]);
			double availableValue = parseAndConvertValue(localInfo[2]);

			String total = formatValueWithUnit(totalValue);
			String used = formatValueWithUnit(usedValue);
			String available = formatValueWithUnit(availableValue);

			System.out.println("Local Info: Total - " + total + ", Used - " + used + ", Available - " + available);

			ObservableList<PieChart.Data> pieChartLocal = FXCollections.observableArrayList(
					new PieChart.Data("Used: ", usedValue), new PieChart.Data("Available: ", availableValue));

			pieChartLocal.forEach(data -> {
				String unitValue = formatValueWithUnit(data.getPieValue());
				data.nameProperty().bind(Bindings.concat(data.getName(), " ", unitValue));
			});

			totalLocal.setText("Total: " + total);
			localPieChart.getData().addAll(pieChartLocal);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();

		}
	}

	public void remotePie() {
		try {
			String[] remoteInfo = getRemoteStorageInfo().split(" ");
			double totalValue = parseAndConvertValue(remoteInfo[0]);
			double usedValue = parseAndConvertValue(remoteInfo[1]);
			double availableValue = parseAndConvertValue(remoteInfo[2]);
			double trashedValue = parseAndConvertValue(remoteInfo[3]);

			String total = formatValueWithUnit(totalValue);
			String used = formatValueWithUnit(usedValue);
			String available = formatValueWithUnit(availableValue);
			String trashed = formatValueWithUnit(trashedValue);

			System.out.println("Remote Info: Total - " + total + ", Used - " + used + ", Available - " + available
					+ ", Trashed - " + trashed);

			ObservableList<PieChart.Data> pieChartRemote = FXCollections.observableArrayList(
					new PieChart.Data("Used: ", usedValue), new PieChart.Data("Available: ", availableValue),
					new PieChart.Data("Trashed: ", trashedValue));

			pieChartRemote.forEach(data -> {
				String unitValue = formatValueWithUnit(data.getPieValue());
				data.nameProperty().bind(Bindings.concat(data.getName(), " ", unitValue));
			});

			totalRemote.setText("Total: " + total);
			remotePieChart.getData().addAll(pieChartRemote);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();

		}
	}

	public void saveConfig_action() {
//
//		String logFolderPath = log_textField.getText();
//		String configFolderPath = config_textField.getText();
//
//		Config config = new Config();
//
//		config.setLogFolderPath(logFolderPath);
//		config.setConfigFolderPath(configFolderPath);
//
	}

	public void generate_action(ActionEvent event) {
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

	/* ==================== HANDLE CHART ==================== */

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

	public Map<LocalDate, Integer> getBackupCountByDate() {
		return getBackupCountByDate();
	}

	private void displayLabelForData(Object data) {
//		if (data instanceof XYChart.Data) {
//			XYChart.Data<String, Integer> chartData = (XYChart.Data<String, Integer>) data;
//			final Node node = chartData.getNode();
//
//			if (node != null) {
//				node.parentProperty().addListener((obs, oldParent, newParent) -> {
//					if (newParent != null) {
//						updateLabel(chartData);
//
//						node.boundsInParentProperty().addListener((ChangeListener<Bounds>) (ov, oldBounds, bounds) -> {
//							if (detail_barChart.isVisible()) {
//							}
//						});
//
//						node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
//							String info = formatInformation(chartData);
//							detail_barChart.setText(info);
//							detail_barChart.setVisible(true);
//						});
//
//						node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
//							detail_barChart.setVisible(false);
//						});
//					}
//				});
//			}
//		}
	}

//	private void updateLabel(XYChart.Data<String, Integer> chartData) {
//		String info = formatInformation(chartData);
//		detail_barChart.setText(info);
//	}

//	private String formatInformation(XYChart.Data<String, Integer> chartData) {
//		return String.format("Information: %s Backup... %d", chartData.getXValue(), chartData.getYValue());
//	}

	private void clearDashboard() {
		Platform.runLater(() -> barChartDaily.getData().clear());
	}

	private void filterDataByDateRange(LocalDate startDate, LocalDate endDate) {
		Map<String, Integer> filteredBackupLocalCount = backupLocalCount.entrySet().stream()
				.filter(entry -> isWithinDateRange(entry.getKey(), startDate, endDate))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		Map<String, Integer> filteredCleanupLocalCount = cleanupLocalCount.entrySet().stream()
				.filter(entry -> isWithinDateRange(entry.getKey(), startDate, endDate))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		Map<String, Integer> filteredBackupRemoteCount = backupRemoteCount.entrySet().stream()
				.filter(entry -> isWithinDateRange(entry.getKey(), startDate, endDate))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		Map<String, Integer> filteredCleanupRemoteCount = cleanupRemoteCount.entrySet().stream()
				.filter(entry -> isWithinDateRange(entry.getKey(), startDate, endDate))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		updateBarChart(filteredBackupLocalCount, filteredCleanupLocalCount, filteredBackupRemoteCount,
				filteredCleanupRemoteCount);
	}

	private void updateBarChart() {
		updateBarChart(backupLocalCount, cleanupLocalCount, backupRemoteCount, cleanupRemoteCount);
	}

	private String extractTask(String line) {
		String[] tasks = { "BACKUPLOCAL", "CLEANUPLOCAL", "BACKUPREMOTE", "CLEANUPREMOTE" };
		for (String task : tasks) {
			if (line.contains("[" + task + "]")) {
				return task;
			}
		}
		return null;
	}

	private void processLogLine(String line, Map<String, Integer> backupLocalCount,
			Map<String, Integer> cleanupLocalCount, Map<String, Integer> backupRemoteCount,
			Map<String, Integer> cleanupRemoteCount) {
		String[] parts = line.split(" ");
		if (parts.length >= 3) {
			String date = parts[0];
			String task = extractTask(line);

			if (task != null) {
				Integer count = line.contains("[START]") ? 1 : 0;
				switch (task) {
				case "BACKUPLOCAL":
					backupLocalCount.merge(date, count, Integer::sum);
					break;
				case "CLEANUPLOCAL":
					cleanupLocalCount.merge(date, count, Integer::sum);
					break;
				case "BACKUPREMOTE":
					backupRemoteCount.merge(date, count, Integer::sum);
					break;
				case "CLEANUPREMOTE":
					cleanupRemoteCount.merge(date, count, Integer::sum);
					break;
				}
			}
		}
	}

	public void dataChartDaily() {
		synchronized (lock) {
			Config config = new Config();
			String logDirPath = config.getLogFolderPath();
			String separator = File.separator;
			logDirPath = logDirPath.replace("\\", separator).replace("/", separator);
			logDir = new File(logDirPath);

			if (!logDir.exists() || !logDir.isDirectory()) {
				clearDashboard();
				return;
			}

			File[] logFiles = logDir.listFiles((dir, name) -> name.endsWith(".log"));
			if (logFiles == null || logFiles.length == 0) {
				clearDashboard();
				return;
			}

			backupLocalCount.clear();
			cleanupLocalCount.clear();
			backupRemoteCount.clear();
			cleanupRemoteCount.clear();

			for (File logFile : logFiles) {
				try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
					String line;
					while ((line = reader.readLine()) != null) {
						processLogLine(line, backupLocalCount, cleanupLocalCount, backupRemoteCount,
								cleanupRemoteCount);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (startDate != null && endDate != null) {
				filterDataByDateRange(startDate, endDate);
			} else {
				updateBarChart();
			}
		}
	}

	private void updateBarChart(Map<String, Integer> backupLocalCount, Map<String, Integer> cleanupLocalCount,
			Map<String, Integer> backupRemoteCount, Map<String, Integer> cleanupRemoteCount) {
		Platform.runLater(() -> {
			barChartDaily.getData().clear();
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
			series1.setName("Backup Local");
			backupLocalCount.entrySet().stream().filter(entry -> isValidDate(entry.getKey(), inputFormatter))
					.sorted(Map.Entry.comparingByKey()).forEach(entry -> {
						String formattedDate = LocalDate.parse(entry.getKey(), inputFormatter).format(outputFormatter);
						XYChart.Data<String, Integer> data = new XYChart.Data<>(formattedDate, entry.getValue());
						series1.getData().add(data);
						data.nodeProperty().addListener((obs, oldNode, newNode) -> {
							if (newNode != null) {
								displayLabelForData(data);
							}
						});
					});

			XYChart.Series<String, Integer> series2 = new XYChart.Series<>();
			series2.setName("Cleanup Local");
			cleanupLocalCount.entrySet().stream().filter(entry -> isValidDate(entry.getKey(), inputFormatter))
					.sorted(Map.Entry.comparingByKey()).forEach(entry -> {
						String formattedDate = LocalDate.parse(entry.getKey(), inputFormatter).format(outputFormatter);
						XYChart.Data<String, Integer> data = new XYChart.Data<>(formattedDate, entry.getValue());
						series2.getData().add(data);
						data.nodeProperty().addListener((obs, oldNode, newNode) -> {
							if (newNode != null) {
								displayLabelForData(data);
							}
						});
					});

			XYChart.Series<String, Integer> series3 = new XYChart.Series<>();
			series3.setName("Backup Remote");
			backupRemoteCount.entrySet().stream().filter(entry -> isValidDate(entry.getKey(), inputFormatter))
					.sorted(Map.Entry.comparingByKey()).forEach(entry -> {
						String formattedDate = LocalDate.parse(entry.getKey(), inputFormatter).format(outputFormatter);
						XYChart.Data<String, Integer> data = new XYChart.Data<>(formattedDate, entry.getValue());
						series3.getData().add(data);
						data.nodeProperty().addListener((obs, oldNode, newNode) -> {
							if (newNode != null) {
								displayLabelForData(data);
							}
						});
					});

			XYChart.Series<String, Integer> series4 = new XYChart.Series<>();
			series4.setName("Cleanup Remote");
			cleanupRemoteCount.entrySet().stream().filter(entry -> isValidDate(entry.getKey(), inputFormatter))
					.sorted(Map.Entry.comparingByKey()).forEach(entry -> {
						String formattedDate = LocalDate.parse(entry.getKey(), inputFormatter).format(outputFormatter);
						XYChart.Data<String, Integer> data = new XYChart.Data<>(formattedDate, entry.getValue());
						series4.getData().add(data);
						data.nodeProperty().addListener((obs, oldNode, newNode) -> {
							if (newNode != null) {
								displayLabelForData(data);
							}
						});
					});

			barChartDaily.getData().addAll(series1, series2, series3, series4);
		});
	}

	private boolean isValidDate(String dateStr, DateTimeFormatter formatter) {
		try {
			LocalDate.parse(dateStr, formatter);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	public boolean isWithinDateRange(String date, LocalDate startDate, LocalDate endDate) {

		try {
			LocalDate logDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
			return (logDate.isEqual(startDate) || logDate.isAfter(startDate))
					&& (logDate.isEqual(endDate) || logDate.isBefore(endDate));
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	private void processLogLineStatus(String line, Map<String, Integer> successLocalCount,
			Map<String, Integer> failLocalCount, Map<String, Integer> successRemoteCount,
			Map<String, Integer> failRemoteCount) {
		String[] parts = line.split(" ");
		if (parts.length >= 3) {
			String date = parts[0];
			if (line.contains("[SUCCESSFUL] [BACKUPLOCAL]")) {
				successLocalCount.merge(date, 1, Integer::sum);
			} else if (line.contains("[FAILED] [BACKUPLOCAL]")) {
				failLocalCount.merge(date, 1, Integer::sum);
			} else if (line.contains("[SUCCESS] [BACKUPREMOTE]")) {
				successRemoteCount.merge(date, 1, Integer::sum);
			} else if (line.contains("[FAILED] [BACKUPREMOTE]")) {
				failRemoteCount.merge(date, 1, Integer::sum);
			}
		}
	}

	public void dataChartStatus() {
		synchronized (lock) {
			parseLogs();

			Platform.runLater(() -> {
				stackBarChartStatus.getData().clear();
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
				DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

				Map<LocalDate, Integer> filteredSuccessLocal = filterDataByDateRange(successLocalCount);
				Map<LocalDate, Integer> filteredFailLocal = filterDataByDateRange(failLocalCount);
				Map<LocalDate, Integer> filteredSuccessRemote = filterDataByDateRange(successRemoteCount);
				Map<LocalDate, Integer> filteredFailRemote = filterDataByDateRange(failRemoteCount);

				XYChart.Series<String, Integer> seriesSuccessLocal = new XYChart.Series<>();
				seriesSuccessLocal.setName("Success Local");
				filteredSuccessLocal.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
					String formattedDate = entry.getKey().format(outputFormatter);
					XYChart.Data<String, Integer> data = new XYChart.Data<>(formattedDate, entry.getValue());
					seriesSuccessLocal.getData().add(data);
					data.nodeProperty().addListener((obs, oldNode, newNode) -> {
						if (newNode != null) {
							displayLabelForData(data);
						}
					});
				});

				XYChart.Series<String, Integer> seriesFailLocal = new XYChart.Series<>();
				seriesFailLocal.setName("Fail Local");
				filteredFailLocal.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
					String formattedDate = entry.getKey().format(outputFormatter);
					XYChart.Data<String, Integer> data = new XYChart.Data<>(formattedDate, entry.getValue());
					seriesFailLocal.getData().add(data);
					data.nodeProperty().addListener((obs, oldNode, newNode) -> {
						if (newNode != null) {
							displayLabelForData(data);
						}
					});
				});

				XYChart.Series<String, Integer> seriesSuccessRemote = new XYChart.Series<>();
				seriesSuccessRemote.setName("Success Remote");
				filteredSuccessRemote.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
					String formattedDate = entry.getKey().format(outputFormatter);
					XYChart.Data<String, Integer> data = new XYChart.Data<>(formattedDate, entry.getValue());
					seriesSuccessRemote.getData().add(data);
					data.nodeProperty().addListener((obs, oldNode, newNode) -> {
						if (newNode != null) {
							displayLabelForData(data);
						}
					});
				});

				XYChart.Series<String, Integer> seriesFailRemote = new XYChart.Series<>();
				seriesFailRemote.setName("Fail Remote");
				filteredFailRemote.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
					String formattedDate = entry.getKey().format(outputFormatter);
					XYChart.Data<String, Integer> data = new XYChart.Data<>(formattedDate, entry.getValue());
					seriesFailRemote.getData().add(data);
					data.nodeProperty().addListener((obs, oldNode, newNode) -> {
						if (newNode != null) {
							displayLabelForData(data);
						}
					});
				});

				stackBarChartStatus.getData().addAll(seriesSuccessLocal, seriesFailLocal, seriesSuccessRemote,
						seriesFailRemote);
			});
		}
	}

	private Map<LocalDate, Integer> filterDataByDateRange(Map<String, Integer> logData) {
		if (startDate == null || endDate == null) {
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			return logData.entrySet().stream()
					.map(entry -> Map.entry(LocalDate.parse(entry.getKey(), inputFormatter), entry.getValue()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}

		return logData.entrySet().stream()
				.map(entry -> Map.entry(LocalDate.parse(entry.getKey(), DateTimeFormatter.ofPattern("yyyy/MM/dd")),
						entry.getValue()))
				.filter(entry -> isWithinDateRange(entry.getKey(), startDate, endDate))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private boolean isWithinDateRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
		return (date.isEqual(startDate) || date.isAfter(startDate))
				&& (date.isEqual(endDate) || date.isBefore(endDate));
	}

	private void parseLogs() {
		successLocalCount.clear();
		failLocalCount.clear();
		successRemoteCount.clear();
		failRemoteCount.clear();

		Config config = new Config();
		String logDirPath = config.getLogFolderPath();
		String separator = File.separator;
		logDirPath = logDirPath.replace("\\", separator).replace("/", separator);
		File logDir = new File(logDirPath);

		if (!logDir.exists() || !logDir.isDirectory()) {
			return;
		}

		File[] logFiles = logDir.listFiles((dir, name) -> name.endsWith(".log"));
		if (logFiles == null || logFiles.length == 0) {
			return;
		}

		for (File logFile : logFiles) {
			try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
				String line;
				while ((line = reader.readLine()) != null) {
					processLogLineStatus(line, successLocalCount, failLocalCount, successRemoteCount, failRemoteCount);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* ==================== HANDLE TABLE ==================== */

	private List<BackupProject> projects;

	private Map<String, Integer> localCount = new HashMap<>();
	private Map<String, Integer> remoteCount = new HashMap<>();

	private BackupTask convertToBackupTask(TableModel selectedTask, BackupProject selectedProject) {
		if (selectedProject == null) {
			return null;
		}

		for (BackupTask task : selectedProject.getBackupTasks()) {
			if (task.getName().equals(selectedTask.getBackupTaskName())) {
				return task;
			}
		}

		return null;
	}

	private BackupProject getSelectedProject(TableModel selectedTask) {
		for (BackupProject project : projects) {
			for (BackupTask task : project.getBackupTasks()) {
				if (task.getName().equals(selectedTask.getBackupTaskName())) {
					return project;
				}
			}
		}
		return null;
	}

	private TableModel getSelectedTaskFromEvent(MouseEvent event) {
		TableView<TableModel> tableView = (TableView<TableModel>) event.getSource();
		return tableView.getSelectionModel().getSelectedItem();
	}

	@FXML
	public void action_table(MouseEvent event) {
		if (event.getClickCount() == 2) {
			TableModel selectedTask = getSelectedTaskFromEvent(event);
			BackupProject selectedProject = getSelectedProject(selectedTask);
			System.out.println(selectedProject);
			if (selectedTask != null && selectedProject != null) {
				try {
					FXMLLoader loader = new FXMLLoader(
							getClass().getResource("/vn/mekosoft/backup/view/detailsTableInfor.fxml"));
					Parent root = loader.load();

					DetailsTableInfor controller = loader.getController();
					controller.setDashboardController(this);
					BackupTask task = convertToBackupTask(selectedTask, selectedProject);
					controller.taskDetails(task, selectedProject);

					Stage stage = new Stage();
					stage.setScene(new Scene(root));
					stage.showAndWait();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (event.getClickCount() == 5) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/test.fxml"));
				Parent rParent = loader.load();
				Stage stage = new Stage();
				stage.setScene(new Scene(rParent));
				stage.showAndWait();
			} catch (Exception e) {
			}
		}
	}

	public void loadDataTable() {
		BackupService backupService = new BackupServiceImpl();
		projects = backupService.loadData();
		countBackupTasks();
	}

	public void tableDashboard() {
		table_dashboard.getItems().clear();
		ObservableList<TableModel> data = FXCollections.observableArrayList();
		loadDataTable();
		for (BackupProject project : projects) {
			String projectName = project.getProjectName();
			long projectId = project.getProjectId();

			for (BackupTask task : project.getBackupTasks()) {
				String backupTaskName = task.getName();
				long taskId = task.getBackupTaskId();
				int folderCount = task.getBackupFolders().size();
				String crontabLocal = task.getLocalSchedular();
				String crontabRemote = task.getRemoteSchedular();
				String backupTaskStatus = task.getBackupTaskStatusEnum().getDescription();

				String projectIdTaskName = projectId + "_" + taskId;
				int countBackupLocal = backupLocalCount.getOrDefault(projectIdTaskName + "_BACKUPLOCAL", 0);
				int countBackupRemote = backupRemoteCount.getOrDefault(projectIdTaskName + "_BACKUPREMOTE", 0);
				int countBackup = countBackupLocal + countBackupRemote;

				int successful = successfulCount.getOrDefault(projectIdTaskName, 0);
				int failed = failedCount.getOrDefault(projectIdTaskName, 0);

				if (task.getBackupTaskStatusEnum() == BackupTaskStatus.DA_DAT_LICH) {
					backupTaskStatus = "Running";
				} else if (task.getBackupTaskStatusEnum() == BackupTaskStatus.KHONG_HOAT_DONG
						|| task.getBackupTaskStatusEnum() == BackupTaskStatus.DANG_BIEN_SOAN) {
					backupTaskStatus = "Stop";
				}

				TableModel row = new TableModel(projectName, backupTaskName, String.valueOf(folderCount), countBackup,
						crontabLocal, crontabRemote, backupTaskStatus, successful, failed);

				data.add(row);
			}
		}

		projectName_col.setCellValueFactory(new PropertyValueFactory<>("projectName"));
		taskName_col.setCellValueFactory(new PropertyValueFactory<>("backupTaskName"));
		folder_col.setCellValueFactory(new PropertyValueFactory<>("folderCount"));
		countBackup_col.setCellValueFactory(new PropertyValueFactory<>("countBackup"));
		crontabLocal_col.setCellValueFactory(new PropertyValueFactory<>("crontabLocal"));
		crontabRemote_col.setCellValueFactory(new PropertyValueFactory<>("crontabRemote"));
		status_col.setCellValueFactory(new PropertyValueFactory<>("backupTaskStatus"));
		successful_col.setCellValueFactory(new PropertyValueFactory<>("successful"));
		failed_col.setCellValueFactory(new PropertyValueFactory<>("failed"));

		table_dashboard.setItems(data);

	}

	private Map<String, Integer> successfulCount = new HashMap<>();
	private Map<String, Integer> failedCount = new HashMap<>();

	private void countBackupTasks() {
		for (BackupProject project : projects) {
			long projectId = project.getProjectId();

			for (BackupTask task : project.getBackupTasks()) {
				long taskId = task.getBackupTaskId();
				String projectIdTaskName = projectId + "_" + taskId;

				String logFilePath = new Config().getConfigLog(projectId, taskId);

				if (logFilePath == null || logFilePath.isEmpty()) {
					continue;
				}

				try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
					String line;
					while ((line = reader.readLine()) != null) {
						if (line.contains("[END] [BACKUPLOCAL]")) {
							backupLocalCount.put(projectIdTaskName + "_BACKUPLOCAL",
									backupLocalCount.getOrDefault(projectIdTaskName + "_BACKUPLOCAL", 0) + 1);

							String nextLine = reader.readLine();
							if (nextLine != null && nextLine.contains("[FAILED] [BACKUPLOCAL]")) {
								failedCount.put(projectIdTaskName, failedCount.getOrDefault(projectIdTaskName, 0) + 1);
							} else {
								successfulCount.put(projectIdTaskName,
										successfulCount.getOrDefault(projectIdTaskName, 0) + 1);
							}
						} else if (line.contains("[END] [BACKUPREMOTE]")) {
							backupRemoteCount.put(projectIdTaskName + "_BACKUPREMOTE",
									backupRemoteCount.getOrDefault(projectIdTaskName + "_BACKUPREMOTE", 0) + 1);

							String nextLine = reader.readLine();
							if (nextLine != null && nextLine.contains("[FAILED] [BACKUPREMOTE]")) {
								failedCount.put(projectIdTaskName, failedCount.getOrDefault(projectIdTaskName, 0) + 1);
							} else {
								successfulCount.put(projectIdTaskName,
										successfulCount.getOrDefault(projectIdTaskName, 0) + 1);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
