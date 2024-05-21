package vn.mekosoft.backup.model;

import java.util.List;

public class BackupProject {
	private long projectId;
	private String projectName;
	private String description;
	private String hostname;
	private String username;
	private String password;
	private long backupProjectStatus;
	private List<BackupTask> backupTasks;

	public BackupProject() {
		// default constructor
	}
	
	

	public BackupProject(long projectId, String projectName, String description, String hostname, String username,
			String password, long backupProjectStatus, List<BackupTask> backupTasks) {
		this.projectId = projectId;
		this.projectName = projectName;
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

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String name) {
		this.projectName = projectName;
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
		return "BackupProject [backupProjectId=" + projectId + ", projectName=" + projectName + ", description=" + description
				+ ", hostname=" + hostname + ", username=" + username + ", password=" + password
				+ ", backupProjectStatus=" + backupProjectStatus + "]";
	}

}
