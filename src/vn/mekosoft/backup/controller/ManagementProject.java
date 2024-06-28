package vn.mekosoft.backup.controller;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import vn.mekosoft.backup.action.AlertMaker;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.service.BackupProjectService;
import vn.mekosoft.backup.impl.BackupProjectServiceImpl;

public class ManagementProject implements Initializable {
	@FXML
	private Button button_addTask;

	@FXML
	private AnchorPane infor_project;

	@FXML
	private Label project_activity;

	@FXML
	private Label project_hostname;

	@FXML
	private Label project_name_main;

	@FXML
	private Label project_username;

	@FXML
	private Circle circle_project_status;

	@FXML
	private Button button_DeleteProject;

	@FXML
	private Button button_EditProject;
	private BackupProject project;
	private Dashboard dashboardController;

	public void setDashboardController(Dashboard dashboardController) {
		this.dashboardController = dashboardController;
	}

	public void deleteProject_action(ActionEvent event) {
		Optional<ButtonType> response = AlertMaker.showConfirmAlert("Delete Project",
				"Are you sure you want to delete this project?");
		if (response.isPresent() && response.get() == ButtonType.OK) {
			BackupProjectService projectService = new BackupProjectServiceImpl();
			projectService.deleteProject(project.getProjectId());
			dashboardController.rf();
			dashboardController.refresh_action();
		}
	}

	public void editProject_action(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/editProject.fxml"));
			Parent root = loader.load();

			EditProject editProjectController = loader.getController();
			editProjectController.setProject(project);

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Edit Project");
			stage.getIcons().add(new Image("/vn/mekosoft/backup/view/img/company_logo.png"));
			stage.showAndWait();

			projectData(project);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addTask_action(ActionEvent event) throws IOException {
		BackupTask task = new BackupTask();
		project.getBackupTasks().add(task);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/mekosoft/backup/view/detailsTask.fxml"));
		Parent root = loader.load();
		DetailsTask controller = loader.getController();
		controller.setProject(project);
		controller.setDashboardController(dashboardController);
		controller.taskDetails(task);
		controller.inforClear();
		controller.hideFolderPane();
		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.setTitle("Add New Task");
		stage.getIcons().add(new Image("/vn/mekosoft/backup/view/img/company_logo.png"));

		// Đặt vị trí cửa sổ ở trung tâm
		stage.setWidth(1040); // Chiều rộng của cửa sổ
		stage.setHeight(400);
		stage.show();

		stage.setOnHidden(e -> {
			dashboardController.rf();
			dashboardController.refresh_action();
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle resources) {

	}

	public void projectData(BackupProject project) {
		this.project = project;
		project_name_main.setText("Project: " + " " + project.getProjectName());
		project_hostname.setText(" ● Hostname: " + " " + project.getHostname());
		project_username.setText(" ● Username: " + " " + project.getUsername());
		project_activity
				.setText(BackupProjectStatus.fromId(project.getBackupProjectStatus()).getDescriptionStatusProject());
		String colorCode = BackupProjectStatus.fromId(project.getBackupProjectStatus()).getColorProject();

		Color color = Color.web(colorCode);
		circle_project_status.setFill(color);
	}
}
