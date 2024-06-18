package vn.mekosoft.backup.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
import javafx.util.StringConverter;
import vn.mekosoft.backup.action.BackupActionUtil;
import vn.mekosoft.backup.config.Config;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.impl.CoreScriptServiceImpl;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.TableModel;
import vn.mekosoft.backup.model.Time;
import vn.mekosoft.backup.service.BackupService;
import vn.mekosoft.backup.service.CoreScriptService;

public class Dashboard implements Initializable {
	@FXML
	private AnchorPane addProject_view, addTask_view, backupProject_view, backupTask_view, content_layout, content_view,
			createProject_view, dashboard_view, scheduler_view, tableDashboard_action,dataProtection_view;

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
	private Folder folderController;
	@FXML
	private DetailsTask detailsTaskController;
	@FXML
	private ImageView logo;

	@FXML
	private BarChart<String, Integer> barChartDaily;
	@FXML
	private StackedBarChart<String, Integer> stackBarChartStatus;
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
	private Label totalLocal, detail_barChart;

	@FXML
	private Label totalRemote;

	@FXML
	private TableColumn<TableModel, Long> backupProject_col;

	@FXML
	private TableColumn<TableModel, String> backupTask_col;

	@FXML
	private TableColumn<TableModel, String> folder_col;

	@FXML
	private TableColumn<TableModel, String> remoteStorage_col;

	@FXML
	private TableColumn<TableModel, String> status_col;

	@FXML
	private TableColumn<TableModel, String> localStorage_col;

	@FXML
	public void action_table(MouseEvent event) {
		System.out.print("Click");
		tableDashboard_action.setVisible(true);
		dashboard_view.setVisible(false);
	}

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

	@Override
	public void initialize(URL url, ResourceBundle resources) {

		logo.setVisible(true);
		Folder folder = new Folder();
		setFolderController(folder);
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
//		dataChartStatus();
//		localPie();
		// remotePie();
		tableDashboard();
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

	public void sumary_action(ActionEvent event) {
		if(event.getSource() == button_sumary) {
			dashboard_view.setVisible(true);
			dataProtection_view.setVisible(false);
		}
	}
	public void dataProtection_action(ActionEvent event) {
		
		if(event.getSource() == button_dataProtection) {
			dashboard_view.setVisible(false);
			dataProtection_view.setVisible(true);
		}
	}
	public void applyDateRange_action() {
		startDate = startRange_date.getValue();
		System.out.println("start date: " + startDate);
		endDate = endRange_date.getValue();
		System.out.println("end date: " + endDate);

		if (startDate != null && endDate != null) {
			if (endDate.isBefore(startDate)) {
				return;
			}

			filterDataByDateRange(startDate, endDate);
		}
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

	public boolean isWithinDateRange(String date, LocalDate startDate, LocalDate endDate) {

		try {
			LocalDate logDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
			return (logDate.isEqual(startDate) || logDate.isAfter(startDate))
					&& (logDate.isEqual(endDate) || logDate.isBefore(endDate));
		} catch (DateTimeParseException e) {
			System.err.println("Unable to parse date: " + date);
			return false;
		}
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
		System.out.println("Refreshing every " + intervalInSeconds + " seconds");

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
		}
	}

	public void showAddProjectView() {
		addProject_view.setVisible(true);
	}

	public AnchorPane getContentView() {
		return content_view;
	}

	public void setFolderController(Folder folderController) {
		this.folderController = folderController;
	}

	public void refreshFolder() {
		if (folderController != null) {
			folderController.refresh();
		}
	}

	public Map<LocalDate, Integer> getBackupCountByDate() {
		return getBackupCountByDate();
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
				System.out.println("Invalid log directory: " + logDirPath);
				clearDashboard();
				return;
			}

			File[] logFiles = logDir.listFiles((dir, name) -> name.endsWith(".log"));
			if (logFiles == null || logFiles.length == 0) {
				System.out.println("No log files found.");
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
					System.err.println("Error reading file: " + logFile.getName());
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

//	private void updateBarChart(Map<String, Integer> backupLocalCount, Map<String, Integer> cleanupLocalCount,
//			Map<String, Integer> backupRemoteCount, Map<String, Integer> cleanupRemoteCount) {
//		Platform.runLater(() -> {
//			barChartDaily.getData().clear();
//			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//			XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
//			series1.setName("Backup Local");
//			backupLocalCount.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
//				String formattedDate = LocalDate.parse(entry.getKey(), inputFormatter).format(outputFormatter);
//				series1.getData().add(new XYChart.Data<>(formattedDate, entry.getValue()));
//			});
//
//			XYChart.Series<String, Integer> series2 = new XYChart.Series<>();
//			series2.setName("Cleanup Local");
//			cleanupLocalCount.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
//				String formattedDate = LocalDate.parse(entry.getKey(), inputFormatter).format(outputFormatter);
//				series2.getData().add(new XYChart.Data<>(formattedDate, entry.getValue()));
//			});
//
//			XYChart.Series<String, Integer> series3 = new XYChart.Series<>();
//			series3.setName("Backup Remote");
//			backupRemoteCount.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
//				String formattedDate = LocalDate.parse(entry.getKey(), inputFormatter).format(outputFormatter);
//				series3.getData().add(new XYChart.Data<>(formattedDate, entry.getValue()));
//			});
//
//			XYChart.Series<String, Integer> series4 = new XYChart.Series<>();
//			series4.setName("Cleanup Remote");
//			cleanupRemoteCount.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
//				String formattedDate = LocalDate.parse(entry.getKey(), inputFormatter).format(outputFormatter);
//				series4.getData().add(new XYChart.Data<>(formattedDate, entry.getValue()));
//			});
//
//			barChartDaily.getData().addAll(series1, series2, series3, series4);
//		
//		});
//	}
	private void updateBarChart(Map<String, Integer> backupLocalCount, Map<String, Integer> cleanupLocalCount,
			Map<String, Integer> backupRemoteCount, Map<String, Integer> cleanupRemoteCount) {
		Platform.runLater(() -> {
			barChartDaily.getData().clear();
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
			series1.setName("Backup Local");
			backupLocalCount.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
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
			cleanupLocalCount.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
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
			backupRemoteCount.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
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
			cleanupRemoteCount.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
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

//	private void displayLabelForData(XYChart.Data<String, Integer> data) {
//	    final Text text = new Text();  // Text không hiển thị ngay từ đầu
//	    final Node node = data.getNode();
//
//	    if (node != null) {
//	        // Thêm listener cho parentProperty
//	        node.parentProperty().addListener((obs, oldParent, newParent) -> {
//	            if (newParent != null) {
//	                ((Group) newParent).getChildren().add(text);
//	                updateTextPosition(node, text);  // Đặt vị trí của nhãn ngay từ đầu
//
//	                // Thêm listener khi node được update lại
//	                node.boundsInParentProperty().addListener((obs2, oldBounds, bounds) -> {
//	                    if (text.isVisible()) {
//	                        updateTextPosition(node, text);  // Đặt lại vị trí khi kích thước cột thay đổi
//	                    }
//	                });
//
//	                node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
//	                    text.setText(data.getYValue().toString());  // Hiển thị số
//	                    updateTextPosition(node, text);
//	                    text.setVisible(true);  // Hiện text khi mouse entered
//	                });
//
//	                node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
//	                    text.setVisible(false);  // Ẩn text khi mouse exited
//	                });
//	            }
//	        });
//	    }
//	}
//
//	private void updateTextPosition(Node node, Text text) {
//	    // Chuyển đổi tọa độ của text để hiển thị bên trong cột của biểu đồ
//	    node.boundsInParentProperty().addListener((ChangeListener<Bounds>) (ov, oldBounds, bounds) -> {
//	        if (text.isVisible()) {
//	            double centerX = Math.round(bounds.getMinX() + bounds.getWidth() / 2);
//	            double yPosition = Math.round(bounds.getMinY()   + bounds.getHeight() / 2);  
//	            text.setLayoutX(centerX - text.prefWidth(-1) / 2);
//	            text.setLayoutY(yPosition - text.prefHeight(-1));
//	        }
//	    });
//	}
	private void displayLabelForData(Object data) {
	    if (data instanceof XYChart.Data) {
	        XYChart.Data<String, Integer> chartData = (XYChart.Data<String, Integer>) data;
	        final Node node = chartData.getNode();

	        if (node != null) {
	            node.parentProperty().addListener((obs, oldParent, newParent) -> {
	                if (newParent != null) {
	                    updateLabel(chartData);
	                //    updateLabelPosition(chartData); // Đặt vị trí của label ngay từ đầu

	                    node.boundsInParentProperty().addListener((ChangeListener<Bounds>) (ov, oldBounds, bounds) -> {
	                        if (detail_barChart.isVisible()) {
	                           // updateLabelPosition(chartData); // Đặt lại vị trí khi kích thước cột thay đổi
	                        }
	                    });

	                    node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
	                        String info = formatInformation(chartData); // Tạo thông tin định dạng
	                        detail_barChart.setText(info); // Cập nhật nội dung của label
	                        detail_barChart.setVisible(true); // Hiện label khi mouse entered
	                    });

	                    node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
	                        detail_barChart.setVisible(false); // Ẩn label khi mouse exited
	                    });
	                }
	            });
	        }
	    }
	}

	private void updateLabel(XYChart.Data<String, Integer> chartData) {
	    String info = formatInformation(chartData); // Format the information for label
	    detail_barChart.setText(info); // Set formatted text to the label
	}

//	private void updateLabelPosition(XYChart.Data<String, Integer> chartData) {
//	    Node node = chartData.getNode();
//	    if (node != null) {
//	        node.boundsInParentProperty().addListener((ChangeListener<Bounds>) (ov, oldBounds, bounds) -> {
//	            if (detail_barChart.isVisible()) {
//	                double centerX = Math.round(bounds.getMinX() + bounds.getWidth() / 2);
//	                double yPosition = Math.round(bounds.getMinY() + bounds.getHeight() / 2);
//	                detail_barChart.setLayoutX(centerX - detail_barChart.prefWidth(-1) / 2);
//	                detail_barChart.setLayoutY(yPosition - detail_barChart.prefHeight(-1));
//	            }
//	        });
//	    }
//	}

	private String formatInformation(XYChart.Data<String, Integer> chartData) {
	    return String.format("Information: %s Backup... %d", chartData.getXValue(), chartData.getYValue());
	}





	private void clearDashboard() {
		Platform.runLater(() -> barChartDaily.getData().clear());
	}

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
			tableDashboard_action.setVisible(false);

		} else if (event.getSource() == button_backupProject) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(true);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(false);
			config_view.setVisible(false);
			createProject_view.setVisible(false);
			tableDashboard_action.setVisible(false);

		} else if (event.getSource() == button_backupTask) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(true);
			scheduler_view.setVisible(false);
			config_view.setVisible(false);
			createProject_view.setVisible(false);
			tableDashboard_action.setVisible(false);

		} else if (event.getSource() == button_scheduler) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(true);
			config_view.setVisible(false);
			createProject_view.setVisible(false);
			tableDashboard_action.setVisible(false);

		} else if (event.getSource() == button_addProject) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(false);
			config_view.setVisible(false);
			tableDashboard_action.setVisible(false);

			createProject_view.setVisible(true);
		} else if (event.getSource() == button_config) {
			dashboard_view.setVisible(false);
			backupProject_view.setVisible(false);
			backupTask_view.setVisible(false);
			scheduler_view.setVisible(false);
			tableDashboard_action.setVisible(false);

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
				System.out.print(isBackupStarted);
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
			{
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
		}
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

	public void dataChartStatus() {
		XYChart.Series<String, Integer> series2 = new XYChart.Series<>();
		series2.setName("Failed");
		series2.getData().add(new XYChart.Data<>("2024/05/29", 7));
		series2.getData().add(new XYChart.Data<>("2024/05/30", 4));
		series2.getData().add(new XYChart.Data<>("2024/05/31", 0));
		series2.getData().add(new XYChart.Data<>("2024/06/01", 0));
		series2.getData().add(new XYChart.Data<>("2024/06/02", 5));
		series2.getData().add(new XYChart.Data<>("2024/06/03", 1));
		// series2.getData().add(new XYChart.Data<>("2024/06/07", 5.0));

		stackBarChartStatus.getData().add(series2);

		XYChart.Series<String, Integer> series3 = new XYChart.Series<>();
		series3.setName("Success");
		series3.getData().add(new XYChart.Data<>("2024/05/29", 5));
		series3.getData().add(new XYChart.Data<>("2024/05/30", 1));
		series3.getData().add(new XYChart.Data<>("2024/05/31", 3));
		series3.getData().add(new XYChart.Data<>("2024/06/01", 12));
		series3.getData().add(new XYChart.Data<>("2024/06/02", 0));
		series3.getData().add(new XYChart.Data<>("2024/06/03", 4));
		// series3.getData().add(new XYChart.Data<>("2024/06/07", 2.0));

		stackBarChartStatus.getData().add(series3);
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
		String numericValue = str.replaceAll("[^\\d.]", "");
		double value = Double.parseDouble(numericValue);
		if (str.contains("T")) {
			return value * 1024 * 1024;
		} else if (str.contains("G")) {
			return value * 1024;
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

	public void tableDashboard() {
		BackupService backupService = new BackupServiceImpl();
		ObservableList<TableModel> data = FXCollections.observableArrayList();

		List<BackupProject> backupProjects = backupService.loadData();
		for (BackupProject project : backupProjects) {
			long projectId = project.getProjectId();
			for (BackupTask task : project.getBackupTasks()) {
				String backupTaskName = task.getName();
				String localPath = task.getLocalPath();
				String remotePath = task.getRemotePath();
				String backupTaskStatus = task.getBackupTaskStatusEnum().getDescription();
				for (BackupFolder folder : task.getBackupFolders()) {
					String folderPath = folder.getFolderPath();

					TableModel row = new TableModel(projectId, backupTaskName, folderPath, localPath, remotePath,
							backupTaskStatus);
					data.add(row);
				}
			}
		}

		backupProject_col.setCellValueFactory(new PropertyValueFactory<>("projectId"));
		backupTask_col.setCellValueFactory(new PropertyValueFactory<>("backupTaskName"));
		folder_col.setCellValueFactory(new PropertyValueFactory<>("folderPath"));
		localStorage_col.setCellValueFactory(new PropertyValueFactory<>("localPath"));
		remoteStorage_col.setCellValueFactory(new PropertyValueFactory<>("remotePath"));
		status_col.setCellValueFactory(new PropertyValueFactory<>("backupTaskStatus"));

		table_dashboard.setItems(data);
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
//		System.out.println("Cấu hình đã được lưu.");
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

}
