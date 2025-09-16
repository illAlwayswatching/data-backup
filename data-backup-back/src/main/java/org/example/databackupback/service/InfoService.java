package org.example.databackupback.service;

import org.example.databackupback.common.Response;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/11 16:38
 **/
public interface InfoService {
    Response getEntries(String username, String path);

    Response delBackup(Integer id);

    Response addFolder(String username, String path, String folderName);

    Response copyInServer(String username, String fileId, String to);
}
