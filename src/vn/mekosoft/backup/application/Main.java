package vn.mekosoft.backup.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import vn.mekosoft.backup.controller.Dashboard;

public class Main extends Application {
	private Dashboard controller;

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/vn/mekosoft/backup/view/dashboard.fxml"));
			Scene scene = new Scene(root);
			//FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/dashboard.fxml"));
			//Scene scene = new Scene(fxmlLoader.load(),Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Image icon = new Image(getClass().getResourceAsStream("/vn/mekosoft/backup/view/img/company_logo.png"));
			primaryStage.getIcons().add(icon);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Meko Backup Tool");
			   primaryStage.setOnCloseRequest(event -> {
		            if (controller != null) {
		                controller.stopAutoRefresh();
		            }
		            Platform.exit();
		            System.exit(0);
		        });
//			   Screen screen = Screen.getPrimary();
//		        double screenWidth = screen.getBounds().getWidth();
//		        double screenHeight = screen.getBounds().getHeight();
//
//		        // Thiết lập kích thước ban đầu cho cửa sổ
//		        primaryStage.setWidth(screenWidth * 0.8);
//		        primaryStage.setHeight(screenHeight * 0.8); 
//
//		        // Đặt title cho cửa sổ
//
//		        // Đăng ký sự kiện cho việc thay đổi kích thước cửa sổ
//		        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
//		            // Xử lý khi kích thước chiều rộng của cửa sổ thay đổi
//		            System.out.println("Width changed: " + newVal);
//		        });
//
//		        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
//		            // Xử lý khi kích thước chiều cao của cửa sổ thay đổi
//		            System.out.println("Height changed: " + newVal);
//		        });

		        // Hiển thị Stage
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  
	public static void main(String[] args) {
		launch(args);
	}
}
