package org.example.databackupback.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.databackupback.common.Response;
import org.example.databackupback.entity.BackupFile;
import org.example.databackupback.entity.BackupFileInfo;
import org.example.databackupback.mapper.BackupFileInfoMapper;
import org.example.databackupback.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/11 16:41
 **/
@Slf4j
@Service
public class InfoServiceImpl implements InfoService {
    @Autowired
    BackupFileInfoMapper backupFileInfoMapper;

    private static final HashSet<String> picPostfix = new HashSet<>(Arrays.asList("xbm", "tif", "pjp", "svgz", "jpg",
            "jpeg", "ico", "tiff", "gif", "svg", "jfif", "webp", "png", "bmp", "pjpeg", "avif"));

    private static final HashSet<String> compressPostfix = new HashSet<>(Arrays.asList("rar", "zip"));

    @Override
    public Response getEntries(String username, String path) {
        File file = new File(Response.USER_DATA + "/" + username + path);

        System.out.println(Response.USER_DATA + "/" + username + path);

        if (!file.isDirectory()) {
            return Response.error("不是目录地址");
        }
        File[] files = file.listFiles();
        if (Objects.isNull(files)) return Response.success("获取成功", null);
        List<BackupFile> list = new ArrayList<>();
        for (File item : files) {
            BackupFile backupFile = new BackupFile();
            backupFile.setName(item.getName());
            Path itemPath = item.toPath();

            // path
            backupFile.setPath(path + item.getName());

            // id
            QueryWrapper<BackupFileInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("path", "/" + username + backupFile.getPath());
            BackupFileInfo info = backupFileInfoMapper.selectOne(wrapper);
            backupFile.setId(info.getId());

            try {
                BasicFileAttributes fatr = Files.readAttributes(itemPath,
                        BasicFileAttributes.class);
                // uploadDate
                backupFile.setUploadDate(new Date(fatr.creationTime().toMillis()));
                // modifyDate
                backupFile.setModifyDate(new Date(fatr.lastModifiedTime().toMillis()));
                // size
                backupFile.setSize(fatr.size());
                // type: 1为目录，2为非图片的文件，3为图片文件
                String filePath = item.getName();
                String postfix = filePath.substring(filePath.lastIndexOf(".") + 1);
                if (item.isDirectory()) backupFile.setType(1);
                else {
                    if (picPostfix.contains(postfix)) backupFile.setType(3);
                    else backupFile.setType(2);
                }

                // isEncrypted
                backupFile.setIsEncrypted(info.getKeyword() != null && !info.getKeyword().isEmpty());

                // isCompressed
                backupFile.setIsCompressed(compressPostfix.contains(postfix));

            } catch (Exception e) {
                return Response.error("获取失败");
            }

            list.add(backupFile);
        }
        return Response.success("获取成功", list);
    }

    @Override
    public Response delBackup(Integer id) {
        BackupFileInfo info = backupFileInfoMapper.selectById(id);
        File toDelete = new File(Response.USER_DATA + info.getPath());

        if (toDelete.isDirectory()) {
            if (Objects.requireNonNull(toDelete.list()).length > 0) return Response.error("目录不为空，拒绝删除");
            else {
                toDelete.delete();
                backupFileInfoMapper.deleteById(id);
            }
        } else {
            toDelete.delete();
            backupFileInfoMapper.deleteById(id);
        }

        return Response.success("成功删除");
    }

    @Override
    public Response addFolder(String username, String path, String folderName) {
        String targetPath = Response.USER_DATA + "/" + username + path;
        String dirPath = targetPath + folderName + "/";

        File target = new File(targetPath);
        if (!target.isDirectory()) {
            log.error("目标位置非目录");
            return Response.error("目标位置非目录");
        }

        File dir = new File(dirPath);
        if (dir.exists()) {
            log.error("同名文件夹已存在");
            return Response.error("同名文件夹已存在");
        }

        dir.mkdir();
        backupFileInfoMapper.insert(new BackupFileInfo(null, "/" + username + path + folderName, null));

        return Response.success("新建文件夹成功");
    }

    @Override
    public Response copyInServer(String username, String fileId, String to) {
        BackupFileInfo info = backupFileInfoMapper.selectById(fileId);

        String source = Response.USER_DATA + info.getPath();
        String target = Response.USER_DATA + "/" + username + to;

        File sourceFile = new File(source);
        if (!sourceFile.exists()) {
            log.error("欲备份的文件不存在");
            return Response.error("欲备份的文件不存在");
        }

        File targetDir = new File(target);
        if (!targetDir.isDirectory()) {
            log.error("目标位置非目录");
            return Response.error("目标位置非目录");
        }

        String final_path = R.USER_DATA + "/" + username + to + sourceFile.getName();
        File finalFile = new File(final_path);
        if (finalFile.exists()) {
            log.error("目标位置有同名文件");
            return Response.error("目标位置有同名文件");
        }

        Path finalPath = Paths.get(final_path);
        try (InputStream is = Files.newInputStream(sourceFile.toPath());
            OutputStream os = Files.newOutputStream(finalPath)) {
            byte[] buffer = new byte[4096]; // 缓冲区大小
            int i;
            while ((i = is.read(buffer)) != -1) { // 读取加密文件内容到缓冲区
                os.write(buffer, 0, i); // 将解密后的数据写入解密文件
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("拷贝文件出错");
            return Response.error("拷贝文件出错");
        }

        if (info.getKeyword() == null || info.getKeyword().isEmpty())
            backupFileInfoMapper.insert(new BackupFileInfo(null, "/" + username + to + sourceFile.getName(), null));
        else
            backupFileInfoMapper.insert(new BackupFileInfo(null, "/" + username + to + sourceFile.getName(), info.getKeyword()));

        return Response.success("拷贝文件成功");
    }
}
