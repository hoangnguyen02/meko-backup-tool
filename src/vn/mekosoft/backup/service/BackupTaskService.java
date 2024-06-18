package vn.mekosoft.backup.service;

import java.util.List;

import vn.mekosoft.backup.model.BackupTask;

public interface BackupTaskService {
	List<BackupTask> loadTaskData();




	void updateBackupTask(long projectId, long taskId, BackupTask updatedTask);

	void deleteBackupTask(long projectId, long taskId);



}
