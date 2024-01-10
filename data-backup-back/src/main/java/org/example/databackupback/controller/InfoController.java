package org.example.databackupback.controller;

import org.example.databackupback.common.R;
import org.example.databackupback.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/6 17:47
 **/
@RestController
@CrossOrigin
@RequestMapping("/info")
public class InfoController {
    @Autowired
    private InfoService infoService;

    @GetMapping("/")
    public R getEntries(String username, String path) {
        System.out.println(username);
        System.out.println(path);
        return infoService.getEntries(username, path);
    }

    @DeleteMapping("/{id}")
    public R delBackup(@PathVariable("id") Integer id) {
        return infoService.delBackup(id);
    }

    @GetMapping("/addFolder")
    public R addFolder(String username, String target, String folderName) {
        return infoService.addFolder(username, target, folderName);
    }

    @PostMapping("/copyInServer")
    public R copyInServer(String username, String fileId, String to) {
        return infoService.copyInServer(username, fileId, to);
    }
}
