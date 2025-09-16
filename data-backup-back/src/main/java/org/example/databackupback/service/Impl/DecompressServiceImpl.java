package org.example.databackupback.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.databackupback.common.Response;
import org.example.databackupback.entity.BackupFile;
import org.example.databackupback.entity.BackupFileInfo;
import org.example.databackupback.mapper.BackupFileInfoMapper;
import org.example.databackupback.service.DecompressService;
import org.example.databackupback.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/11 16:49
 **/
@Slf4j
@Service
public class DecompressServiceImpl implements DecompressService {
    @Autowired
    BackupFileInfoMapper backupFileInfoMapper;

    @Autowired
    FileUtil fileUtil;

    @Override
    public Response decompressByPath(String username, String target, String zipPath) {
        File zipFile = new File(Response.USER_DATA + "/" + username + zipPath);

        String targetPath = Response.USER_DATA + "/" + username + target;
        File targetDir = new File(targetPath);
        if (!targetDir.isDirectory()) {
            log.error("欲解压的地址不是一个目录");
            return Response.error("欲解压的地址不是一个目录");
        }

        try {
            decompressHandler(zipFile, targetDir);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("解压失败");
            Response.error("解压失败");
        }

        return Response.success("解压成功");
    }

    @Override
    public Response decompressById(String username, String target, String zipId) {
        BackupFileInfo zipSource = backupFileInfoMapper.selectById(zipId);
        File zipFile = new File(Response.USER_DATA + zipSource.getPath());

        String targetPath = Response.USER_DATA + "/" + username + target;
        File targetDir = new File(targetPath);
        if (!targetDir.isDirectory()) {
            log.error("欲解压的地址不是一个目录");
            return Response.error("欲解压的地址不是一个目录");
        }

        try {
            decompressHandler(zipFile, targetDir);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("解压失败");
            Response.error("解压失败");
        }

        return Response.success("解压成功");
    }

    private void decompressHandler(File zipFile, File targetDir) throws IOException {
        // 读入流
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile.getPath()));
        // 遍历每一个文件
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
            if (zipEntry.isDirectory()) { // 文件夹
                String unzipFilePath = targetDir.getPath() + File.separator + zipEntry.getName();
                // 直接创建
                fileUtil.mkdir(new File(unzipFilePath));
            } else { // 文件
                String unzipFilePath = targetDir.getPath() + File.separator + zipEntry.getName();
                File file = new File(unzipFilePath);
                String pathToStore = fileUtil.getPathToStore(file);

                if (file.exists()) {
                    file.delete();
                    QueryWrapper<BackupFileInfo> wrapper = new QueryWrapper<>();
                    wrapper.eq("path", pathToStore);
                    backupFileInfoMapper.delete(wrapper);
                }

                // 创建父目录
                fileUtil.mkdir(file.getParentFile());
                // 写出文件流
                BufferedOutputStream bufferedOutputStream =
                        new BufferedOutputStream(new FileOutputStream(unzipFilePath));
                byte[] bytes = new byte[1024];
                int readLen;
                while ((readLen = zipInputStream.read(bytes)) != -1) {
                    bufferedOutputStream.write(bytes, 0, readLen);
                }
                bufferedOutputStream.close();

                backupFileInfoMapper.insert(new BackupFileInfo(null, pathToStore, null));
            }
            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }
}
