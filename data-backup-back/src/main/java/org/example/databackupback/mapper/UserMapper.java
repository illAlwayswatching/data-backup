package org.example.databackupback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.databackupback.entity.User;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/6 17:29
 **/
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
