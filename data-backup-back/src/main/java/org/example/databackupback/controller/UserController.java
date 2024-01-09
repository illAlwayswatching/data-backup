package org.example.databackupback.controller;

import org.example.databackupback.common.R;
import org.example.databackupback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/6 16:48
 **/
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public R login(String username, String password) {
        return userService.login(username, password);
    }

    @PostMapping("/register")
    public R register(String username, String password) {
        return userService.register(username, password);
    }

    @DeleteMapping("/{id}")
    public R delUser(@PathVariable("id") Integer id) {
        return userService.delUser(id);
    }
}
