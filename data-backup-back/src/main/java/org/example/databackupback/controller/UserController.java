package org.example.databackupback.controller;

import org.example.databackupback.common.Response;
import org.example.databackupback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/13 10:12
 **/
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public Response login(String username, String password) {
        return userService.login(username, password);
    }

    @PostMapping("/register")
    public Response register(String username, String password) {
        return userService.register(username, password);
    }

    @DeleteMapping("/{id}")
    public Response delUser(@PathVariable("id") Integer id) {
        return userService.delUser(id);
    }
}
