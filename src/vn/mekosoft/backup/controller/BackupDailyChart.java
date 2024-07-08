package vn.mekosoft.backup.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import vn.mekosoft.backup.config.ConfigReader;

public class BackupDailyChart implements Initializable {
	@FXML
	private BarChart<String, Integer> barChartDaily;
	@FXML
	private CategoryAxis category_axis;

	@FXML
	private NumberAxis number_axis;
	private final Map<String, Integer> backupLocalCount = new TreeMap<>();
	private final Map<String, Integer> cleanupLocalCount = new TreeMap<>();
	private final Map<String, Integer> backupRemoteCount = new TreeMap<>();
	private final Map<String, Integer> cleanupRemoteCount = new TreeMap<>();
	private final Object lock = new Object();
	private File logDir;
	private LocalDate startDate;
	private LocalDate endDate;
	private Dashboard dashboardController;

	public void setDashboardController(Dashboard dashboardController) {
		this.dashboardController = dashboardController;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// dataChartDaily();
		barChartDaily.getStyleClass().add("chart1");
		barChartDaily.setCategoryGap(50);
		barChartDaily.setBarGap(1);
		barChartDaily.setMaxSize(1000, 500);
		updateChart();

	}

	public void hideBackupDailyChart() {
		barChartDaily.setVisible(false);
	}

	public Map<LocalDate, Integer> getBackupCountByDate() {
		return getBackupCountByDate();
	}

	public void filterDataByDateRange(LocalDate startDate, LocalDate endDate) {
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

	private void updateBarChart(Map<String, Integer> backupLocalCount, Map<String, Integer> cleanupLocalCount,
	        Map<String, Integer> backupRemoteCount, Map<String, Integer> cleanupRemoteCount) {
	    Platform.runLater(() -> {
	        barChartDaily.getData().clear();
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	        XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
	        series1.setName("Backup Local");
	        backupLocalCount.entrySet().stream()
	                .filter(entry -> isValidDate(entry.getKey(), inputFormatter))
	                .sorted(Map.Entry.comparingByKey())
	                .forEach(entry -> {
	                    String formattedDate = LocalDate.parse(entry.getKey(), inputFormatter).format(outputFormatter);
	                    XYChart.Data<String, Integer> data = new XYChart.Data<>(formattedDate, entry.getValue());
	                    series1.getData().add(data);
	                    data.nodeProperty().addListener((obs, oldNode, newNode) -> {
	                        if (newNode != null) {
	                           displayLabelForData(data, series1);
	                        }
	                    });
	                });

	        XYChart.Series<String, Integer> series2 = new XYChart.Series<>();
	        series2.setName("Cleanup Local");
	        cleanupLocalCount.entrySet().stream()
	                .filter(entry -> isValidDate(entry.getKey(), inputFormatter))
	                .sorted(Map.Entry.comparingByKey())
	                .forEach(entry -> {
	                    String formattedDate = LocalDate.parse(entry.getKey(), inputFormatter).format(outputFormatter);
	                    XYChart.Data<String, Integer> data = new XYChart.Data<>(formattedDate, entry.getValue());
	                    series2.getData().add(data);
	                    data.nodeProperty().addListener((obs, oldNode, newNode) -> {
	                        if (newNode != null) {
	                           displayLabelForData(data, series2);
	                        }
	                    });
	                });

	        XYChart.Series<String, Integer> series3 = new XYChart.Series<>();
	        series3.setName("Backup Remote");
	        backupRemoteCount.entrySet().stream()
	                .filter(entry -> isValidDate(entry.getKey(), inputFormatter))
	                .sorted(Map.Entry.comparingByKey())
	                .forEach(entry -> {
	                    String formattedDate = LocalDate.parse(entry.getKey(), inputFormatter).format(outputFormatter);
	                    XYChart.Data<String, Integer> data = new XYChart.Data<>(formattedDate, entry.getValue());
	                    series3.getData().add(data);
	                    data.nodeProperty().addListener((obs, oldNode, newNode) -> {
	                    //	 newNode.setStyle("-fx-bar-fill: #dcfce7;"); 
	                        if (newNode != null) {
	                        	
	                         displayLabelForData(data, series3);
	                        }
	                    });
	                });

	        XYChart.Series<String, Integer> series4 = new XYChart.Series<>();
	        series4.setName("Cleanup Remote");
	        cleanupRemoteCount.entrySet().stream()
	                .filter(entry -> isValidDate(entry.getKey(), inputFormatter))
	                .sorted(Map.Entry.comparingByKey())
	                .forEach(entry -> {
	                    String formattedDate = LocalDate.parse(entry.getKey(), inputFormatter).format(outputFormatter);
	                    XYChart.Data<String, Integer> data = new XYChart.Data<>(formattedDate, entry.getValue());
	                    series4.getData().add(data);
	                    data.nodeProperty().addListener((obs, oldNode, newNode) -> {
	                        if (newNode != null) {
	                          displayLabelForData(data, series4);
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
	
	
	
//	private void updateBarChart(Map<String, Integer> backupLocalCount, Map<String, Integer> cleanupLocalCount,
//	        Map<String, Integer> backupRemoteCount, Map<String, Integer> cleanupRemoteCount) {
//	    Platform.runLater(() -> {
//	        barChartDaily.getData().clear();
//	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//	        addSeriesToChart("Backup Local", backupLocalCount, inputFormatter, outputFormatter);
//	        addSeriesToChart("Cleanup Local", cleanupLocalCount, inputFormatter, outputFormatter);
//	        addSeriesToChart("Backup Remote", backupRemoteCount, inputFormatter, outputFormatter);
//	        addSeriesToChart("Cleanup Remote", cleanupRemoteCount, inputFormatter, outputFormatter);
//	    });
//	}

	private String formatInformation(XYChart.Data<String, Integer> chartData) {
		return String.format("Information: %s Backup... %d", chartData.getXValue(), chartData.getYValue());
	}

	public void dataChartDaily() {
		synchronized (lock) {
			ConfigReader config = new ConfigReader();
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

			LocalDate currentDate = LocalDate.now();
			if (startDate == null || endDate == null) {
				startDate = currentDate.minusDays(6);
				endDate = currentDate;
			}

			backupLocalCount.clear();
			cleanupLocalCount.clear();
			backupRemoteCount.clear();
			cleanupRemoteCount.clear();

			boolean hasData = false;
			for (File logFile : logFiles) {
				// Check if logFile exists before processing
				if (!logFile.exists()) {
					continue;
				}

				try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
					String line;
					while ((line = reader.readLine()) != null) {
						hasData |= processLogLine(line, backupLocalCount, cleanupLocalCount, backupRemoteCount,
								cleanupRemoteCount);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (!hasData) {
				clearDashboard();
				return;
			}

			if (startDate != null && endDate != null) {
				filterDataByDateRange(startDate, endDate);
			} else {
				updateBarChart();
			}
		}
	}

	private void clearDashboard() {
		Platform.runLater(() -> barChartDaily.getData().clear());
	}

	private boolean processLogLine(String line, Map<String, Integer> backupLocalCount,
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
					return true;
				case "CLEANUPLOCAL":
					cleanupLocalCount.merge(date, count, Integer::sum);
					return true;
				case "BACKUPREMOTE":
					backupRemoteCount.merge(date, count, Integer::sum);
					return true;
				case "CLEANUPREMOTE":
					cleanupRemoteCount.merge(date, count, Integer::sum);
					return true;
				}
			}
		}
		return false;
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

  
	public void updateChart() {
		dataChartDaily();
	}
}