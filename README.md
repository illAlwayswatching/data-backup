# data-backup

大四上软件开发综合实验 — 数据备份软件

## 项目介绍

### 基础功能：
- 数据备份：将目录树中的文件数据保存到指定位置
- 数据还原：将目录树中的文件数据恢复到指定位置

### 扩展功能：
- 打包解包：将所有备份文件拼接为一个大文件保存
- 压缩解压：通过文件压缩节省备份文件的存储空间
- 加密解密：由用户指定密码，将所有备份文件均加密保存
- 图形界面：实现友好易用的GUI界面
- 网络备份：将数据备份软件从单机模式扩展为网盘模式，此外包括传输加密等

## 技术栈

### 后端：
- Spring Boot 3.1.7
- Java 17
- MyBatis Plus 3.5.5
- MySQL 8.0
- Maven

### 前端：
- Vue 3.3.11
- Vite 5.0.10
- Element Plus 2.4.4
- Pinia 2.1.7
- Vue Router 4.2.5
- Axios 1.6.5

## 开发环境要求

- **JDK**: 17 或更高版本
- **Node.js**: 18 或更高版本
- **Maven**: 3.6+ 
- **MySQL**: 8.0 或更高版本
- **Docker** (可选): 用于容器化部署

## VSCode 插件安装

为了在 VSCode/Cursor 中更好地开发本项目，请安装以下插件：

### 必需插件

1. **Extension Pack for Java** (Microsoft)
   - 提供 Java 语言支持、调试、测试等功能
   - 包含：Language Support for Java、Debugger for Java、Test Runner for Java 等

2. **Maven for Java** (Microsoft)
   - Maven 项目管理和构建支持

3. **Vue Language Features (Volar)** (Vue)
   - Vue 3 语法高亮、智能提示、错误检查

4. **TypeScript Vue Plugin (Volar)** (Vue)
   - Vue 文件中的 TypeScript 支持

### 推荐插件

5. **Spring Boot Extension Pack** (VMware)
   - Spring Boot 开发工具集
   - 包含：Spring Boot Tools、Spring Initializr 等

6. **ESLint** (Microsoft)
   - JavaScript/Vue 代码检查

7. **Prettier - Code formatter** (Prettier)
   - 代码格式化工具

8. **Lombok Annotations Support for VS Code** (GabrielBB)
   - Lombok 注解支持

### 安装方法

1. 打开 VSCode/Cursor
2. 按 `Cmd+Shift+X` (Mac) 或 `Ctrl+Shift+X` (Windows/Linux) 打开扩展面板
3. 搜索上述插件名称并安装
4. 安装完成后，按 `Cmd+Shift+P` 输入 "Reload Window" 重新加载窗口

## 项目运行步骤

### 方式一：本地开发运行

#### 1. 克隆项目

```bash
git clone https://github.com/illAlwayswatching/data-backup.git
cd data-backup
```

#### 2. 数据库配置

##### 2.1 安装并启动 MySQL

确保 MySQL 8.0 已安装并运行。

##### 2.2 创建数据库

执行项目根目录下的 `init.sql` 文件：

```bash
mysql -u root -p < init.sql
```

或者手动创建：

```sql
CREATE DATABASE IF NOT EXISTS data_backup CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE data_backup;
-- 然后执行 init.sql 中的表创建语句
```

##### 2.3 配置数据库连接

编辑 `data-backup-back/src/main/resources/application.yaml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/data_backup
    username: root
    password: your_password  # 修改为你的 MySQL 密码
```

#### 3. 后端运行

##### 3.1 进入后端目录

```bash
cd data-backup-back
```

##### 3.2 使用 Maven 安装依赖并运行

不推荐该方法 直接IDE调试即可
```bash
# 安装依赖
mvn clean install

# 运行项目
mvn spring-boot:run
```

或者使用 IDE：
- 打开 `DataBackupBackApplication.java`
- 右键选择 "Run" 或按 `F5` 调试运行

后端服务将在 `http://localhost:8888` 启动。

#### 4. 前端运行

##### 4.1 进入前端目录

```bash
cd data-backup-front
```

##### 4.2 安装依赖

先检查有没有安装 没有再安装
```bash
npm install
```

##### 4.3 启动开发服务器

```bash
npm run dev
```

前端服务将在 `http://localhost:5173` 启动（Vite 默认端口）。

##### 4.4 配置 API 地址（如需要） 可不做

编辑 `data-backup-front/src/utils/request.js`，修改 `BASE_URL`：

```javascript
const BASE_URL = "http://localhost:8888/"  // 后端地址
```

#### 5. 访问应用

打开浏览器访问：`http://localhost:5173`

### 方式二：Docker 容器化部署 暂未实现

#### 1. 确保 Docker 和 Docker Compose 已安装

```bash
docker --version
docker-compose --version
```

#### 2. 修改配置（如需要）

编辑 `docker-compose.yml` 中的数据库密码等配置。

#### 3. 构建并启动所有服务

在项目根目录执行：

```bash
docker-compose up -d --build
```

这将启动：
- MySQL 数据库（端口 3306）
- Spring Boot 后端（端口 8080）
- Vue 前端（端口 80）

#### 4. 查看运行状态

```bash
docker-compose ps
```

#### 5. 查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f db
```

#### 6. 停止服务

```bash
docker-compose down
```

#### 7. 清理数据（包括数据库）

```bash
docker-compose down -v
```

## 项目结构

```
data-backup/
├── data-backup-back/          # Spring Boot 后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── org/example/databackupback/
│   │   │   │       ├── controller/    # 控制器
│   │   │   │       ├── service/       # 业务逻辑
│   │   │   │       ├── mapper/        # 数据访问层
│   │   │   │       ├── entity/        # 实体类
│   │   │   │       └── utils/         # 工具类
│   │   │   └── resources/
│   │   │       └── application.yaml  # 配置文件
│   │   └── test/                      # 测试代码
│   └── pom.xml                        # Maven 配置
├── data-backup-front/          # Vue 前端
│   ├── src/
│   │   ├── components/         # Vue 组件
│   │   ├── views/              # 页面视图
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia 状态管理
│   │   └── utils/              # 工具函数
│   ├── package.json            # 依赖配置
│   └── vite.config.js          # Vite 配置
├── docker-compose.yml          # Docker Compose 配置
├── init.sql                    # 数据库初始化脚本
└── README.md                   # 项目说明文档
```

## 常见问题

### Q: Java 项目无法识别？

A: 
1. 确保已安装 "Extension Pack for Java"
2. 按 `Cmd+Shift+P` 输入 "Java: Clean Java Language Server Workspace"
3. 重新加载窗口

### Q: Maven 依赖下载失败？

A:
1. 检查网络连接
2. 配置 Maven 镜像源（编辑 `~/.m2/settings.xml`）
3. 尝试手动运行 `mvn clean install`

### Q: 前端 npm install 失败？

A:
1. 检查 Node.js 版本（需要 18+）
2. 尝试删除 `node_modules` 和 `package-lock.json` 后重新安装
3. 使用 `npm install --legacy-peer-deps` 安装

### Q: 数据库连接失败？

A:
1. 确认 MySQL 服务已启动
2. 检查 `application.yaml` 中的数据库配置
3. 确认数据库 `data_backup` 已创建
4. 检查 MySQL 用户权限

### Q: 端口被占用？

A:
- 后端端口 8888：修改 `application.yaml` 中的 `server.port`
- 前端端口 5173：修改 `vite.config.js` 或使用 `npm run dev -- --port 3000`
- MySQL 端口 3306：修改 `docker-compose.yml` 或 MySQL 配置

### Q: Lombok 注解不生效？

A:
1. 安装 "Lombok Annotations Support for VS Code" 插件
2. 在 VSCode 设置中启用 Lombok
3. 重启编辑器

## 开发调试

### 后端调试

1. 在 VSCode 中打开 `DataBackupBackApplication.java`
2. 设置断点
3. 按 `F5` 启动调试
4. 或使用命令：`mvn spring-boot:run`

### 前端调试

1. 启动开发服务器：`npm run dev`
2. 在浏览器开发者工具中调试
3. 使用 Vue DevTools 扩展（推荐）

## 构建部署

### 后端打包

```bash
cd data-backup-back
mvn clean package
```

生成的 JAR 文件位于 `target/data-backup-back-0.0.1-SNAPSHOT.jar`

### 前端打包

```bash
cd data-backup-front
npm run build
```
