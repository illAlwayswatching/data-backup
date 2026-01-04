package org.example.databackupback.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.databackupback.common.Response;
import org.example.databackupback.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    public Response downloadFile(String username, String source, HttpServletRequest request, HttpServletResponse response) {
        return downloadService.downloadFile(username, source, request, response);
    }

    @GetMapping("/fileDecrypt")
    public Response downloadFileDecrypt(String username, String source, String keyword, HttpServletRequest request, HttpServletResponse response) {
        return downloadService.downloadFileDecrypt(username, source, keyword, request, response);
    }

    // 处理 OPTIONS 预检请求
    @RequestMapping(value = "/file", method = RequestMethod.OPTIONS)
    public void handleOptionsFile(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        if (origin != null && !origin.isEmpty()) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        } else {
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    @RequestMapping(value = "/fileDecrypt", method = RequestMethod.OPTIONS)
    public void handleOptionsFileDecrypt(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        if (origin != null && !origin.isEmpty()) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        } else {
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

}


    