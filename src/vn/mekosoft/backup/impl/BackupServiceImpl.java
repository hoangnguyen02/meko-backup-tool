package vn.mekosoft.backup.impl;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;
import vn.mekosoft.backup.service.BackupService;

public class BackupServiceImpl implements BackupService {
	@Override

	public List<BackupProject> loadData() {
	    List<BackupProject> fakeDataBackupProject = new ArrayList<>();
	    
//	    fakeDataBackupProject.add(new BackupProject(1, "Backup Server 1", "Description 1", "pve1", "pvebackup", "admin",
//	            BackupProjectStatus.DANG_BIEN_SOAN.getId(), new ArrayList<>(loadTasks())));
//
//	 
//	    fakeDataBackupProject.add(new BackupProject(2, "Backup Server 2", "Description 2", "pve2", "pveuser", "admin",
//	            BackupProjectStatus.HOAT_DONG.getId(), new ArrayList<>(loadTasks2())));
//
//	    fakeDataBackupProject.add(new BackupProject(3, "Backup Server 3", "Description 2", "pve2", "pveuser", "admin",
//	            BackupProjectStatus.HOAT_DONG.getId(), new ArrayList<>(loadTasks3())));
	    
	    return fakeDataBackupProject;
	}

	
	public List<BackupTask> loadTasks() {
	    List<BackupTask> fakeDataBackupTask1 = new ArrayList<>();
	    fakeDataBackupTask1.add(new BackupTask(1, 1, "Backup Folder 1", "*/2 * * * *", "/home/ubuntu/sftp_ver2", 2,
	            "2 * * 8 *", "pve/backup", 3, BackupTaskStatus.DANG_BIEN_SOAN.getId(), new ArrayList<>(loadFolders())));
	    fakeDataBackupTask1.add(new BackupTask(2, 1, "Backup Folder 2", "*/5 * * * *", "/home/ubuntu/sftp_ver2", 2,
	            "2 * * 8 *", "pve/backup", 3, BackupTaskStatus.HOAT_DONG.getId(), new ArrayList<>(loadFolders())));
	  
	    List<BackupTask> allTasks = new ArrayList<>();
	    allTasks.addAll(fakeDataBackupTask1);
	    return allTasks;
	}
	
	public List<BackupTask> loadTasks2() {
	    List<BackupTask> fakeDataBackupTask2 = new ArrayList<>();
	    fakeDataBackupTask2.add(new BackupTask(1, 2, "Backup Folder 3", "*/2 * * * *", "/home/ubuntu/sftp_ver2", 2,
	            "2 * * 8 *", "pve/backup", 3, BackupTaskStatus.DANG_BIEN_SOAN.getId(), new ArrayList<>(loadFolders())));
	    fakeDataBackupTask2.add(new BackupTask(2, 2, "Backup Folder 4", "*/5 * * * *", "/home/ubuntu/sftp_ver2", 2,
	            "2 * * 8 *", "pve/backup", 3, BackupTaskStatus.HOAT_DONG.getId(), new ArrayList<>(loadFolders())));
	    
	    List<BackupTask> allTasks = new ArrayList<>();
	    allTasks.addAll(fakeDataBackupTask2);
	    return allTasks;
	}
	
	public List<BackupTask> loadTasks3() {
	    List<BackupTask> fakeDataBackupTask3 = new ArrayList<>();
	    fakeDataBackupTask3.add(new BackupTask(1, 3, "Backup Folder 5", "*/2 * * * *", "/home/ubuntu/sftp_ver2", 2,
	            "2 * * 8 *", "pve/backup", 3, BackupTaskStatus.DANG_BIEN_SOAN.getId(), new ArrayList<>(loadFolders())));
	    fakeDataBackupTask3.add(new BackupTask(2, 3, "Backup Folder 6", "*/5 * * * *", "/home/ubuntu/sftp_ver2", 2,
	            "2 * * 8 *", "pve/backup", 3, BackupTaskStatus.HOAT_DONG.getId(), new ArrayList<>(loadFolders())));
	    
	    List<BackupTask> allTasks = new ArrayList<>();
	    allTasks.addAll(fakeDataBackupTask3);
	    return allTasks;
	}

	public List<BackupFolder> loadFolders() {
		List<BackupFolder> fakeDataBackupFolder = new ArrayList<>();
		fakeDataBackupFolder.add(new BackupFolder(1, "/var/lib/vz/dump", 1));
		fakeDataBackupFolder.add(new BackupFolder(1, "/folder1", 2));
		fakeDataBackupFolder.add(new BackupFolder(2, "/folder2", 2));
		return fakeDataBackupFolder;
	}

}
