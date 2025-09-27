package org.example.databackupback.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/9/10 15:37
 **/
public class BackupFile {
    private Integer id;
    private String name;
    private Date uploadDate;
    private Date modifyDate;
    private Long size;
    private String path;
    private Integer type;   // 1为目录，2为非图片的文件，3为图片文件
    private Boolean isEncrypted;
    private Boolean isCompressed;

    public BackupFile() {
    }

    public BackupFile(Integer id, String name, Date uploadDate, Date modifyDate, Long size, String path, Integer type, Boolean isEncrypted, Boolean isCompressed) {
        this.id = id;
        this.name = name;
        this.uploadDate = uploadDate;
        this.modifyDate = modifyDate;
        this.size = size;
        this.path = path;
        this.type = type;
        this.isEncrypted = isEncrypted;
        this.isCompressed = isCompressed;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(Boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public Boolean getIsCompressed() {
        return isCompressed;
    }

    public void setIsCompressed(Boolean isCompressed) {
        this.isCompressed = isCompressed;
    }

    @Override
    public String toString() {
        return "BackupFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", uploadDate=" + uploadDate +
                ", modifyDate=" + modifyDate +
                ", size=" + size +
                ", path='" + path + '\'' +
                ", type=" + type +
                ", isEncrypted=" + isEncrypted +
                ", isCompressed=" + isCompressed +
                '}';
    }
}
