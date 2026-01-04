package org.example.databackupback.entity;

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
    
    // 新增元数据字段
    private String owner;           // 文件所有者
    private String group;           // 文件所属组
    private String permissions;     // 文件权限（如 "rwxr-xr-x"）
    private Integer permissionMode; // 权限模式（八进制，如 755）
    private Boolean isSymbolicLink; // 是否为符号链接
    private Boolean isRegularFile;   // 是否为普通文件
    private Boolean isDirectory;    // 是否为目录
    private String linkTarget;       // 符号链接目标（如果是链接）

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public Boolean getIsRegularFile() {
        return isRegularFile;
    }

    public void setIsRegularFile(Boolean isRegularFile) {
        this.isRegularFile = isRegularFile;
    }

    public Boolean getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(Boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public String getLinkTarget() {
        return linkTarget;
    }

    public void setLinkTarget(String linkTarget) {
        this.linkTarget = linkTarget;
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
                ", owner='" + owner + '\'' +
                ", group='" + group + '\'' +
                ", permissions='" + permissions + '\'' +
                ", permissionMode=" + permissionMode +
                ", isSymbolicLink=" + isSymbolicLink +
                ", isRegularFile=" + isRegularFile +
                ", isDirectory=" + isDirectory +
                ", linkTarget='" + linkTarget + '\'' +
                '}';
    }
}
