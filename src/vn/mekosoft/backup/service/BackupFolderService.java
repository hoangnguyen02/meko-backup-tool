package vn.mekosoft.backup.service;

import vn.mekosoft.backup.model.BackupFolder;
import java.util.List;

public interface BackupFolderService {
    List<BackupFolder> loadFolderData(long projectId, long taskId);
    void updateBackupFolder(long projectId, long taskId, long folderId, BackupFolder updatedFolder);
    void deleteBackupFolder(long projectId, long taskId, long folderId);
}
