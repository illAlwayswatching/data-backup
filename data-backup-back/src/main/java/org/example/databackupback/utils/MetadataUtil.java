package org.example.databackupback.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.util.Set;

/**
 * 文件元数据工具类
 * 用于获取和设置文件的权限、所有者等元数据
 * @Author:Aoromandy
 * @ProjectName:data-backup-back
 * @Date: 2025/1/XX
 */
public class MetadataUtil {
    
    /**
     * 获取文件的元数据信息
     */
    public static FileMetadata getFileMetadata(Path filePath) throws IOException {
        FileMetadata metadata = new FileMetadata();
        
        // 获取基本文件属性
        BasicFileAttributes basicAttrs = Files.readAttributes(filePath, BasicFileAttributes.class);
        metadata.setIsDirectory(basicAttrs.isDirectory());
        metadata.setIsRegularFile(basicAttrs.isRegularFile());
        metadata.setIsSymbolicLink(basicAttrs.isSymbolicLink());
        
        // 如果是符号链接，获取链接目标
        if (basicAttrs.isSymbolicLink()) {
            try {
                Path linkTarget = Files.readSymbolicLink(filePath);
                metadata.setLinkTarget(linkTarget.toString());
            } catch (IOException e) {
                // 链接目标可能不存在，忽略错误
                metadata.setLinkTarget(null);
            }
        }
        
        // 尝试获取POSIX文件属性（Linux/Unix系统）
        try {
            PosixFileAttributes posixAttrs = Files.readAttributes(filePath, PosixFileAttributes.class);
            
            // 获取所有者
            UserPrincipal owner = posixAttrs.owner();
            metadata.setOwner(owner.getName());
            
            // 获取所属组
            GroupPrincipal group = posixAttrs.group();
            metadata.setFileGroup(group.getName());
            
            // 获取权限
            Set<PosixFilePermission> permissions = posixAttrs.permissions();
            metadata.setPermissions(formatPermissions(permissions));
            metadata.setPermissionMode(getPermissionMode(permissions));
            
        } catch (UnsupportedOperationException | IOException e) {
            // Windows系统不支持POSIX属性，使用DosFileAttributes
            try {
                DosFileAttributes dosAttrs = Files.readAttributes(filePath, DosFileAttributes.class);
                // Windows系统下，使用文件所有者（如果可用）
                try {
                    UserPrincipal owner = Files.getOwner(filePath);
                    metadata.setOwner(owner.getName());
                } catch (Exception ex) {
                    metadata.setOwner("unknown");
                }
                metadata.setFileGroup("unknown");
                // Windows权限转换为字符串格式
                metadata.setPermissions(formatDosPermissions(dosAttrs));
                metadata.setPermissionMode(null);
            } catch (Exception ex) {
                // 如果都获取失败，设置为默认值
                metadata.setOwner("unknown");
                metadata.setFileGroup("unknown");
                metadata.setPermissions("rw-r--r--");
                metadata.setPermissionMode(644);
            }
        }
        
        return metadata;
    }
    
    /**
     * 设置文件的元数据信息
     */
    public static void setFileMetadata(Path filePath, FileMetadata metadata) throws IOException {
        // 设置文件权限
        if (metadata.getPermissionMode() != null) {
            try {
                Set<PosixFilePermission> permissions = parsePermissions(metadata.getPermissionMode());
                Files.setPosixFilePermissions(filePath, permissions);
            } catch (UnsupportedOperationException e) {
                // Windows系统不支持，忽略
            }
        }
        
        // 设置文件所有者（需要管理员权限）
        if (metadata.getOwner() != null && !metadata.getOwner().equals("unknown")) {
            try {
                UserPrincipalLookupService lookupService = 
                    filePath.getFileSystem().getUserPrincipalLookupService();
                UserPrincipal owner = lookupService.lookupPrincipalByName(metadata.getOwner());
                Files.setOwner(filePath, owner);
            } catch (Exception e) {
                // 权限不足或其他错误，记录日志但不抛出异常
                System.err.println("无法设置文件所有者: " + e.getMessage());
            }
        }
        
        // 设置文件所属组（需要管理员权限）
        if (metadata.getFileGroup() != null && !metadata.getFileGroup().equals("unknown")) {
            try {
                UserPrincipalLookupService lookupService = 
                    filePath.getFileSystem().getUserPrincipalLookupService();
                GroupPrincipal group = lookupService.lookupPrincipalByGroupName(metadata.getFileGroup());
                Files.getFileAttributeView(filePath, PosixFileAttributeView.class)
                     .setGroup(group);
            } catch (Exception e) {
                // 权限不足或其他错误，记录日志但不抛出异常
                System.err.println("无法设置文件所属组: " + e.getMessage());
            }
        }
    }
    
    /**
     * 格式化权限为字符串（如 "rwxr-xr-x"）
     */
    private static String formatPermissions(Set<PosixFilePermission> permissions) {
        StringBuilder sb = new StringBuilder(9);
        sb.append(permissions.contains(PosixFilePermission.OWNER_READ) ? 'r' : '-');
        sb.append(permissions.contains(PosixFilePermission.OWNER_WRITE) ? 'w' : '-');
        sb.append(permissions.contains(PosixFilePermission.OWNER_EXECUTE) ? 'x' : '-');
        sb.append(permissions.contains(PosixFilePermission.GROUP_READ) ? 'r' : '-');
        sb.append(permissions.contains(PosixFilePermission.GROUP_WRITE) ? 'w' : '-');
        sb.append(permissions.contains(PosixFilePermission.GROUP_EXECUTE) ? 'x' : '-');
        sb.append(permissions.contains(PosixFilePermission.OTHERS_READ) ? 'r' : '-');
        sb.append(permissions.contains(PosixFilePermission.OTHERS_WRITE) ? 'w' : '-');
        sb.append(permissions.contains(PosixFilePermission.OTHERS_EXECUTE) ? 'x' : '-');
        return sb.toString();
    }
    
    /**
     * 将权限转换为八进制模式（如 755）
     */
    private static Integer getPermissionMode(Set<PosixFilePermission> permissions) {
        int mode = 0;
        if (permissions.contains(PosixFilePermission.OWNER_READ)) mode |= 0400;
        if (permissions.contains(PosixFilePermission.OWNER_WRITE)) mode |= 0200;
        if (permissions.contains(PosixFilePermission.OWNER_EXECUTE)) mode |= 0100;
        if (permissions.contains(PosixFilePermission.GROUP_READ)) mode |= 0040;
        if (permissions.contains(PosixFilePermission.GROUP_WRITE)) mode |= 0020;
        if (permissions.contains(PosixFilePermission.GROUP_EXECUTE)) mode |= 0010;
        if (permissions.contains(PosixFilePermission.OTHERS_READ)) mode |= 0004;
        if (permissions.contains(PosixFilePermission.OTHERS_WRITE)) mode |= 0002;
        if (permissions.contains(PosixFilePermission.OTHERS_EXECUTE)) mode |= 0001;
        return mode;
    }
    
    /**
     * 将八进制模式转换为权限集合
     */
    private static Set<PosixFilePermission> parsePermissions(Integer mode) {
        Set<PosixFilePermission> permissions = new java.util.HashSet<>();
        if ((mode & 0400) != 0) permissions.add(PosixFilePermission.OWNER_READ);
        if ((mode & 0200) != 0) permissions.add(PosixFilePermission.OWNER_WRITE);
        if ((mode & 0100) != 0) permissions.add(PosixFilePermission.OWNER_EXECUTE);
        if ((mode & 0040) != 0) permissions.add(PosixFilePermission.GROUP_READ);
        if ((mode & 0020) != 0) permissions.add(PosixFilePermission.GROUP_WRITE);
        if ((mode & 0010) != 0) permissions.add(PosixFilePermission.GROUP_EXECUTE);
        if ((mode & 0004) != 0) permissions.add(PosixFilePermission.OTHERS_READ);
        if ((mode & 0002) != 0) permissions.add(PosixFilePermission.OTHERS_WRITE);
        if ((mode & 0001) != 0) permissions.add(PosixFilePermission.OTHERS_EXECUTE);
        return permissions;
    }
    
    /**
     * 格式化Windows DOS权限
     */
    private static String formatDosPermissions(DosFileAttributes attrs) {
        // Windows系统简化处理
        if (attrs.isReadOnly()) {
            return "r--r--r--";
        } else {
            return "rw-rw-rw-";
        }
    }
    
    /**
     * 元数据数据类
     */
    public static class FileMetadata {
        private String owner;
        private String fileGroup;
        private String permissions;
        private Integer permissionMode;
        private Boolean isSymbolicLink;
        private Boolean isRegularFile;
        private Boolean isDirectory;
        private String linkTarget;
        
        // getter/setter方法
        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }
        
        public String getFileGroup() { return fileGroup; }
        public void setFileGroup(String fileGroup) { this.fileGroup = fileGroup; }
        
        public String getPermissions() { return permissions; }
        public void setPermissions(String permissions) { this.permissions = permissions; }
        
        public Integer getPermissionMode() { return permissionMode; }
        public void setPermissionMode(Integer permissionMode) { this.permissionMode = permissionMode; }
        
        public Boolean getIsSymbolicLink() { return isSymbolicLink; }
        public void setIsSymbolicLink(Boolean isSymbolicLink) { this.isSymbolicLink = isSymbolicLink; }
        
        public Boolean getIsRegularFile() { return isRegularFile; }
        public void setIsRegularFile(Boolean isRegularFile) { this.isRegularFile = isRegularFile; }
        
        public Boolean getIsDirectory() { return isDirectory; }
        public void setIsDirectory(Boolean isDirectory) { this.isDirectory = isDirectory; }
        
        public String getLinkTarget() { return linkTarget; }
        public void setLinkTarget(String linkTarget) { this.linkTarget = linkTarget; }
    }
}

