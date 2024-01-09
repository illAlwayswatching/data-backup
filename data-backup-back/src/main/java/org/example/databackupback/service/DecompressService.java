package org.example.databackupback.service;

import org.example.databackupback.common.R;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/7 16:22
 **/
public interface DecompressService {
    R decompressByPath(String username, String target, String zipPath);

    R decompressById(String username, String target, String zipId);
}
