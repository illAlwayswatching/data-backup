package org.example.databackupback.controller;

import org.example.databackupback.common.Response;
import org.example.databackupback.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/13 10:04
 **/
@RestController
@CrossOrigin
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    private UploadService uploadService;

    @PostMapping("/file")
    public Response uploadFile(String username, String target, MultipartFile file) {
        return uploadService.uploadFile(username, target, file);
    }

    @PostMapping("/fileEncrypt")
    public Response uploadFileEncrypt(String username, String target, String keyword, MultipartFile file) {
        return uploadService.uploadFileEncrypt(username, target, keyword, file);
    }

    @PostMapping("/fileEncryptSerpent")
    public Response uploadFileEncryptSerpent(String username, String target, String keyword, MultipartFile file) {
        return uploadService.uploadFileEncryptSerpent(username, target, keyword, file);
    }

    @PostMapping("/fileEncryptChacha20")
    public Response uploadFileEncryptChacha20(String username, String target, String keyword, MultipartFile file) {
        return uploadService.uploadFileEncryptChacha20(username, target, keyword, file);
    }

    @PostMapping("/fileEncryptTwofish")
    public Response uploadFileEncryptTwofish(String username, String target, String keyword, MultipartFile file) {
        return uploadService.uploadFileEncryptTwofish(username, target, keyword, file);
    }

    @PostMapping("/fileEncryptCamellia")
    public Response uploadFileEncryptCamellia(String username, String target, String keyword, MultipartFile file) {
        return uploadService.uploadFileEncryptCamellia(username, target, keyword, file);
    }
}
