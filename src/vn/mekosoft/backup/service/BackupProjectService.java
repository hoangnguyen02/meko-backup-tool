package vn.mekosoft.backup.service;

import java.util.List;

import vn.mekosoft.backup.model.BackupProject;

public interface BackupProjectService {
	List<BackupProject> loadProjectData();

	void addProject(BackupProject backupProject);

	void updateProject(long projectId, BackupProject updatedProject);

	void deleteProject(long projectId);

}
