package vn.mekosoft.backup.model;

public enum BackupTaskStatus {
	DANG_BIEN_SOAN(1, "Đang biên soạn"),
	HOAT_DONG(2, "Hoạt động");

	private final long id;
	private final String description;

	BackupTaskStatus(long id, String description) {
		this.id = id;
		this.description = description;
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
