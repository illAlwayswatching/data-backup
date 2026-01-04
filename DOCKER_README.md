# Docker 部署指南

## 前置要求

- Docker 20.10+
- Docker Compose 2.0+

## 快速启动

### 1. 构建并启动所有服务

```bash
docker-compose up -d --build
```

这将启动：
- MySQL 数据库（端口 3306）
- Spring Boot 后端（端口 8888）
- Vue 前端（端口 80）

### 2. 查看服务状态

```bash
docker-compose ps
```

### 3. 查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f db
```

### 4. 访问应用

- **前端界面**: http://localhost
- **后端 API**: http://localhost:8888

## 停止服务

```bash
docker-compose down
```

## 清理数据（包括数据库）

```bash
docker-compose down -v
```

## 重新构建

如果修改了代码，需要重新构建：

```bash
# 停止服务
docker-compose down

# 重新构建并启动
docker-compose up -d --build
```

## 验证部署

### 1. 检查数据库

```bash
docker-compose exec db mysql -u appuser -papppassword data_backup -e "SHOW TABLES;"
```

应该看到 `user` 和 `backup_file_info` 两个表。

### 2. 检查后端

```bash
# 查看后端日志
docker-compose logs backend

# 测试后端 API
curl http://localhost:8888/user/login
```

### 3. 检查前端

打开浏览器访问 http://localhost，应该能看到登录页面。

## 常见问题

### 端口被占用

如果 80、3306 或 8888 端口被占用，可以修改 `docker-compose.yml` 中的端口映射：

```yaml
ports:
  - "8080:80"  # 前端改为 8080
  - "3307:3306"  # 数据库改为 3307
  - "8889:8888"  # 后端改为 8889
```

### 数据库连接失败

1. 检查数据库容器是否正常运行：
   ```bash
   docker-compose ps db
   ```

2. 查看数据库日志：
   ```bash
   docker-compose logs db
   ```

3. 确认数据库已初始化：
   ```bash
   docker-compose exec db mysql -u root -prootpassword -e "SHOW DATABASES;"
   ```

### 前端无法连接后端

1. 检查 nginx 配置是否正确
2. 查看前端日志：
   ```bash
   docker-compose logs frontend
   ```
3. 检查后端是否正常运行：
   ```bash
   docker-compose logs backend
   ```

### 文件上传失败

1. 检查后端数据目录权限：
   ```bash
   docker-compose exec backend ls -la /app/data
   ```

2. 确保数据卷已正确挂载

## 数据持久化

所有数据都保存在 Docker volumes 中：
- `db_data`: 数据库数据
- `backend_data`: 用户上传的文件

即使删除容器，数据也不会丢失。要完全清理数据，使用：
```bash
docker-compose down -v
```

