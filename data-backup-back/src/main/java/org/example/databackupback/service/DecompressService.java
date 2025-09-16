package org.example.databackupback.service;

import org.example.databackupback.common.Response;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/11 16:47
 **/
public interface DecompressService {
    Response decompressByPath(String username, String target, String zipPath);

    Response decompressById(String username, String target, String zipId);
}

