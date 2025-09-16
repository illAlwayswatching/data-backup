package org.example.databackupback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.databackupback.entity.User;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/11 16:11
 **/
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
