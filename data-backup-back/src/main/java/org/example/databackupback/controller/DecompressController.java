package org.example.databackupback.controller;

import org.example.databackupback.common.R;
import org.example.databackupback.service.DecompressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/7 16:21
 **/
@RestController
@CrossOrigin
@RequestMapping("/decompress")
public class DecompressController {
    @Autowired
    private DecompressService decompressService;

    @PostMapping("/byPath")
    public R decompressByPath(String username, String target, String zipPath) {
        return decompressService.decompressByPath(username, target, zipPath);
    }

    @PostMapping("/byId")
    public R decompressByIds(String username, String target, String zipId) {
        return decompressService.decompressById(username, target, zipId);
    }
}
