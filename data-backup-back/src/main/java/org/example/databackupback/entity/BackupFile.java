package org.example.databackupback.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:Gary
 * @ProjectName:data-backup-back
 * @Date: 2024/1/6 16:41
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackupFile {
    private Integer id;
    private String name;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date uploadDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyDate;
    private Long size;
    private String path;
    private Integer type;   // 1为目录，2为非图片的文件，3为图片文件
    private Boolean isEncrypted;
    private Boolean isCompressed;
}
