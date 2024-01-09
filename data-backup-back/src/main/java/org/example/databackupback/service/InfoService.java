package org.example.databackupback.service;

import org.example.databackupback.common.R;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/6 17:48
 **/
public interface InfoService {
    R getEntries(String username, String path);

    R delBackup(Integer id);

    R addFolder(String username, String path, String folderName);

    R copyInServer(String username, String fileId, String to);
}
