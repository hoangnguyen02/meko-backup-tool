package vn.mekosoft.backup.model;

public enum BackupProjectStatus {
	DANG_BIEN_SOAN(1, "Đang biên soạn"),
    HOAT_DONG(2, "Hoạt động"),
    KHONG_HOAT_DONG(3, "Không hoạt động");

    private final long id;
    private final String descriptionStatusProject;

    BackupProjectStatus(long id, String descriptionStatusProject) {
        this.id = id;
        this.descriptionStatusProject = descriptionStatusProject;
    }

    public long getId() {
        return id;
    }

    public String getDescriptionStatusProject() {
        return descriptionStatusProject;
    }

    @Override
    public String toString() {
        return descriptionStatusProject;
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
