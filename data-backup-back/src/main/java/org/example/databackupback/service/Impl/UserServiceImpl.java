package org.example.databackupback.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.databackupback.common.R;
import org.example.databackupback.entity.User;
import org.example.databackupback.mapper.UserMapper;
import org.example.databackupback.service.UserService;
import org.example.databackupback.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/6 17:28
 **/
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public R login(String username, String password) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);

        User user = userMapper.selectOne(userQueryWrapper);
        if (user == null){
            return R.error("用户不存在");
        } else if (!SecurityUtil.passwordMatch(password, user.getPassword())) {
            return R.error("密码错误");
        }

        Map<String, Object> res = new HashMap<>();
        res.put("uid", user.getId());

        return R.success("登录成功", res);
    }

    @Override
    public R register(String username, String password) {
        User user = new User(null, username, password);

        user.setPassword(SecurityUtil.encryptPassword(user.getPassword()));
        if (userMapper.exists(new QueryWrapper<User>().eq("username", user.getUsername()))) {
            return R.error("用户名重复，注册失败");
        }

        if (userMapper.insert(user) > 0)
            return R.success("注册成功");

        File initial = new File(R.USER_DATA + "/" + username + "/");
        if (!initial.exists()) initial.mkdirs();

        return R.error("注册失败");
    }

    @Override
    public R delUser(Integer id) {
        if (userMapper.deleteById(id) > 0)
            return R.success("注销成功");

        return R.error("注销失败");
    }
}
