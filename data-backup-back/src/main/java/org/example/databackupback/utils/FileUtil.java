package org.example.databackupback.utils;

import org.example.databackupback.common.R;
import org.example.databackupback.entity.BackupFileInfo;
import org.example.databackupback.mapper.BackupFileInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/7 18:43
 **/
@Component
public class FileUtil {
    @Autowired
    BackupFileInfoMapper backupFileInfoMapper;

    public void mkdir(File dir) {
        if (null == dir || dir.exists()) {
            return;
        }
        mkdir(dir.getParentFile());    // 递归新建所有路径
        dir.mkdir();
        String pathToStore = getPathToStore(dir);
        System.out.println(pathToStore);
        backupFileInfoMapper.insert(new BackupFileInfo(null, pathToStore, null));
    }

    public String getPathToStore(File file) {
        return file.getAbsolutePath().substring(R.USER_DATA.length()).replace('\\', '/');
//        return file.getAbsolutePath().substring(R.USER_DATA.length());
    }
}
