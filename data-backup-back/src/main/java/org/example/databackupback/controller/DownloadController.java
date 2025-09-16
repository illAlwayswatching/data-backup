package org.example.databackupback.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.databackupback.common.Response;
import org.example.databackupback.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/13 10:00
 **/
@RestController
@CrossOrigin
@RequestMapping("/download")
public class DownloadController {
    @Autowired
    private DownloadService downloadService;

    @GetMapping("/file")
    public Response downloadFile(String username, String source, HttpServletResponse response) {
        return downloadService.downloadFile(username, source, response);
    }

    @GetMapping("/fileDecrypt")
    public Response downloadFileDecrypt(String username, String source, String keyword, HttpServletResponse response) {
        return downloadService.downloadFileDecrypt(username, source, keyword, response);
    }

}


    