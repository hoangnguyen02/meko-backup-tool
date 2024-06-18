package vn.mekosoft.backup.service;

import java.util.List;

import vn.mekosoft.backup.model.BackupProject;

public interface BackupService {
	List<BackupProject> loadData();

	void saveData(List<BackupProject> backupProjects);
}
