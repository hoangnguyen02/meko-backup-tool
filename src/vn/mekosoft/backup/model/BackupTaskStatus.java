package vn.mekosoft.backup.model;

import vn.mekosoft.backup.controller.Dashboard;

public enum BackupTaskStatus {
	DANG_BIEN_SOAN(1, "Đang biên soạn", "#ffff00"),
	HOAT_DONG(2, "Hoạt động", "#00ff00"),
	DA_DAT_LICH(3,"Đã đặt lịch", "#00ff00"),
	KHONG_HOAT_DONG(4, "Không hoạt động", "#ff0000");

	
	private final long id;
	private final String description;
	private String colorTask;

	

	 BackupTaskStatus(long id, String description, String colorTask) {
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
	 public static BackupTaskStatus fromDescription(String description) {
	        for (BackupTaskStatus status : values()) {
	            if (status.getDescription().equalsIgnoreCase(description)) {
	                return status;
	            }
	        }
	        return null;
	    }


}
