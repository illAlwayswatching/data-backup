-- init-db.sql
CREATE DATABASE IF NOT EXISTS data_backup CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE data_backup;

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '加密后的密码',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `backup_file_info` (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    path VARCHAR(512) NOT NULL COMMENT '文件路径',
    keyword VARCHAR(64) NULL COMMENT '关键词/密钥（可为空）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    algorithm VARCHAR(20) NULL COMMENT '加密算法(如AES, DES, 3DES, RSA, ECC, ChaCha20, TwoFish等)',
    owner VARCHAR(100) NULL COMMENT '文件所有者',
    file_group VARCHAR(100) NULL COMMENT '文件所属组',
    permissions VARCHAR(10) NULL COMMENT '文件权限（如 rwxr-xr-x）',
    permission_mode INT NULL COMMENT '权限模式（八进制，如 755）',
    is_symbolic_link BOOLEAN DEFAULT FALSE COMMENT '是否为符号链接',
    link_target VARCHAR(512) NULL COMMENT '符号链接目标路径'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='备份文件信息表';