package vn.mekosoft.backup.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class FolderTable implements Initializable {
	@FXML
	private TextField folder_path;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	public void setFolderPath(String path) {
		folder_path.setText(path); 
	}
}
