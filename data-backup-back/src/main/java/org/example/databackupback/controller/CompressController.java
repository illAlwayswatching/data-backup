package org.example.databackupback.controller;

import org.example.databackupback.common.Response;
import org.example.databackupback.service.CompressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/13 9:46
 **/
@RestController
@CrossOrigin
@RequestMapping("/compress")
public class CompressController {
    @Autowired
    private CompressService compressService;

    @PostMapping("/byPath")
    public Response compressByPath(String username, String target, String zipName, String[] source) {
        return compressService.compressByPath(username, target, zipName, source);
    }

    @PostMapping("/byId")
    public Response compressByIds(String username, String target, String zipName, String[] sourceIds) {
        return compressService.compressByIds(username, target, zipName, sourceIds);
    }
}


