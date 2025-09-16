package org.example.databackupback.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.databackupback.common.Response;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/11 16:46
 **/
public interface DownloadService {
    Response downloadFile(String username, String target, HttpServletResponse response);

    Response downloadFileDecrypt(String username, String target, String keyword, HttpServletResponse response);

}
