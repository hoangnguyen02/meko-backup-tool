package vn.mekosoft.backup.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import vn.mekosoft.backup.config.ConfigReader;

public class BackupTaskStatusChart implements Initializable {

    @FXML
    private BarChart<String, Integer> stackBarChartStatus;

    @FXML
    private CategoryAxis xStatus;

    @FXML
    private NumberAxis yStatus;
    private LocalDate startDate;
    private LocalDate endDate;

    @FXML
    private AnchorPane chartStatus;
    private final Map<String, Integer> successLocalCount = new HashMap<>();
    private final Map<String, Integer> failLocalCount = new HashMap<>();
    private final Map<String, Integer> successRemoteCount = new HashMap<>();
    private final Map<String, Integer> failRemoteCount = new HashMap<>();
    private Dashboard dashboardController;

    public void setDashboardController(Dashboard dashboardController) {
        this.dashboardController = dashboardController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stackBarChartStatus.getStyleClass().add("chart2");
        stackBarChartStatus.setCategoryGap(50);
        stackBarChartStatus.setBarGap(1);
        stackBarChartStatus.setMaxSize(1000, 500);
        updateChart();

    }
//    public void hideBackupTaskStatus() {
//    	
//    	chartStatus.setVisible(false);
//    	   if (dashboardController != null) {
//    	        dashboardController.loadData();
//    	    } else {
//    	        System.err.println("dashboardController is null");
//    	    }
//    }
    public void dataChartStatus() {
        parseLogs();

        Platform.runLater(() -> {
            stackBarChartStatus.getData().clear();
            xStatus.setLabel("Date");
            yStatus.setLabel("Count");
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
                    	displayLabelForData(data,  seriesSuccessLocal);
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
                    	displayLabelForData(data,   seriesFailLocal);
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
                    	displayLabelForData(data,  seriesSuccessRemote);
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
                    	displayLabelForData(data, seriesFailRemote);
                    }
                });
            });

            stackBarChartStatus.getData().addAll(seriesSuccessLocal, seriesFailLocal, seriesSuccessRemote, seriesFailRemote);
        });
    }
	private void displayLabelForData(XYChart.Data<String, Integer> data, XYChart.Series<String, Integer> series) {
	    Node node = data.getNode();
	    if (node != null) {
	        StackPane stackPane = (StackPane) node;
	        Label label = new Label(String.valueOf(data.getYValue()));
	        label.setStyle("-fx-font: 10 arial;");
	        stackPane.getChildren().add(label);
	        StackPane.setAlignment(label, Pos.BASELINE_CENTER);
	    }
	}
    private Map<LocalDate, Integer> filterDataByDateRange(Map<String, Integer> logData) {
        Map<LocalDate, Integer> filteredData = new HashMap<>();

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        for (Map.Entry<String, Integer> entry : logData.entrySet()) {
            LocalDate date = LocalDate.parse(entry.getKey(), inputFormatter);
            if (startDate == null || endDate == null || isWithinDateRange(date, startDate, endDate)) {
                filteredData.put(date, entry.getValue());
            }
        }

        return filteredData;
    }

    private boolean isWithinDateRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return (date.isEqual(startDate) || date.isAfter(startDate))
                && (date.isEqual(endDate) || date.isBefore(endDate));
    }
    public void filterDataByDateRange(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
       // System.out.println("startDate status: " + startDate + ", endDate: " + endDate);

        dataChartStatus(); // Update the chart with filtered data
    }


    private void parseLogs() {
        successLocalCount.clear();
        failLocalCount.clear();
        successRemoteCount.clear();
        failRemoteCount.clear();

        ConfigReader config = new ConfigReader();
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
//        if(!hasLogFiles(logFiles)) {
//        	hideBackupTaskStatus();
//        	return;
//        }
        LocalDate currentDate = LocalDate.now();
        if (startDate == null || endDate == null) {
            startDate = currentDate.minusDays(6);
            endDate = currentDate;
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

    private void processLogLineStatus(String line, Map<String, Integer> successLocalCount, Map<String, Integer> failLocalCount,
            Map<String, Integer> successRemoteCount, Map<String, Integer> failRemoteCount) {
        if (line.contains("[SUCCESSFUL] [BACKUPLOCAL]")) {
            String date = extractDateFromLog(line);
            successLocalCount.merge(date, 1, Integer::sum);
        } else if (line.contains("[FAILED] [BACKUPLOCAL]")) {
            String date = extractDateFromLog(line);
            failLocalCount.merge(date, 1, Integer::sum);
        } else if (line.contains("[SUCCESS] [BACKUPREMOTE]")) {
            String date = extractDateFromLog(line);
            successRemoteCount.merge(date, 1, Integer::sum);
        } else if (line.contains("[FAILED] [BACKUPREMOTE]")) {
            String date = extractDateFromLog(line);
            failRemoteCount.merge(date, 1, Integer::sum);
        }
    }

    private String extractDateFromLog(String line) {
        return line.substring(0, 10); 
    }

	public void updateChart() {
		dataChartStatus();
		
	}



}
