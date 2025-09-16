package org.example.databackupback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.databackupback.entity.BackupFileInfo;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/12 23:19
 **/
@Mapper
public interface BackupFileInfoMapper extends BaseMapper<BackupFileInfo> {
}
