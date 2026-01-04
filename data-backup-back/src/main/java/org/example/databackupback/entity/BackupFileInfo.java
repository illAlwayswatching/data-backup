package org.example.databackupback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

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
    
    // 新增元数据字段
    private String owner;           // 文件所有者
    private String fileGroup;       // 文件所属组（注意：group是SQL关键字，所以用fileGroup）
    private String permissions;     // 文件权限（如 "rwxr-xr-x"）
    private Integer permissionMode; // 权限模式（八进制，如 755）
    private Boolean isSymbolicLink; // 是否为符号链接
    private String linkTarget;       // 符号链接目标路径

    public BackupFileInfo() {
    }

    public BackupFileInfo(Integer id, String path, String keyword, String algorithm) {
        this.id = id;
        this.path = path;
        this.keyword = keyword;
        this.algorithm = algorithm;
    }
    
    public BackupFileInfo(Integer id, String path, String keyword, String algorithm, 
                         String owner, String fileGroup, String permissions, 
                         Integer permissionMode, Boolean isSymbolicLink, String linkTarget) {
        this.id = id;
        this.path = path;
        this.keyword = keyword;
        this.algorithm = algorithm;
        this.owner = owner;
        this.fileGroup = fileGroup;
        this.permissions = permissions;
        this.permissionMode = permissionMode;
        this.isSymbolicLink = isSymbolicLink;
        this.linkTarget = linkTarget;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFileGroup() {
        return fileGroup;
    }

    public void setFileGroup(String fileGroup) {
        this.fileGroup = fileGroup;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public Integer getPermissionMode() {
        return permissionMode;
    }

    public void setPermissionMode(Integer permissionMode) {
        this.permissionMode = permissionMode;
    }

    public Boolean getIsSymbolicLink() {
        return isSymbolicLink;
    }

    public void setIsSymbolicLink(Boolean isSymbolicLink) {
        this.isSymbolicLink = isSymbolicLink;
    }

    public String getLinkTarget() {
        return linkTarget;
    }

    public void setLinkTarget(String linkTarget) {
        this.linkTarget = linkTarget;
    }

    @Override
    public String toString() {
        return "BackupFileInfo{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", keyword='" + keyword + '\'' +
                ", algorithm='" + algorithm + '\'' +
                ", owner='" + owner + '\'' +
                ", fileGroup='" + fileGroup + '\'' +
                ", permissions='" + permissions + '\'' +
                ", permissionMode=" + permissionMode +
                ", isSymbolicLink=" + isSymbolicLink +
                ", linkTarget='" + linkTarget + '\'' +
                '}';
    }
}
