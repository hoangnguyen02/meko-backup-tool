package vn.mekosoft.backup.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ConfigReader {
	private static final String CONFIG_LOG = "/projectId_${project_id}_backupTaskId_${task_id}.log";
//	private String LOG_FOLDER_PATH = "src/vn/mekosoft/backup/log";
//	private String CONFIG_FOLDER_PATH = "config.json";

	private String LOG_FOLDER_PATH = "/home/ubuntu/sftp_ver2/log";
	private String CONFIG_FOLDER_PATH = "/home/ubuntu/sftp_ver2/config.json";

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
	
//	 private static final String CONFIG_LOG = "/projectId_${project_id}_backupTaskId_${task_id}.log";
//	    private String LOG_FOLDER_PATH;
//	    private String CONFIG_FOLDER_PATH;
//
//	    public ConfigReader() {
//	        loadPathsFromJson();
//	    }
//
//	    private void loadPathsFromJson() {
//	    	Gson gson = new Gson();
//	    	try (InputStream inputStream = getClass().getResourceAsStream("/vn/mekosoft/backup/resources/path.json")) {
//	    	    System.out.println("Input Stream: " + inputStream);
//	    	    if (inputStream != null) {
//	    	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//	    	        System.out.println("Reader: " + reader);
//	    	        
//	    	        // Read the JSON content from the reader
//	    	        StringBuilder jsonContent = new StringBuilder();
//	    	        String line;
//	    	        while ((line = reader.readLine()) != null) {
//	    	            jsonContent.append(line);
//	    	        }
//	    	        
//	    	        // Convert JSON to Map using Gson
//	    	        Map<String, String> paths = gson.fromJson(jsonContent.toString(), new TypeToken<Map<String, String>>() {}.getType());
//
//	    	        // Now use paths as needed
//	    	        String logFolderPath = paths.get("LOG_FOLDER_PATH");
//	    	        String configFolderPath = paths.get("CONFIG_FOLDER_PATH");
//
//	    	        System.out.println("LOG_FOLDER_PATH: " + logFolderPath);
//	    	        System.out.println("CONFIG_FOLDER_PATH: " + configFolderPath);
//
//	    	    } else {
//	    	        System.err.println("File path.json not found or cannot be read.");
//	    	    }
//	    	} catch (IOException e) {
//	    	    e.printStackTrace();
//	    	}
//
//	    }
//
//	    public void setLogFolderPath(String logFolderPath) {
//	        this.LOG_FOLDER_PATH = logFolderPath;
//	    }
//
//	    public void setConfigFolderPath(String configFolderPath) {
//	        this.CONFIG_FOLDER_PATH = configFolderPath;
//	    }
//
//	    public String getConfigLog(long projectId, long backupTaskId) {
//	        return LOG_FOLDER_PATH + CONFIG_LOG.replace("${project_id}", String.valueOf(projectId))
//	                                           .replace("${task_id}", String.valueOf(backupTaskId));
//	    }
//
//	    public String getLogFolderPath() {
//	        return LOG_FOLDER_PATH;
//	    }
//
//	    public String getConfigFolderPath() {
//	        return CONFIG_FOLDER_PATH;
//	    }
}
