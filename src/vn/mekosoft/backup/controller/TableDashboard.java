package vn.mekosoft.backup.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import vn.mekosoft.backup.config.ConfigReader;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;
import vn.mekosoft.backup.model.TableModel;
import vn.mekosoft.backup.service.BackupService;

public class TableDashboard implements Initializable {

	@FXML
	private TableColumn<TableModel, String> projectName_col, taskName_col, status_col, crontabLocal_col,
			crontabRemote_col, successful_col, failed_col, countBackup_col;
	@FXML
	private TableColumn<TableModel, Integer> folder_col;

	@FXML
	private TableView<TableModel> table_dashboard;

	private final Map<String, Integer> backupLocalCount = new TreeMap<>();
	private final Map<String, Integer> cleanupLocalCount = new TreeMap<>();
	private final Map<String, Integer> backupRemoteCount = new TreeMap<>();
	private final Map<String, Integer> cleanupRemoteCount = new TreeMap<>();

	private List<BackupProject> projects;
	private Map<String, Integer> successfulCount = new HashMap<>();
	private Map<String, Integer> failedCount = new HashMap<>();

	private Dashboard dashboardController;

	public void setDashboardController(Dashboard dashboardController) {
		this.dashboardController = dashboardController;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		updateData();
		
	}

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

	public void applyDateRange(LocalDate startDate, LocalDate endDate) {
		if (dashboardController != null) {
			dashboardController.filterDataByDateRange(startDate, endDate);
		}
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
					DetailsTableInfor detailsTableController = loader.getController();
					detailsTableController.setTableDashboardController(this);
					Stage stage = new Stage();
					stage.setScene(new Scene(root));
					stage.setTitle("Details Information Task");
					stage.getIcons().add(new Image("/vn/mekosoft/backup/view/img/company_logo.png"));
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
		
		status_col.setCellFactory(column -> {
			return new TableCell<TableModel, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						setText(item);
						if ("Running".equals(item)) {
							setStyle("-fx-text-fill: green;");
						} else if ("Stop".equals(item)) {
							setStyle("-fx-text-fill: red;");
						} else {
							setStyle("");
						}
					}
				}
			};
		});
	

	}

	private void countBackupTasks() {
		// Dọn sạch các giá trị cũ
		backupLocalCount.clear();
		backupRemoteCount.clear();
		successfulCount.clear();
		failedCount.clear();

		for (BackupProject project : projects) {
			long projectId = project.getProjectId();

			for (BackupTask task : project.getBackupTasks()) {
				long taskId = task.getBackupTaskId();
				String projectIdTaskName = projectId + "_" + taskId;

				String logFilePath = new ConfigReader().getConfigLog(projectId, taskId);

				if (logFilePath == null || logFilePath.isEmpty()) {
					continue;
				}

				File logFile = new File(logFilePath);
				if (!logFile.exists()) {
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
					
				}
			}
		}

	}

	public void updateData() {
	tableDashboard();
		
	}



}
