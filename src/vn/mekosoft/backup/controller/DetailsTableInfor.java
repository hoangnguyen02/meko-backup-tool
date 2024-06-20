package vn.mekosoft.backup.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import vn.mekosoft.backup.config.Config;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.LogEntry;
public class DetailsTableInfor implements Initializable {
    @FXML
    private TextField local_cronTab;

    @FXML
    private TextField local_path;

    @FXML
    private TextField local_retention;

    @FXML
    private TextField project_name;

    @FXML
    private TextField remote_cronTab;

    @FXML
    private TextField remote_path;

    @FXML
    private TextField remote_retention;

    @FXML
    private TextField task_name;

    @FXML
    private AnchorPane title;

    @FXML
    private VBox vbox_folderList;

    @FXML
    private TableView<LogEntry> tableDetails;

    @FXML
    private TableColumn<LogEntry, String> dateTime_col;

    @FXML
    private TableColumn<LogEntry, String> action_col;

    @FXML
    private TableColumn<LogEntry, String> result_col;

    private BackupTask task;
    private BackupProject project;

    private Dashboard dashboardController;
    private ObservableList<LogEntry> logEntries = FXCollections.observableArrayList();

    public void setDashboardController(Dashboard dashboardController) {
        this.dashboardController = dashboardController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateTime_col.setCellValueFactory(cellData -> cellData.getValue().dateTimeProperty());
        action_col.setCellValueFactory(cellData -> cellData.getValue().actionProperty());
        result_col.setCellValueFactory(cellData -> cellData.getValue().resultProperty());
        tableDetails.setItems(logEntries);
        result_col.setCellFactory(new Callback<TableColumn<LogEntry, String>, TableCell<LogEntry, String>>() {
            @Override
            public TableCell<LogEntry, String> call(TableColumn<LogEntry, String> param) {
                return new TableCell<LogEntry, String>() {
                    @Override
                    protected void updateItem(String result, boolean empty) {
                        super.updateItem(result, empty);
                        if (empty || result == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            if (result.equals("Completed")) {
                                setStyle("-fx-text-fill: green;");
                                setText(result);
                            } else if (result.equals("Failed")) {
                                setStyle("-fx-text-fill: red;");
                                setText(result);
                            } else {
                                setText(result);
                                setStyle("");
                            }
                        }
                    }
                };
            }
        });
    }
    

    public void taskDetails(BackupTask task, BackupProject project) {
        if (task == null || project == null) {
            return;
        }

        this.task = task;
        this.project = project;

        task_name.setText(task.getName());
        local_cronTab.setText(task.getLocalSchedular());
        remote_cronTab.setText(task.getRemoteSchedular());
        local_path.setText(task.getLocalPath());
        remote_path.setText(task.getRemotePath());
        local_retention.setText(String.valueOf(task.getLocalRetention()));
        remote_retention.setText(String.valueOf(task.getRemoteRetention()));
        project_name.setText(project.getProjectName());

        // Process folder list
        if (task.getBackupFolders() != null && !task.getBackupFolders().isEmpty()) {
            for (BackupFolder folder : task.getBackupFolders()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/folderTable.fxml"));
                    Parent folderRoot = loader.load();
                    FolderTable folderTableController = loader.getController();
                    folderTableController.setFolderPath(folder.getFolderPath());

                    vbox_folderList.getChildren().add(folderRoot);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        displayTaskLogs();
    }

    private void displayTaskLogs() {
        Config config = new Config();
        String logFilePath = config.getConfigLog(task.getProjectId(), task.getBackupTaskId());

        if (logFilePath == null || logFilePath.isEmpty()) {
            Platform.runLater(() -> {
                logEntries.clear();
                logEntries.add(new LogEntry("Log file not available.", "", ""));
            });
            return;
        }

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("[START] [BACKUPLOCAL]") ||
                        line.contains("[START] [BACKUPREMOTE]") ||
                        line.contains("[START] [CLEANUPLOCAL]") ||
                        line.contains("[START] [CLEANUPREMOTE]")) {
                        continue;
                    }

                    if (line.contains("[END] [BACKUPLOCAL]") ||
                        line.contains("[END] [BACKUPREMOTE]") ||
                        line.contains("[END] [CLEANUPLOCAL]") ||
                        line.contains("[END] [CLEANUPREMOTE]")) {

                        String[] parts = line.split(" ");
                        String dateTime = parts[0] + " " + parts[1];
                        String action = line.contains("[BACKUPLOCAL]") ? "Backup Local" :
                                        line.contains("[BACKUPREMOTE]") ? "Backup Remote" :
                                        line.contains("[CLEANUPLOCAL]") ? "Cleanup Local" : "Cleanup Remote";
                        String result = "Completed";

                        String nextLine = reader.readLine();
                        if (nextLine != null && nextLine.contains("[FAILED]")) {
                            result = "Failed";
                        }

                        LogEntry logEntry = new LogEntry(dateTime, action, result);
                        Platform.runLater(() -> logEntries.add(logEntry));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
