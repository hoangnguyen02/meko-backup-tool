package vn.mekosoft.backup.action;

import java.util.Optional;
import java.util.StringTokenizer;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AlertMaker {
	public static void successfulAlert(String title, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setContentText(content);
		addIconToAlert(alert);
		alert.showAndWait();
	}

	public static void errorAlert(String title, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setContentText(content);
		addIconToAlert(alert);
		alert.showAndWait();
	}

	public static Optional<ButtonType> showConfirmAlert(String title, String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		addIconToAlert(alert);
		return alert.showAndWait();
	}

	public static Optional<ButtonType> showConfirmDelete(String title, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		addIconToAlert(alert);
//		ButtonType button = new ButtonType("Delete");
//		alert.getButtonTypes().setAll(button);
//		

		return alert.showAndWait();

	}

	private static final String ICON_PATH = "/vn/mekosoft/backup/view/img/company_logo.png"; 

	private static void addIconToAlert(Alert alert) {
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(ICON_PATH));
	}
}
