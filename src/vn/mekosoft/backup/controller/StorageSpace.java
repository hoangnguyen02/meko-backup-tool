package vn.mekosoft.backup.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

public class StorageSpace implements Initializable {

	@FXML
	private PieChart localPieChart;

	@FXML
	private PieChart remotePieChart;

	@FXML
	private Label totalLocal;

	@FXML
	private Label totalRemote;
	private Dashboard dashboardController;

	public void setDashboardController(Dashboard dashboardController) {
		this.dashboardController = dashboardController;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		localPie();
		remotePie();
		localPieChart.getStylesheets().add(getClass().getResource("/vn/mekosoft/backup/application/application.css").toExternalForm());
		remotePieChart.getStylesheets().add(getClass().getResource("/vn/mekosoft/backup/application/application.css").toExternalForm());
	}

//	private String getRemoteStorageInfo() throws IOException, InterruptedException {
//	String totalCommand = "rclone about onedrive: | awk 'NR==1 {print $2}'";
//	String usedCommand = "rclone about onedrive: | awk 'NR==2 {print $2}'";
//	String availableCommand = "rclone about onedrive: | awk 'NR==3 {print $2}'";
//	String trashedCommand = "rclone about onedrive: | awk 'NR==4 {print $2}'";
//
//	String total = executeCommand(totalCommand);
//	String used = executeCommand(usedCommand);
//	String available = executeCommand(availableCommand);
//	String trashed = executeCommand(trashedCommand);
//
//	return total + " " + used + " " + available + " " + trashed;
//}

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
}
