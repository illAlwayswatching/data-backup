package org.example.databackupback.controller;

import org.example.databackupback.common.R;
import org.example.databackupback.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/6 16:50
 **/
@RestController
@CrossOrigin
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    private UploadService uploadService;

    @PostMapping("/file")
    public R uploadFile(String username, String target, MultipartFile file) {
        return uploadService.uploadFile(username, target, file);
    }

    @PostMapping("/fileEncrypt")
    public R uploadFileEncrypt(String username, String target, String keyword, MultipartFile file) {
        return uploadService.uploadFileEncrypt(username, target, keyword, file);
    }
}
