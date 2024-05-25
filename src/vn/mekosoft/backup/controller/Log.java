package vn.mekosoft.backup.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import vn.mekosoft.backup.config.Config;
import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.model.BackupProject;

public class Log implements Initializable {
	@FXML
	private TextArea content_log;

	@FXML
	private Label folderPath_log;

	@FXML
	private AnchorPane log_view;

	@FXML
	private Label projectName_log;

	@FXML
	private Label taskName_log;

	private Config config_LogFile;
	public static Logger LOGGER;
    static {
        LOGGER = Logger.getLogger(Log.class.getName());
    }

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		 LOGGER.info("Log controller initialized.");
	
		readLog();
	}
	
	  
	private void readLog() {
	    if (config_LogFile != null) {
	        String logFilePath = config_LogFile.getConfigLog();
	        	        if (logFilePath != null && !logFilePath.isEmpty()) {
	            try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
	                StringBuilder content = new StringBuilder();
	                String line;
	                while ((line = reader.readLine()) != null) {
	                    content.append(line).append("\n");
	                }
	                content_log.setText(content.toString());
	            } catch (IOException e) {
	                e.printStackTrace();
	                content_log.setText("Error: " + e.getMessage());
	            }
	        } 
	    }
	    System.out.print("k lấy được");
	}
}
