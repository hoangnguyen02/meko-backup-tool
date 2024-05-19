package vn.mekosoft.backup.model;

import java.util.List;

public class BackupProject {
	private long backupProjectId;
	private String name;
	private String description;
	private String hostname;
	private String username;
	private String password;
	private long backupProjectStatus;
	private List<BackupTask> backupTasks;

	public BackupProject() {
		// default constructor
	}

	public BackupProject(long backupProjectId, String name, String description, String hostname, String username,
			String password, long backupProjectStatus, List<BackupTask> backupTasks) {
		this.backupProjectId = backupProjectId;
		this.name = name;
		this.description = description;
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.backupProjectStatus = backupProjectStatus;
		this.backupTasks = backupTasks;
	}
	

	public List<BackupTask> getBackupTasks() {
		return backupTasks;
	}

	public void setBackupTasks(List<BackupTask> backupTasks) {
		this.backupTasks = backupTasks;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getBackupProjectStatus() {
		return backupProjectStatus;
	}

	public void setBackupProjectStatus(long backupProjectStatus) {
		this.backupProjectStatus = backupProjectStatus;
	}

	public BackupProjectStatus getBackupProjectStatusEnum() {
		return BackupProjectStatus.fromId(backupProjectStatus);
	}

	public void setBackupProjectStatusFromEnum(BackupProjectStatus status) {
		this.backupProjectStatus = status.getId();
	}

	@Override
	public String toString() {
		return "BackupProject [backupProjectId=" + backupProjectId + ", name=" + name + ", description=" + description
				+ ", hostname=" + hostname + ", username=" + username + ", password=" + password
				+ ", backupProjectStatus=" + backupProjectStatus + "]";
	}

}
