package org.example.databackupback.service;

import org.example.databackupback.common.R;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/7 16:22
 **/
public interface CompressService {
    R compressByPath(String username, String target, String zipName, String[] source);

    R compressByIds(String username, String target, String zipName, String[] sourceIds);
}
