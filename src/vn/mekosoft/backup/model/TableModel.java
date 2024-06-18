package vn.mekosoft.backup.model;

public class TableModel {
	private long projectId;
	private String backupTaskName;
	private String folderPath;
	private String localPath;
	private String remotePath;
	private String backupTaskStatus;
	public TableModel(long projectId, String backupTaskName, String folderPath, String localPath, String remotePath,
			String backupTaskStatus) {
		super();
		this.projectId = projectId;
		this.backupTaskName = backupTaskName;
		this.folderPath = folderPath;
		this.localPath = localPath;
		this.remotePath = remotePath;
		this.backupTaskStatus = backupTaskStatus;
	}
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public String getBackupTaskName() {
		return backupTaskName;
	}
	public void setBackupTaskName(String backupTaskName) {
		this.backupTaskName = backupTaskName;
	} 
	public String getFolderPath() {
		return folderPath;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getRemotePath() {
		return remotePath;
	}
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	public String getBackupTaskStatus() {
		return backupTaskStatus;
	}
	public void setBackupTaskStatus(String backupTaskStatus) {
		this.backupTaskStatus = backupTaskStatus;
	}
	
	
}
