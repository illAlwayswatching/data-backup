# data-backup
大四上软件开发综合实验 — 数据备份软件

### 项目介绍

---

#### 基础功能：

- 数据备份：将目录树中的文件数据保存到指定位置
- 数据还原：将目录树中的文件数据恢复到指定位置

#### 扩展功能：

- 打包解包：将所有备份文件拼接为一个大文件保存
- 压缩解压：通过文件压缩节省备份文件的存储空间
- 加密解密：由用户指定密码，将所有备份文件均加密保
- 图形界面：实现友好易用的GUI界面
- 网络备份：将数据备份软件从单机模式扩展为网盘模式，此外包括传输加密等

### 技术栈

---

#### 后端：

- 使用springboot2作为主体框架
- 使用mybatis-plus作为连接数据库的框架
- 使用swagger自动生成接口文档

#### 前端用户界面：

- 使用Vue作为主体框架

### 开发环境

---

**JDK：**jdk1.8

### 使用说明

---

1. 使用 Git 下载项目到本地

```
git clone https://github.com/abel-chai/data-backup.git
```

### LICENSE

---

[Apache-2.0.license](https://github.com/abel-chai/data-backup/blob/main/LICENSE)