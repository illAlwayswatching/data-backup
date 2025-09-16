package org.example.databackupback.service;

import org.example.databackupback.common.Response;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2024/9/11 16:04
 **/
public interface UserService {

    Response login(String username, String password);

    Response register(String username, String password);

    Response delUser(Integer id);
}