package org.example.databackupback.service.Impl;


import lombok.extern.slf4j.Slf4j;
import org.example.databackupback.common.Response;
import org.example.databackupback.entity.BackupFileInfo;
import org.example.databackupback.mapper.BackupFileInfoMapper;
import org.example.databackupback.service.UploadService;
import org.example.databackupback.utils.EncryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/11 16:24
 **/
@Slf4j
@Service
public class UploadServiceImpl implements UploadService {
    @Autowired
    BackupFileInfoMapper backupFileInfoMapper;

    @Override
    public Response uploadFile(String username, String target, MultipartFile file) {
        String dir_path = Response.USER_DATA + "/" + username + target;
        String dest_path = dir_path + file.getOriginalFilename();
        String user_path = "/" + username + target + file.getOriginalFilename();

        // 目标备份的目录存在才能进行
        System.out.println(dir_path);
        File dir = new File(dir_path);
        if (!dir.exists()) {
            log.error("目标目录不存在");
            return Response.error("目标目录不存在");
        }

        // 备份
        File dest = new File(dest_path);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("文件备份失败");
            return Response.error("文件备份失败");
        }

        // 添加数据库备份文件的表项
        backupFileInfoMapper.insert(new BackupFileInfo(null, user_path, null));

        return Response.success("备份成功");
    }

    @Override
    public Response uploadFileEncrypt(String username, String target, String keyword, MultipartFile file) {
        String dir_path = Response.USER_DATA + "/" + username + target;
        String dest_path = dir_path + file.getOriginalFilename();
        String user_path = "/" + username + target + file.getOriginalFilename();

        // 目标备份的目录存在才能进行
        System.out.println(dir_path);
        File dir = new File(dir_path);
        if (!dir.exists()) {
            log.error("目标目录不存在");
            return Response.error("目标目录不存在");
        }

        // 加密备份
        Path destPath = Paths.get(dest_path);
        try {
            EncryptUtil.encryptFile(file.getInputStream(), Files.newOutputStream(destPath), keyword);
        } catch (Exception e) {
            log.error("文件加密备份失败");
            e.printStackTrace();
            return Response.error("文件加密备份失败");
        }

        // 添加数据库备份文件的表项
        backupFileInfoMapper.insert(new BackupFileInfo(null, user_path, keyword));

        return Response.success("加密备份成功");
    }
}
