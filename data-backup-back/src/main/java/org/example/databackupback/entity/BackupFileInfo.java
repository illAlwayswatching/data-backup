package org.example.databackupback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/10 16:10
 **/
public class BackupFileInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String path;
    private String keyword;
    private String algorithm;

    public BackupFileInfo() {
    }

    public BackupFileInfo(Integer id, String path, String keyword, String algorithm) {
        this.id = id;
        this.path = path;
        this.keyword = keyword;
        this.algorithm = algorithm;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String toString() {
        return "BackupFileInfo{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", keyword='" + keyword + '\'' +
                ", algorithm='" + algorithm + '\'' +
                '}';
    }
}
