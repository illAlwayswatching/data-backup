package org.example.databackupback.controller;

import org.example.databackupback.common.Response;
import org.example.databackupback.service.DecompressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/13 09:49
 **/
@RestController
@CrossOrigin
@RequestMapping("/decompress")
public class DecompressController {
    @Autowired
    private DecompressService decompressService;

    @PostMapping("/byPath")
    public Response decompressByPath(String username, String target, String zipPath) {
        return decompressService.decompressByPath(username, target, zipPath);
    }

    @PostMapping("/byId")
    public Response decompressByIds(String username, String target, String zipId) {
        return decompressService.decompressById(username, target, zipId);
    }
}
