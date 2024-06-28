package vn.mekosoft.backup.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import vn.mekosoft.backup.action.AlertMaker;
import vn.mekosoft.backup.impl.BackupFolderServiceImpl;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.impl.BackupTaskServiceImpl;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;
import vn.mekosoft.backup.service.BackupFolderService;

public class DetailsTask implements Initializable {
	@FXML
	private AnchorPane addTask_view;

	@FXML
	private Button button_saveFolder;

	@FXML
	private Button button_save_inforTask;

	@FXML
	private AnchorPane folderPath_view;
	@FXML
	private AnchorPane folder_pane;

	@FXML
	private TextField folder_id;

	@FXML
	private TextField folder_path;

	@FXML
	private AnchorPane listFolder;

	@FXML
	private VBox vbox_folder;

	@FXML
	private TextField local_cronTab;

	@FXML
	private TextField local_path;

	@FXML
	private TextField local_retention;

	@FXML
	private Label project_name_BP;

	@FXML
	private TextField remote_cronTab;

	@FXML
	private TextField remote_path;

	@FXML
	private TextField remote_retention;

	@FXML
	private TextField task_id;

	@FXML
	private TextField task_name;

	@FXML
	private TextField config_textField;

	@FXML
	private Button save_config;

	@FXML
	private TextField get_FolderId;

	@FXML
	private TextField get_FolderPath;

	@FXML
	private AnchorPane content_folder;

	@FXML
	private Button button_delete;

	private BackupProject project;
	private BackupTask task;
	private BackupFolder folder;
	private static long currentMaxTaskId = 0;
	private static long currentMaxFolderId = 0;
	private Dashboard dashboardController;
	private BackupFolderService folderService = new BackupFolderServiceImpl();
	private Folder folderController;

	public void setDashboardController(Dashboard dashboardController) {
		this.dashboardController = dashboardController;
		this.folderService = new BackupFolderServiceImpl();
	}

	public void setProject(BackupProject project) {
		this.project = project;
	}

	public void setFolderController(Folder folderController) {
		this.folderController = folderController;
	}

	public void refreshView() {
		vbox_folder.getChildren().clear();
		taskDetails(task);
		if (dashboardController != null) {
			dashboardController.loadData();
			dashboardController.refresh_action();

		}
	}

	public void loadFolderDataForTask() {
		if (project != null && task != null) {
			List<BackupFolder> folders = folderService.loadFolderData(project.getProjectId(), task.getBackupTaskId());
			vbox_folder.getChildren().clear();

			for (BackupFolder folder : folders) {
				addFolderLayout(folder);
			}
		}
	}

	public void refreshFolder() {
		List<BackupFolder> newFolders = folderService.loadFolderData(project.getProjectId(), task.getBackupTaskId());
		List<BackupFolder> filteredFolders = new ArrayList<>();

		for (BackupFolder newFolder : newFolders) {
			boolean exists = false;
			for (BackupFolder existingFolder : task.getBackupFolders()) {
				if (existingFolder.getBackupFolderId() == newFolder.getBackupFolderId()) {
					exists = true;
					break;
				}
			}
			if (exists) {
				filteredFolders.add(newFolder);
			}
		}

		task.setBackupFolders(filteredFolders);
		vbox_folder.getChildren().clear();

		for (BackupFolder folder : filteredFolders) {
			addFolderLayout(folder);
		}
	}

	public long getProjectId() {
		return project.getProjectId();
	}

	public long getTaskId() {
		return task.getBackupTaskId();
	}
	   private TableDashboard tableDashboardController;

	    public void setTableDashboardController(TableDashboard controller) {
	        this.tableDashboardController = controller;
	    }
	public void saveFolder_action() {
		try {
			if (task != null) {
				long maxBackupFolderId = task.getBackupFolders().stream().mapToLong(BackupFolder::getBackupFolderId)
						.max().orElse(0);

				long newBackupFolderId = maxBackupFolderId + 1;
				String folderPath = folder_path.getText();
				BackupFolder newFolder = new BackupFolder(newBackupFolderId, folderPath, task.getBackupTaskId());

				boolean isEditing = false;
				if (folder != null) {
					isEditing = true;
					task.getBackupFolders().removeIf(f -> f.getBackupFolderId() == folder.getBackupFolderId());
				}

				task.getBackupFolders().add(newFolder);

				BackupServiceImpl backupService = new BackupServiceImpl();
				List<BackupProject> backupProjects = backupService.loadData();
				for (BackupProject proj : backupProjects) {
					if (proj.getProjectId() == project.getProjectId()) {
						for (BackupTask t : proj.getBackupTasks()) {
							if (t.getBackupTaskId() == task.getBackupTaskId()) {
								t.setBackupFolders(new ArrayList<>(task.getBackupFolders()));
								break;
							}
						}
						break;
					}
				}

				backupService.saveData(backupProjects);

				refreshView();

				if (!isEditing) {
					addFolderLayout(newFolder);
					folder_id.setText(String.valueOf(newBackupFolderId));
					folder_path.clear();
				}

				refreshView();
				 if (tableDashboardController != null) {
		                tableDashboardController.loadDataTable();
		                System.out.print("Gọi table" + tableDashboardController);
		            }
			}
		} catch (Exception e) {
			e.printStackTrace();
			AlertMaker.errorAlert("Error", "Failed to save Folder!");
		}
	}

	@FXML
	private void browseFolderPath(MouseEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select Folder");
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File selectedDirectory = directoryChooser.showDialog(stage);
		if (selectedDirectory != null) {
			folder_path.setText(selectedDirectory.getAbsolutePath());
		}
	}

	public void hideFolderPane() {
		folder_pane.setVisible(false);

	}
	public void saveInforTask_action(ActionEvent event) {
	    try {
	        BackupTaskServiceImpl backupTaskService = new BackupTaskServiceImpl();
	        List<BackupProject> backupProjects = backupTaskService.loadProjectData();

	        // Cập nhật đối tượng task với thông tin mới
	        task.setName(task_name.getText());
	        task.setLocalSchedular(local_cronTab.getText());
	        task.setRemoteSchedular(remote_cronTab.getText());
	        task.setLocalPath(local_path.getText());
	        task.setRemotePath(remote_path.getText());
	        task.setLocalRetention(Long.parseLong(local_retention.getText()));
	        task.setRemoteRetention(Long.parseLong(remote_retention.getText()));

	        // Thực hiện xác thực
	        if (!validateTaskInformation(backupProjects)) {
	            AlertMaker.errorAlert("Validation Error", "All fields must be filled out and the task must be unique.");
	            return;
	        }

	        // Tiếp tục lưu task
	        if (task.getBackupTaskId() != 0) {
	            updateTaskInformation(backupTaskService, backupProjects);
	        } else {
	            createNewTask(backupTaskService, backupProjects);
	            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	            currentStage.close();
	        }

	        verifyLocalPath();
	    } catch (Exception e) {
	        e.printStackTrace();
	        AlertMaker.errorAlert("Error", "Failed to save Task!");
	    }
	    refreshView();
	}



	private void updateTaskInformation(BackupTaskServiceImpl backupTaskService, List<BackupProject> backupProjects) {
	    try {
	        for (BackupProject proj : backupProjects) {
	            if (proj.getProjectId() == project.getProjectId()) {
	                List<BackupTask> tasks = proj.getBackupTasks();
	                for (int i = 0; i < tasks.size(); i++) {
	                    if (tasks.get(i).getBackupTaskId() == task.getBackupTaskId()) {
	                        tasks.set(i, task);
	                        break;
	                    }
	                }
	                break;
	            }
	        }

	        backupTaskService.saveProjectsToFile(backupProjects);
	    } catch (Exception e) {
	        e.printStackTrace();
	        AlertMaker.errorAlert("Error", "Failed to update Task!");
	    }
	}


	private void createNewTask(BackupTaskServiceImpl backupTaskService, List<BackupProject> backupProjects) {
	    try {
	        long maxTaskId = 0;
	        for (BackupProject proj : backupProjects) {
	            List<BackupTask> tasks = proj.getBackupTasks();
	            if (tasks != null) {
	                for (BackupTask task : tasks) {
	                    if (task.getBackupTaskId() > maxTaskId) {
	                        maxTaskId = task.getBackupTaskId();
	                    }
	                }
	            }
	        }
	        maxTaskId++; 
	        BackupTask newTask = new BackupTask();
	        newTask.setBackupTaskId(maxTaskId);
	        newTask.setProjectId(project.getProjectId());
	        newTask.setName(task_name.getText());
	        newTask.setLocalSchedular(local_cronTab.getText());
	        newTask.setRemoteSchedular(remote_cronTab.getText());
	        newTask.setLocalPath(local_path.getText());
	        newTask.setRemotePath(remote_path.getText());
	        newTask.setLocalRetention(Long.parseLong(local_retention.getText()));
	        newTask.setRemoteRetention(Long.parseLong(remote_retention.getText()));
	        newTask.setBackupTaskStatus(BackupTaskStatus.DANG_BIEN_SOAN.getId());
	        newTask.setBackupFolders(new ArrayList<>());

	        // Thêm task mới vào danh sách tasks của dự án
	        for (BackupProject proj : backupProjects) {
	            if (proj.getProjectId() == project.getProjectId()) {
	                List<BackupTask> tasks = proj.getBackupTasks();
	                if (tasks == null) {
	                    tasks = new ArrayList<>();
	                    proj.setBackupTasks(tasks);
	                }
	                tasks.add(newTask);
	                break;
	            }
	        }

	        // Lưu danh sách dự án sau khi thêm task mới
	        backupTaskService.saveProjectsToFile(backupProjects);
	        task = newTask; // Cập nhật task hiện tại thành task mới

	    } catch (Exception e) {
	        e.printStackTrace();
	        AlertMaker.errorAlert("Error", "Failed to save Task!");
	    }
	}



	private boolean validateTaskInformation(List<BackupProject> backupProjects) {
	    if (task.getName().isEmpty() || task.getLocalSchedular().isEmpty() || task.getRemoteSchedular().isEmpty() ||
	            task.getLocalPath().isEmpty() || task.getRemotePath().isEmpty() ||
	            String.valueOf(task.getLocalRetention()).isEmpty() || String.valueOf(task.getRemoteRetention()).isEmpty()) {
	        return false;
	    }

	    return true;
	}




	@FXML
	private void browseLocalPath(MouseEvent event) {
		if(event.getClickCount() == 10) {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select Directory");

			String userHome = System.getProperty("user.home");
			directoryChooser.setInitialDirectory(new File(userHome));

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

			File selectedDirectory = directoryChooser.showDialog(stage);
			if (selectedDirectory != null) {
				String path = Paths.get(selectedDirectory.getAbsolutePath()).toString();
				local_path.setText(path);
				  saveLocalPath(path);
			}
		}
	}

	// Phương thức để lưu đường dẫn
	private void saveLocalPath(String path) {
		// Chuyển đổi đường dẫn thành định dạng độc lập với hệ điều hành trước khi lưu
		String normalizedPath = Paths.get(path).toString();
		task.setLocalPath(normalizedPath);
	}

	// Khi cần sử dụng đường dẫn
	private File getLocalPathFile() {
		String localPath = task.getLocalPath();
		return Paths.get(localPath).toFile();
	}
	public void verifyLocalPath() {
	    File localPathFile = getLocalPathFile();
	    if (!localPathFile.exists()) {
	      //  AlertMaker.showConfirmAlert("Warning", "Local backup path does not exist!");
	    } else if (!localPathFile.isDirectory()) {
	       // AlertMaker.showConfirmAlert("Warning", "Local backup path is not a directory!");
	    } else {
	      //  AlertMaker.showConfirmAlert("Information", "Local backup path is valid.");
	    }
	}
	
	public void taskDetails(BackupTask task) {
		this.task = task;

		if (task != null) {
			task_id.setText(String.valueOf(task.getBackupTaskId()));
			task_name.setText(task.getName());
			local_cronTab.setText(task.getLocalSchedular());
			remote_cronTab.setText(task.getRemoteSchedular());
			local_path.setText(task.getLocalPath());
			remote_path.setText(task.getRemotePath());
			local_retention.setText(String.valueOf(task.getLocalRetention()));
			remote_retention.setText(String.valueOf(task.getRemoteRetention()));
			refreshFolder();
		}

		if (project != null) {
			project_name_BP.setText("Project: " + " " + project.getProjectName());
		}

	}

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		task_id.setText(String.valueOf(++currentMaxTaskId));
		folder_id.setText(String.valueOf(++currentMaxFolderId));
		inforClear();
		local_path.setOnMouseClicked(event -> browseLocalPath(event));
		folder_path.setOnMouseClicked(event -> browseFolderPath(event));
	}

	public void addFolderLayout(BackupFolder folder) {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/folder.fxml"));
			content_folder = loader.load();
			Folder folderController = loader.getController();
			folderController.folderData(folder);
			folderController.setDetailsTaskController(this);
			folderController.setDashboardController(dashboardController);
			vbox_folder.getChildren().add(content_folder);
			content_folder.setMaxHeight(Double.MAX_VALUE);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void inforClear() {
		task_id.setText("");
		task_name.clear();
		local_path.clear();
		local_cronTab.clear();
		local_retention.clear();
		remote_path.clear();
		remote_cronTab.clear();
		remote_retention.clear();
	}

}
