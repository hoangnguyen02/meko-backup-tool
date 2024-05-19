package vn.mekosoft.backup.action;

import java.util.List;

import vn.mekosoft.backup.impl.BackupServiceImpl;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupProject;
import vn.mekosoft.backup.model.BackupProjectStatus;
import vn.mekosoft.backup.model.BackupTask;
import vn.mekosoft.backup.model.BackupTaskStatus;

public class BackupActionUtil {
//	public static void main(String[] args) {
//        BackupServiceImpl backupService = new BackupServiceImpl();
//        List<BackupProject> backupProjects = backupService.loadData();
//
//        // In thông tin của mỗi dự án
//        for (BackupProject project : backupProjects) {
//            System.out.println("Backup Project ID: " + project.getBackupProjectId());
//            System.out.println("Name: " + project.getName());
//            System.out.println("Description: " + project.getDescription());
//            System.out.println("Hostname: " + project.getHostname());
//            System.out.println("Username: " + project.getUsername());
//            System.out.println("Password: " + project.getPassword());
//            System.out.println("\tStatus: " + BackupProjectStatus.fromId(project.getBackupProjectStatus()).getDescription());
//            
//            // In thông tin của mỗi nhiệm vụ trong dự án
//            List<BackupTask> tasks = project.getBackupTasks();
//            System.out.println("Tasks:");
//            for (BackupTask task : tasks) {
//                System.out.println("\tTask ID: " + task.getBackupTaskId());
//                System.out.println("\tName: " + task.getName());
//                System.out.println("\tLocal Scheduler: " + task.getLocalSchedular());
//                System.out.println("\tRemote Scheduler: " + task.getRemoteSchedular());
//                System.out.println("\tLocal Path: " + task.getLocalPath());
//                System.out.println("\tRemote Path: " + task.getRemotePath());
//                System.out.println("\tLocal Retention: " + task.getLocalRetention());
//                System.out.println("\tRemote Retention: " + task.getRemoteRetention());
//                System.out.println("\tStatus: " + BackupTaskStatus.fromId(task.getBackupTaskStatus()).getDescription());
//                
//                // In thông tin của mỗi thư mục sao lưu trong nhiệm vụ
//                List<BackupFolder> folders = task.getBackupFolders();
//                System.out.println("\tFolders:");
//                for (BackupFolder folder : folders) {
//                    System.out.println("\t\tFolder ID: " + folder.getBackupFolderId());
//                    System.out.println("\t\tPath: " + folder.getFolderPath());
//                }
//            }
//        }
//    }
}
