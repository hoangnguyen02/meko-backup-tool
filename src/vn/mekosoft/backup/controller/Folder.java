package vn.mekosoft.backup.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import vn.mekosoft.backup.model.BackupFolder;
import vn.mekosoft.backup.model.BackupTask;

public class Folder {

    @FXML
    private TextField get_FolderId;

    @FXML
    private TextField get_FolderPath;

    @FXML
    private AnchorPane infor_folder;
    public void folderData(BackupFolder folder) {
        get_FolderId.setText(String.valueOf(folder.getBackupFolderId()));
        get_FolderPath.setText(folder.getFolderPath());
    
    }
}
