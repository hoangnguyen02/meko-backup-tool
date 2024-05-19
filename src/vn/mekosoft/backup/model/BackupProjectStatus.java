package vn.mekosoft.backup.model;

public enum BackupProjectStatus {
	DANG_BIEN_SOAN(1, "Đang biên soạn"),
    HOAT_DONG(2, "Hoạt động"),
    KHONG_HOAT_DONG(3, "Không hoạt động");

    private final long id;
    private final String description;

    BackupProjectStatus(long id, String description) {
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

    public static BackupProjectStatus fromId(long id) {
        for (BackupProjectStatus status : values()) {
            if (status.getId() == id) {
                return status;
            }
        }
		return null;
    }
}
