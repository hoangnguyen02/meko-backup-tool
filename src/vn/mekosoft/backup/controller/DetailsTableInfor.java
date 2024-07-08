package vn.mekosoft.backup.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import vn.mekosoft.backup.config.ConfigReader;
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
    @FXML
    private Label countBackup;
    
    private BackupTask task;
    private BackupProject project;

    @FXML
    private TextField backupLocal;

    @FXML
    private TextField backupRemote;

    @FXML
    private TextField cleanupLocal;

    @FXML
    private TextField cleanupRemote;

    @FXML
    private TextField backupRemote_failed;

    @FXML
    private TextField backupRemote_success;
    @FXML
    private TextField backupLocal_failed;

    @FXML
    private TextField backupLocal_success;
    @FXML
    private TextField cleanupLocal_failed;

    @FXML
    private TextField cleanupLocal_success;
    @FXML
    private TextField cleanupRemote_failed;

    @FXML
    private TextField cleanupRemote_success;
    private Dashboard dashboardController;
    private ObservableList<LogEntry> logEntries = FXCollections.observableArrayList();

    public void setDashboardController(Dashboard dashboardController) {
        this.dashboardController = dashboardController;
    }
    private TableDashboard tableDashboardController;

	private LocalDate startDate;

	private LocalDate endDate;

    public void setTableDashboardController(TableDashboard tableDashboard) {
        this.tableDashboardController = tableDashboard;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	table();
    }
    
    public void table() {
    	  dateTime_col.setCellValueFactory(cellData -> cellData.getValue().dateTimeProperty());
          action_col.setCellValueFactory(cellData -> cellData.getValue().actionProperty());
          result_col.setCellValueFactory(cellData -> cellData.getValue().resultProperty());
          tableDetails.setItems(logEntries);
          
          result_col.setCellValueFactory(cellData -> cellData.getValue().resultProperty());
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
                                  setStyle("-fx-text-fill: #3cd856;");
                                  setText(result);
                              } else if (result.equals("Failed")) {
                                  setStyle("-fx-text-fill: #fa557a;");
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
    public void filterDataByDateRange(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

        // Filter data from logEntries based on the startDate and endDate
        ObservableList<LogEntry> filteredList = FXCollections.observableArrayList();
        for (LogEntry entry : logEntries) {
            LocalDate entryDate = parseDateStringToLocalDate(entry.getDateTime());
            if (entryDate != null && isWithinDateRange(entryDate, startDate, endDate)) {
                filteredList.add(entry);
            }
        }

        // Set filtered data back to tableDetails
        tableDetails.setItems(filteredList);
        updateStatistics(filteredList);
    }
    private void updateStatistics(ObservableList<LogEntry> filteredList) {
        int[] backupLocalCount = {0};
        int[] backupRemoteCount = {0};
        int[] cleanupLocalCount = {0};
        int[] cleanupRemoteCount = {0};

        int[] backupLocalSuccessCount = {0};
        int[] backupRemoteSuccessCount = {0};
        int[] cleanupLocalSuccessCount = {0};
        int[] cleanupRemoteSuccessCount = {0};

        int[] backupLocalFailedCount = {0};
        int[] backupRemoteFailedCount = {0};
        int[] cleanupLocalFailedCount = {0};
        int[] cleanupRemoteFailedCount = {0};

        for (LogEntry entry : filteredList) {
            String action = entry.getAction();
            String result = entry.getResult();

            switch (action) {
                case "Backup Local":
                    backupLocalCount[0]++;
                    if ("Completed".equals(result)) {
                        backupLocalSuccessCount[0]++;
                    } else if ("Failed".equals(result)) {
                        backupLocalFailedCount[0]++;
                    }
                    break;
                case "Backup Remote":
                    backupRemoteCount[0]++;
                    if ("Completed".equals(result)) {
                        backupRemoteSuccessCount[0]++;
                    } else if ("Failed".equals(result)) {
                        backupRemoteFailedCount[0]++;
                    }
                    break;
                case "Cleanup Local":
                    cleanupLocalCount[0]++;
                    if ("Completed".equals(result)) {
                        cleanupLocalSuccessCount[0]++;
                    } else if ("Failed".equals(result)) {
                        cleanupLocalFailedCount[0]++;
                    }
                    break;
                case "Cleanup Remote":
                    cleanupRemoteCount[0]++;
                    if ("Completed".equals(result)) {
                        cleanupRemoteSuccessCount[0]++;
                    } else if ("Failed".equals(result)) {
                        cleanupRemoteFailedCount[0]++;
                    }
                    break;
            }
        }

        Platform.runLater(() -> {
            backupLocal.setText(backupLocalCount[0] + " times");
            backupRemote.setText(backupRemoteCount[0] + " times");
            cleanupLocal.setText(cleanupLocalCount[0] + " times");
            cleanupRemote.setText(cleanupRemoteCount[0] + " times");

            backupLocal_success.setText(backupLocalSuccessCount[0] + " times");
            backupRemote_success.setText(backupRemoteSuccessCount[0] + " times");
            cleanupLocal_success.setText(cleanupLocalSuccessCount[0] + " times");
            cleanupRemote_success.setText(cleanupRemoteSuccessCount[0] + " times");

            backupLocal_failed.setText(backupLocalFailedCount[0] + " times");
            backupRemote_failed.setText(backupRemoteFailedCount[0] + " times");
            cleanupLocal_failed.setText(cleanupLocalFailedCount[0] + " times");
            cleanupRemote_failed.setText(cleanupRemoteFailedCount[0] + " times");

            int totalBackupCount = backupLocalCount[0] + backupRemoteCount[0];
            countBackup.setText("Count: " + totalBackupCount);
        });
    }
    private boolean isWithinDateRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return (date.isEqual(startDate) || date.isAfter(startDate))
                && (date.isEqual(endDate) || date.isBefore(endDate));
    }

    private LocalDate parseDateStringToLocalDate(String dateTimeString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
            return dateTime.toLocalDate();
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
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
        ConfigReader config = new ConfigReader();
        String logFilePath = config.getConfigLog(task.getProjectId(), task.getBackupTaskId());

        if (logFilePath == null || logFilePath.isEmpty()) {
            Platform.runLater(() -> {
                logEntries.clear();
                logEntries.add(new LogEntry("Log file not available.", "", ""));
            });
            return;
        }

        new Thread(() -> {
            int[] backupLocalCount = {0};
            int[] backupRemoteCount = {0};
            int[] cleanupLocalCount = {0};
            int[] cleanupRemoteCount = {0};

            int[] backupLocalSuccessCount = {0};
            int[] backupRemoteSuccessCount = {0};
            int[] cleanupLocalSuccessCount = {0};
            int[] cleanupRemoteSuccessCount = {0};

            int[] backupLocalFailedCount = {0};
            int[] backupRemoteFailedCount = {0};
            int[] cleanupLocalFailedCount = {0};
            int[] cleanupRemoteFailedCount = {0};

            try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {

                    if (line.contains("[START] [BACKUPLOCAL]")) {
                        backupLocalCount[0]++;
                    } else if (line.contains("[START] [BACKUPREMOTE]")) {
                        backupRemoteCount[0]++;
                    } else if (line.contains("[START] [CLEANUPLOCAL]")) {
                        cleanupLocalCount[0]++;
                    } else if (line.contains("[START] [CLEANUPREMOTE]")) {
                        cleanupRemoteCount[0]++;
                    } else if (line.contains("[END] [BACKUPLOCAL]")) {
                        String result = "Completed";
                        String nextLine = reader.readLine();

                        if (nextLine != null && nextLine.contains("[FAILED]")) {
                            result = "Failed";
                            backupLocalFailedCount[0]++;
                        } else {
                            backupLocalSuccessCount[0]++;
                        }
                        LogEntry logEntry = new LogEntry(line.split(" ")[0] + " " + line.split(" ")[1], "Backup Local", result);
                        Platform.runLater(() -> logEntries.add(logEntry));
                    } else if (line.contains("[END] [BACKUPREMOTE]")) {
                        String result = "Completed";
                        String nextLine = reader.readLine();

                        if (nextLine != null && nextLine.contains("[FAILED]")) {
                            result = "Failed";
                            backupRemoteFailedCount[0]++;
                        } else {
                            backupRemoteSuccessCount[0]++;
                        }
                        LogEntry logEntry = new LogEntry(line.split(" ")[0] + " " + line.split(" ")[1], "Backup Remote", result);
                        Platform.runLater(() -> logEntries.add(logEntry));
                    } else if (line.contains("[END] [CLEANUPLOCAL]")) {
                        String result = "Completed";
                        String nextLine = reader.readLine();

                        if (nextLine != null && nextLine.contains("[FAILED]")) {
                            result = "Failed";
                            cleanupLocalFailedCount[0]++;
                        } else {
                            cleanupLocalSuccessCount[0]++;
                        }
                        LogEntry logEntry = new LogEntry(line.split(" ")[0] + " " + line.split(" ")[1], "Cleanup Local", result);
                        Platform.runLater(() -> logEntries.add(logEntry));
                    } else if (line.contains("[END] [CLEANUPREMOTE]")) {
                        String result = "Completed";
                        String nextLine = reader.readLine();
 
                        if (nextLine != null && nextLine.contains("[FAILED]")) {
                            result = "Failed";
                            cleanupRemoteFailedCount[0]++;
                        } else {
                            cleanupRemoteSuccessCount[0]++;
                        }
                        LogEntry logEntry = new LogEntry(line.split(" ")[0] + " " + line.split(" ")[1], "Cleanup Remote", result);
                        Platform.runLater(() -> logEntries.add(logEntry));
                    }
                }
                Platform.runLater(() -> {
                    backupLocal.setText(String.valueOf("Total: " + backupLocalCount[0]));
                    backupRemote.setText(String.valueOf("Total: " +backupRemoteCount[0]));
                    cleanupLocal.setText(String.valueOf("Total: " +cleanupLocalCount[0]));
                    cleanupRemote.setText(String.valueOf("Total: " +cleanupRemoteCount[0]));

                    backupLocal_success.setText(String.valueOf("Success: " + backupLocalSuccessCount[0] ));
                    backupRemote_success.setText(String.valueOf("Success: " +backupRemoteSuccessCount[0]));
                    cleanupLocal_success.setText(String.valueOf("Success: " +cleanupLocalSuccessCount[0] ));
                    cleanupRemote_success.setText(String.valueOf("Success: " +cleanupRemoteSuccessCount[0]));

                    backupLocal_failed.setText(String.valueOf("Failed: " + backupLocalFailedCount[0]));
                    backupRemote_failed.setText(String.valueOf("Failed: " + backupRemoteFailedCount[0]));
                    cleanupLocal_failed.setText(String.valueOf("Failed: " + cleanupLocalFailedCount[0]));
                    cleanupRemote_failed.setText(String.valueOf("Failed: " +cleanupRemoteFailedCount[0]));

                    int totalBackupCount = backupLocalCount[0] + backupRemoteCount[0];
                    countBackup.setText("Count Backup: " + totalBackupCount + "times");
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

	public void setDashboardController(TableDashboard tableDashboard) {
		// TODO Auto-generated method stub
		
	}

}
