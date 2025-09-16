package org.example.databackupback.service;

import org.example.databackupback.common.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/11 16:21
 **/
public interface UploadService {
    Response uploadFile(String username, String target, MultipartFile file);

    Response uploadFileEncrypt(String username, String target, String keyword, MultipartFile file);

}
