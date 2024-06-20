// Updated TableModel class definition
package vn.mekosoft.backup.model;

public class TableModel {
    private String projectName; // Using String for consistency
    private String backupTaskName;
    private String folderCount; // Adjusted to String to match TableColumn properties
    private int countBackup; // Added to match backup count
    private String crontabLocal;
    private String crontabRemote;
    private String backupTaskStatus;
    private int successful;
    private int failed;
	public TableModel(String projectName, String backupTaskName, String folderCount, int countBackup,
			String crontabLocal, String crontabRemote, String backupTaskStatus, int successful, int failed) {
		super();
		this.projectName = projectName;
		this.backupTaskName = backupTaskName;
		this.folderCount = folderCount;
		this.countBackup = countBackup;
		this.crontabLocal = crontabLocal;
		this.crontabRemote = crontabRemote;
		this.backupTaskStatus = backupTaskStatus;
		this.successful = successful;
		this.failed = failed;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getBackupTaskName() {
		return backupTaskName;
	}
	public void setBackupTaskName(String backupTaskName) {
		this.backupTaskName = backupTaskName;
	}
	public String getFolderCount() {
		return folderCount;
	}
	public void setFolderCount(String folderCount) {
		this.folderCount = folderCount;
	}
	public int getCountBackup() {
		return countBackup;
	}
	public void setCountBackup(int countBackup) {
		this.countBackup = countBackup;
	}
	public String getCrontabLocal() {
		return crontabLocal;
	}
	public void setCrontabLocal(String crontabLocal) {
		this.crontabLocal = crontabLocal;
	}
	public String getCrontabRemote() {
		return crontabRemote;
	}
	public void setCrontabRemote(String crontabRemote) {
		this.crontabRemote = crontabRemote;
	}
	public String getBackupTaskStatus() {
		return backupTaskStatus;
	}
	public void setBackupTaskStatus(String backupTaskStatus) {
		this.backupTaskStatus = backupTaskStatus;
	}
	public int getSuccessful() {
		return successful;
	}
	public void setSuccessful(int successful) {
		this.successful = successful;
	}
	public int getFailed() {
		return failed;
	}
	public void setFailed(int failed) {
		this.failed = failed;
	}

 
}

