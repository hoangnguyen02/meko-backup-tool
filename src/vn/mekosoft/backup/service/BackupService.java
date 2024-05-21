package vn.mekosoft.backup.service;

import java.util.List;

import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupTask;

public interface BackupService {
	List<BackupProject> loadData();


	
}
