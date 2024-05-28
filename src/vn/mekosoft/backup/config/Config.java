package vn.mekosoft.backup.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Config {
	private static final String CONFIG_LOG = "/projectId_${project_id}_backupTaskId_${task_id}.log";
	private String LOG_FOLDER_PATH = "src/vn/mekosoft/backup/log";
	private String CONFIG_FOLDER_PATH = "config.json";

//	private String LOG_FOLDER_PATH = "/home/ubuntu/sftp_ver2/log";
//	private String CONFIG_FOLDER_PATH = "/home/ubuntu/sftp_ver2/config.json";

	public void setLogFolderPath(String logFolderPath) {
		this.LOG_FOLDER_PATH = logFolderPath;
	}

	public void setConfigFolderPath(String configFolderPath) {
		this.CONFIG_FOLDER_PATH = configFolderPath;
	}

	public String getConfigLog(long projectId, long backupTaskId) {
		return LOG_FOLDER_PATH + CONFIG_LOG.replace("${project_id}", String.valueOf(projectId)).replace("${task_id}",
				String.valueOf(backupTaskId));
	}

	public String getLogFolderPath() {
		return LOG_FOLDER_PATH;
	}

	public String getConfigFolderPath() {
		return CONFIG_FOLDER_PATH;
	}
}
