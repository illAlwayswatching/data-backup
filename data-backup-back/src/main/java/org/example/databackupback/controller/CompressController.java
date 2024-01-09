package org.example.databackupback.controller;

import org.example.databackupback.common.R;
import org.example.databackupback.service.CompressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/7 16:20
 **/
@RestController
@CrossOrigin
@RequestMapping("/compress")
public class CompressController {
    @Autowired
    private CompressService compressService;

    @PostMapping("/byPath")
    public R compressByPath(String username, String target, String zipName, String[] source) {
        return compressService.compressByPath(username, target, zipName, source);
    }

    @PostMapping("/byId")
    public R compressByIds(String username, String target, String zipName, String[] sourceIds) {
        return compressService.compressByIds(username, target, zipName, sourceIds);
    }
}
