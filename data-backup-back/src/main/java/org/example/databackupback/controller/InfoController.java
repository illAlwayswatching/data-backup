package org.example.databackupback.controller;

import org.example.databackupback.common.Response;
import org.example.databackupback.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/13 10:08
 **/
@RestController
@CrossOrigin
@RequestMapping("/info")
public class InfoController {
    @Autowired
    private InfoService infoService;

    @GetMapping("/")
    public Response getEntries(String username, String path) {
        System.out.println(username);
        System.out.println(path);
        return infoService.getEntries(username, path);
    }

    @DeleteMapping("/{id}")
    public Response delBackup(@PathVariable("id") Integer id) {
        return infoService.delBackup(id);
    }

    @GetMapping("/addFolder")
    public Response addFolder(String username, String target, String folderName) {
        return infoService.addFolder(username, target, folderName);
    }

    @PostMapping("/copyInServer")
    public Response copyInServer(String username, String fileId, String to) {
        return infoService.copyInServer(username, fileId, to);
    }
}
