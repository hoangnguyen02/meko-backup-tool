package vn.mekosoft.backup.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ConfigReader {
//	private static final String CONFIG_LOG = "/projectId_${project_id}_backupTaskId_${task_id}.log";
//	private String LOG_FOLDER_PATH = "src/vn/mekosoft/backup/log";
//	private String CONFIG_FOLDER_PATH = "config.json";
//
////	private String LOG_FOLDER_PATH = "/home/ubuntu/sftp_ver2/log";
////	private String CONFIG_FOLDER_PATH = "/home/ubuntu/sftp_ver2/config.json";
//
//	public void setLogFolderPath(String logFolderPath) {
//		this.LOG_FOLDER_PATH = logFolderPath;
//	}
//
//	public void setConfigFolderPath(String configFolderPath) {
//		this.CONFIG_FOLDER_PATH = configFolderPath;
//	}
//
//	public String getConfigLog(long projectId, long backupTaskId) {
//		return LOG_FOLDER_PATH + CONFIG_LOG.replace("${project_id}", String.valueOf(projectId)).replace("${task_id}",
//				String.valueOf(backupTaskId));
//	}
//
//	public String getLogFolderPath() {
//		return LOG_FOLDER_PATH;
//	}
//
//	public String getConfigFolderPath() {
//		return CONFIG_FOLDER_PATH;
//	}

	
	
//	 private static final String CONFIG_LOG = "/projectId_${project_id}_backupTaskId_${task_id}.log";
//	    private String LOG_FOLDER_PATH;
//	    private String CONFIG_FOLDER_PATH;
//
//	    public ConfigReader() {
//	        loadPathsFromJson();
//	    }
//
//	    private void loadPathsFromJson() {
//	        Gson gson = new Gson();
//	        String fileName = "path.json";
//	        File file = new File(fileName);
//
//	        if (!file.exists()) {
//	            createDefaultJsonFile(file);
//	        }
//
//	        try (FileReader reader = new FileReader(file)) {
//	            Map<String, String> paths = gson.fromJson(reader, new TypeToken<Map<String, String>>() {}.getType());
//
//	            // Now use paths as needed
//	            this.LOG_FOLDER_PATH = paths.get("LOG_FOLDER_PATH");
//	            this.CONFIG_FOLDER_PATH = paths.get("CONFIG_FOLDER_PATH");
//
//	            System.out.println("LOG_FOLDER_PATH: " + LOG_FOLDER_PATH);
//	            System.out.println("CONFIG_FOLDER_PATH: " + CONFIG_FOLDER_PATH);
//
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//
//	    private void createDefaultJsonFile(File file) {
//	        Map<String, String> defaultPaths = new HashMap<>();
//	        String homeDir = System.getProperty("user.home");
//	        defaultPaths.put("LOG_FOLDER_PATH", homeDir + File.separator + "myapp" + File.separator + "logs");
//	        defaultPaths.put("CONFIG_FOLDER_PATH", homeDir + File.separator + "myapp" + File.separator + "config");
//
//	        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//	        String json = gson.toJson(defaultPaths);
//
//	        try (FileWriter writer = new FileWriter(file)) {
//	            writer.write(json);
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
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
	
	
	
	
	
	private static final String CONFIG_LOG = "/projectId_${project_id}_backupTaskId_${task_id}.log";
	private String LOG_FOLDER_PATH;
	private String CONFIG_FOLDER_PATH;

	public ConfigReader() {
		String backupToolPath = System.getenv("BACKUPTOOL");

		if (backupToolPath != null && !backupToolPath.isEmpty()) {
			LOG_FOLDER_PATH = backupToolPath + "/log";
						//	home/ubuntu/sftp_ver2 + /log
						//	home/ubuntu/sftp_ver2 + /config.json
			CONFIG_FOLDER_PATH = backupToolPath + "/config.json";


		} else {
//	                LOG_FOLDER_PATH = "/home/ubuntu/sftp_ver2/log";
//	                CONFIG_FOLDER_PATH = "/home/ubuntu/sftp_ver2/config.json";
		}
	}
	 
	public void setLogFolderPath(String logFolderPath) {
		this.LOG_FOLDER_PATH = logFolderPath;
	}

	public void setConfigFolderPath(String configFolderPath) {
		this.CONFIG_FOLDER_PATH = configFolderPath;
	}

	public String getConfigLog(long projectId, long backupTaskId) {
		return LOG_FOLDER_PATH  + CONFIG_LOG.replace("${project_id}", String.valueOf(projectId)).replace("${task_id}",
			// home/ubuntu/sftp_ver2 + "/projectId_${project_id}_backupTaskId_${task_id}.log";
				String.valueOf(backupTaskId));
	}

	public String getLogFolderPath() {
		return LOG_FOLDER_PATH;
	}

	public String getConfigFolderPath() {
		return CONFIG_FOLDER_PATH;
	}

}
