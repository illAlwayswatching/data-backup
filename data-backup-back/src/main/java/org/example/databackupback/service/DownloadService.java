package org.example.databackupback.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.databackupback.common.R;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/7 0:16
 **/
public interface DownloadService {
    R downloadFile(String username, String target, HttpServletResponse response);

    R downloadFileDecrypt(String username, String target, String keyword, HttpServletResponse response);

}
