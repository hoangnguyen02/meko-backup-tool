package vn.mekosoft.backup.model;

public class BackupFolder {
	private long backupFolderId;
	private String folderPath;
	private long backupTaskId;

	public BackupFolder() {
		// default constructor
	}

	public BackupFolder(long backupFolderId, String folderPath, long backupTaskId) {
		this.backupFolderId = backupFolderId;
		this.folderPath = folderPath;
		this.backupTaskId = backupTaskId;
	}

	public long getBackupFolderId() {
		return backupFolderId;
	}

	public void setBackupFolderId(long backupFolderId) {
		this.backupFolderId = backupFolderId;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public long getBackupTaskId() {
		return backupTaskId;
	}

	public void setBackupTaskId(long backupTaskId) {
		this.backupTaskId = backupTaskId;
	}

	@Override
	public String toString() {
		return "BackupFolder [backupFolderId=" + backupFolderId + ", folderPath=" + folderPath + ", backupTaskId="
				+ backupTaskId + "]";
	}

}
