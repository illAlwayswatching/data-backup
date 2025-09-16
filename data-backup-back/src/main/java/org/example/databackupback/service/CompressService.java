package org.example.databackupback.service;

import org.example.databackupback.common.Response;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/11 16:53
 **/
public interface CompressService {
    Response compressByPath(String username, String target, String zipName, String[] source);

    Response compressByIds(String username, String target, String zipName, String[] sourceIds);
}
