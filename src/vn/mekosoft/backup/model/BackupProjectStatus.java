package vn.mekosoft.backup.model;

public enum BackupProjectStatus {
	DANG_BIEN_SOAN(1, "Đang biên soạn", "#ffff00"), 
	HOAT_DONG(2, "Hoạt động", "#00ff00"),
	KHONG_HOAT_DONG(3, "Không hoạt động", "#ff0000");

	private final long id;
	private final String descriptionStatusProject;
	private String colorProject; 

	private BackupProjectStatus(long id, String descriptionStatusProject, String colorProject) {
		this.id = id;
		this.descriptionStatusProject = descriptionStatusProject;
		this.colorProject = colorProject;
	}

	public String getColorProject() {
		return colorProject;
	}

	public void setColorProject(String colorProject) {
		this.colorProject = colorProject;
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
