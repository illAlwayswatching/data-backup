package org.example.databackupback.service;

import org.example.databackupback.common.R;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/6 17:27
 **/
public interface UploadService {
    R uploadFile(String username, String target, MultipartFile file);

    R uploadFileEncrypt(String username, String target, String keyword, MultipartFile file);

}
