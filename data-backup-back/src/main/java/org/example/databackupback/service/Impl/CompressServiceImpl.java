package org.example.databackupback.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.databackupback.common.Response;
import org.example.databackupback.entity.BackupFileInfo;
import org.example.databackupback.mapper.BackupFileInfoMapper;
import org.example.databackupback.service.CompressService;
import org.example.databackupback.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/11 16:54
 **/
@Slf4j
@Service
public class CompressServiceImpl implements CompressService {
    @Autowired
    BackupFileInfoMapper backupFileInfoMapper;

    @Autowired
    FileUtil fileUtil;

    @Override
    public Response compressByPath(String username, String target, String zipName, String[] source) {
        if (zipName == null || zipName.isEmpty()) zipName = String.valueOf(System.currentTimeMillis());

        // 源路径处理
        List<File> sourceFile = new ArrayList<>();
        for (String item : source)
            sourceFile.add(new File(Response.USER_DATA + "/" + username + item));

        // 目标路径处理
        String targetPath = Response.USER_DATA + "/" + username + target;
        File targetDir = new File(targetPath);
        if (!targetDir.isDirectory()) {
            log.error("压缩包存放的地址不是一个目录");
            return Response.error("压缩包存放的地址不是一个目录");
        }

        File zipFile = new File(targetPath + zipName + ".zip");

        try {
            compressHandler(sourceFile, zipFile);
        } catch (IOException e) {
            e.printStackTrace();
            if (zipFile.exists()) zipFile.delete();
            log.error("压缩失败");
            return Response.error("压缩失败");
        }

        return Response.success("压缩成功");
    }

    @Override
    public Response compressByIds(String username, String target, String zipName, String[] sourceIds) {
        if (zipName == null || zipName.isEmpty()) zipName = String.valueOf(System.currentTimeMillis());

        List<BackupFileInfo> list = backupFileInfoMapper.selectBatchIds(Arrays.asList(sourceIds));
        log.info(String.valueOf(list));
        List<File> sourceFile = new ArrayList<>();
        for (BackupFileInfo item : list)
            sourceFile.add(new File(Response.USER_DATA + item.getPath()));

        // 目标路径处理
        String targetPath = Response.USER_DATA + "/" + username + target;
        File targetDir = new File(targetPath);
        if (!targetDir.isDirectory()) {
            log.error("压缩包存放的地址不是一个目录");
            return Response.error("压缩包存放的地址不是一个目录");
        }

        File zipFile = new File(targetPath + zipName + ".zip");

        try {
            compressHandler(sourceFile, zipFile);
        } catch (IOException e) {
            e.printStackTrace();
            if (zipFile.exists()) zipFile.delete();
            log.error("压缩失败");
            return Response.error("压缩失败");
        }

        return Response.success("压缩成功");
    }

    private void compressHandler(List<File> sourceFile, File zipFile) throws IOException {
        System.out.println(sourceFile);

        //zip文件不存在，则创建文件，用于压缩
        if(!zipFile.exists())
            zipFile.createNewFile();

        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));

        for (File sourceItem : sourceFile) {
            if (sourceItem == null || !sourceItem.exists())
                continue;

            compressUnit(sourceItem, zos, sourceItem.getName());
        }
        zos.close();

        String PathToStore = fileUtil.getPathToStore(zipFile);
        backupFileInfoMapper.insert(new BackupFileInfo(null, PathToStore, null));
    }

    private void compressUnit(File sourceFile, ZipOutputStream zos, String fileName) throws IOException {
//        log.info(sourceFile.getPath());
        log.info(fileName);
        if (sourceFile.isDirectory()) {
            //创建文件夹
            zos.putNextEntry(new ZipEntry(fileName + "/"));
            //迭代判断，并且加入对应文件路径
            File[] files = sourceFile.listFiles();
            for (File son : Arrays.asList(files)) {
                compressUnit(son, zos, fileName + "/" + son.getName());
            }
        } else {
            String pathToStore = fileUtil.getPathToStore(sourceFile);
//            log.debug(pathToStore);

            QueryWrapper<BackupFileInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("path", pathToStore);
            BackupFileInfo info = backupFileInfoMapper.selectOne(wrapper);

            if (info == null) return;

            if (info.getKeyword() != null) {
                zos.close();
                throw new IOException("试图压缩一个加密文件");
            }

            FileInputStream fis = new FileInputStream(sourceFile);
            zos.putNextEntry(new ZipEntry(fileName));

            int i;
            byte[] buffer = new byte[1024];
            while ((i = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, i);
                zos.flush();
            }

            zos.closeEntry();
            fis.close();
        }
    }

}
