package org.example.databackupback.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.databackupback.common.R;
import org.example.databackupback.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/7 0:09
 **/
@RestController
@CrossOrigin
@RequestMapping("/download")
public class DownloadController {
    @Autowired
    private DownloadService downloadService;

    @GetMapping("/file")
    public R downloadFile(String username, String source, HttpServletResponse response) {
        return downloadService.downloadFile(username, source, response);
    }

    @GetMapping("/fileDecrypt")
    public R downloadFileDecrypt(String username, String source, String keyword, HttpServletResponse response) {
        return downloadService.downloadFileDecrypt(username, source, keyword, response);
    }

}
