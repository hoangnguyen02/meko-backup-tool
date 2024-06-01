package vn.mekosoft.backup.model;

public enum BackupTaskStatus {
	DANG_BIEN_SOAN(1, "Đang biên soạn", "#ffff00"),
	HOAT_DONG(2, "Hoạt động", "#00ff00");

	private final long id;
	private final String description;
	private String colorTask;

	

	private BackupTaskStatus(long id, String description, String colorTask) {
		this.id = id;
		this.description = description;
		this.colorTask = colorTask;
	}
	

	public String getColorTask() {
		return colorTask;
	}

	public void setColorTask(String colorTask) {
		this.colorTask = colorTask;
	}


	public long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return description;
	}

	public static BackupTaskStatus fromId(long id) {
		for (BackupTaskStatus status : values()) {
			if (status.getId() == id) {
				return status;
			}
		}
		return null;
		
	}
}
