package vn.mekosoft.backup.model;

import java.util.List;

public class BackupTask {
	 private static int autoIncrease = 1;
	private long backupTaskId;
	private long backupProjectId;
	private String name;
	private String localSchedular;
	private String localPath;
	private long localRetention;
	private String remoteSchedular;
	private String remotePath;
	private long remoteRetention;
	private long backupTaskStatus;
	private List<BackupFolder> backupFolders;
	
	public BackupTask() {
		// default constructor
	}
	
	public BackupTask(long backupTaskId, long backupProjectId, String name, String localSchedular, String localPath, long localRetention, String remoteSchedular, String remotePath, long remoteRetention, long backupTaskStatus, List<BackupFolder> backupFolders) {
		this.backupTaskId = autoIncrease++;
		this.backupProjectId = backupProjectId;
		this.name = name;
		this.localSchedular = localSchedular;
		this.localPath = localPath;
		this.localRetention = localRetention;
		this.remoteSchedular = remoteSchedular;
		this.remotePath = remotePath;
		this.remoteRetention = remoteRetention;	
		this.backupTaskStatus = backupTaskStatus;
		this.backupFolders = backupFolders;
		
	}
	public static void setAutoIncrease(int autoIncrease) {
		BackupTask.autoIncrease=autoIncrease;
	}

	public List<BackupFolder> getBackupFolders() {
		return backupFolders;
	}

	public void setBackupFolders(List<BackupFolder> backupFolders) {
		this.backupFolders = backupFolders;
	}

	public long getBackupTaskId() {
		return backupTaskId;
	}

	public void setBackupTaskId(long backupTaskId) {
		this.backupTaskId = backupTaskId;
	}

	public long getBackupProjectId() {
		return backupProjectId;
	}

	public void setBackupProjectId(long backupProjectId) {
		this.backupProjectId = backupProjectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocalSchedular() {
		return localSchedular;
	}

	public void setLocalSchedular(String localSchedular) {
		this.localSchedular = localSchedular;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public long getLocalRetention() {
		return localRetention;
	}

	public void setLocalRetention(long localRetention) {
		this.localRetention = localRetention;
	}

	public String getRemoteSchedular() {
		return remoteSchedular;
	}

	public void setRemoteSchedular(String remoteSchedular) {
		this.remoteSchedular = remoteSchedular;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public long getRemoteRetention() {
		return remoteRetention;
	}

	public void setRemoteRetention(long remoteRetention) {
		this.remoteRetention = remoteRetention;
	}

	public long getBackupTaskStatus() {
		return backupTaskStatus;
	}

	public void setBackupTaskStatus(long backupTaskStatus) {
		this.backupTaskStatus = backupTaskStatus;
	}
	public BackupTaskStatus getBackupTaskStatusEnum() {
		return BackupTaskStatus.fromId(backupTaskStatus);
	}

	public void setBackupTaskStatusFromEnum(BackupTaskStatus status) {
		this.backupTaskStatus = status.getId();
	}

	@Override
	public String toString() {
		return "BackupTask [backupTaskId=" + backupTaskId + ", backupProjectId=" + backupProjectId + ", name=" + name
				+ ", localSchedular=" + localSchedular + ", localPath=" + localPath + ", localRetention="
				+ localRetention + ", remoteSchedular=" + remoteSchedular + ", remotePath=" + remotePath
				+ ", remoteRetention=" + remoteRetention + ", backupTaskStatus=" + backupTaskStatus + "]";
	}
	

}
