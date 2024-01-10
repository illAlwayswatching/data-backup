package org.example.databackupback.service;

import org.example.databackupback.common.R;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/6 17:26
 **/
public interface UserService {

    R login(String username, String password);

    R register(String username, String password);

    R delUser(Integer id);
}