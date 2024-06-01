package vn.mekosoft.backup.service;

import java.util.List;

import vn.mekosoft.backup.model.BackupFolder;

public interface BackupFolderService {
	List<BackupFolder> loadFolderData();

	void updateBackupFolder(long folderId, BackupFolder updatedFolder);

	void deleteBackupFolder(long folderId);
}
