package org.example.databackupback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.databackupback.entity.BackupFileInfo;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/6 19:46
 **/
@Mapper
public interface BackupFileInfoMapper extends BaseMapper<BackupFileInfo> {
}
