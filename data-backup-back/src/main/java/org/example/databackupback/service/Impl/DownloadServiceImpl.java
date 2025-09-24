package org.example.databackupback.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.databackupback.common.Response;
import org.example.databackupback.entity.BackupFileInfo;
import org.example.databackupback.mapper.BackupFileInfoMapper;
import org.example.databackupback.service.DownloadService;
import org.example.databackupback.utils.EncryptUtil;
import org.example.databackupback.utils.EncryptLTY;
import org.example.databackupback.utils.EncryptWZA;
import org.example.databackupback.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/11 16:47
 **/
@Slf4j
@Service
public class DownloadServiceImpl implements DownloadService {
    @Autowired
    BackupFileInfoMapper backupFileInfoMapper;

    @Autowired
    FileUtil fileUtil;

    public void setResponse(HttpServletResponse response, File file) throws UnsupportedEncodingException {
        response.reset();
//        response.setContentType("application/octet-stream");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/force-download");      // 设置强制下载不打开
//        response.setContentLength((int) file.length());             // 文件长度
        String fileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name());
        response.addHeader("Content-disposition", "attachment;filename=" + fileName + ";filename*=UTF-8" + fileName);    // 文件名
        response.setHeader("Access-Control-Expose-Headers","Content-Disposition");
    }

    @Override
    public Response downloadFile(String username, String source, HttpServletResponse response) {
        String user_path = "/" + username + source;
        String source_path = Response.USER_DATA + user_path;

        System.out.println(source_path);

        File file = new File(source_path);
        if (!file.exists()) {
            log.error("源文件不存在");
            return Response.error("源文件不存在");
        }
        if (file.isDirectory()) {
            log.error("不能为目录文件");
            return Response.error("不能为目录文件");
        }

        try {
            setResponse(response, file);
        } catch (UnsupportedEncodingException e) {
            log.error("设置返回数据失败");
            return Response.error("设置返回数据失败");
        }

        try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()))) {     // try-with-resource
            byte[] buffer = new byte[4096];
            OutputStream os  = response.getOutputStream();
            System.out.println(os);
            int i = 0;
            while ((i = bis.read(buffer)) != -1) {
                os.write(buffer, 0, i);
                os.flush();
            }
//            OutputStream os  = response.getOutputStream();
//            IOUtils.copy(bis, os);
        } catch (IOException e) {
            log.error("还原失败", e);
            return Response.error("还原失败");
        }

        return null;
    }

    @Override
    public Response downloadFileDecrypt(String username, String source, String keyword, HttpServletResponse response) {
        String user_path = "/" + username + source;
        String source_path = Response.USER_DATA + user_path;

        System.out.println(source_path);

        File file = new File(source_path);
        if (!file.exists()) {
            log.error("源文件不存在");
            return Response.error("源文件不存在");
        }
        if (file.isDirectory()) {
            log.error("不能为目录文件");
            return Response.error("不能为目录文件");
        }
        QueryWrapper<BackupFileInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("path", fileUtil.getPathToStore(file));
        BackupFileInfo info = backupFileInfoMapper.selectOne(wrapper);

        if (info.getKeyword() == null) {
            log.error("不是加密文件");
            return Response.error("不是加密文件");
        }
        if (!info.getKeyword().equals(keyword)) {
            log.error("加密密钥错误");
            return Response.error("加密密钥错误");
        }

        try {
            setResponse(response, file);
        } catch (UnsupportedEncodingException e) {
            log.error("设置返回数据失败");
            return Response.error("设置返回数据失败");
        }

        Path sourcePath = Paths.get(source_path);
        try (InputStream is = Files.newInputStream(sourcePath)) {
            OutputStream os = response.getOutputStream();
            if (info.getAlgorithm().equals("Chacha20")) {
                EncryptLTY.chacha20Decrypt(is, os, keyword, file.getName());
            }
            else if (info.getAlgorithm().equals("Twofish")) {
                EncryptLTY.twofishDecrypt(is, os, keyword, file.getName());
            }
            else if (info.getAlgorithm().equals("Serpent")) {
                EncryptWZA.decrypt("Serpent", is, os, keyword, file.getName());
            }
            else if (info.getAlgorithm().equals("Camellia")) {
                EncryptWZA.decrypt("Camellia", is, os, keyword, file.getName());
            }
            else if (info.getAlgorithm().equals("AES")) {
                EncryptUtil.decryptFile(is, os, keyword, file.getName());
            }
            os.close();
        } catch (Exception e) {
            log.error("文件解密还原失败");
            e.printStackTrace();
            return Response.error("文件解密还原失败");
        }

        return null;
    }
}
