# Java 项目配置说明

## 已完成的配置

1. ✅ 修复了 `pom.xml` 中的主类配置错误
2. ✅ 创建了 `.vscode/settings.json` 配置文件
3. ✅ 创建了 `.project` 和 `.classpath` Eclipse 项目文件
4. ✅ 配置了 Maven 项目识别

## 解决 "ConfigError: The project 'data-backup-back' is not a valid java project" 的步骤

### 步骤 1: 重新加载 Vscode

1. 按 `Cmd+Shift+P` (Mac) 或 `Ctrl+Shift+P` (Windows/Linux)
2. 输入 "Reload Window" 并执行
3. 或者直接重启 Vscode

### 步骤 2: 安装 Java 扩展
1. 按 `Cmd+Shift+X` (Mac) 或 `Ctrl+Shift+X` (Windows/Linux) 打开扩展面板
2. 搜索并安装以下扩展：
   - **Extension Pack for Java** (由 Microsoft 提供)
   - **Maven for Java** (由 Microsoft 提供)
   - **Spring Boot Extension Pack** (可选，但推荐)

### 步骤 3: 等待项目索引
- Vscode 会自动检测 Maven 项目并开始索引
- 查看右下角的状态栏，等待 "Java Projects" 完成加载
- 这可能需要几分钟时间

### 步骤 4: 验证项目识别
1. 打开 `data-backup-back/pom.xml` 文件
2. 如果看到 Maven 图标或项目结构被识别，说明配置成功
3. 如果仍有问题，尝试：
   - 打开命令面板 (`Cmd+Shift+P`)
   - 输入 "Java: Clean Java Language Server Workspace"
   - 选择并执行，然后重新加载窗口

### 步骤 5: 检查 Java 版本
确保系统已安装 Java 17（项目要求的版本）：
```bash
java -version
```

如果版本不对，需要安装 Java 17。

## 项目结构

```
data-backup/
├── .vscode/              # CCuCurCurs
│   ├── settings.json     # Java 项目设置
│   ├── launch.json      # 调试配置
│   ├── tasks.json       # Maven 任务
│   └── extensions.json  # 推荐扩展
├── data-backup-back/     # Java 后端项目
│   ├── .project         # Eclipse 项目文件（用于项目识别）
│   ├── .classpath       # Eclipse 类路径文件
│   ├── pom.xml          # Maven 配置文件
│   └── src/
└── data-backup-front/    # Vue 前端项目
```

## 常见问题

### Q: 仍然显示 "not a valid java project"
A: 
1. 确保已安装 Java Extension Pack
2. 检查 `data-backup-back` 目录下是否有 `pom.xml` 文件
3. 尝试删除 `.vscode` 目录并重新打开项目
4. 检查 Java Language Server 是否正在运行（查看输出面板）

### Q: Maven 依赖无法下载
A:
1. 检查网络连接
2. 尝试手动运行 `mvn clean install` 命令
3. 检查 Maven 设置文件位置

### Q: Lombok 注解不生效
A:
1. 确保已安装 Lombok 扩展
2. 在设置中启用 Lombok 支持
3. 重启 Vscode

## 调试配置

已配置的调试选项：
- **Launch DataBackupBackApplication**: 启动 Spring Boot 应用
- **Launch Current File**: 运行当前打开的 Java 文件

按 `F5` 开始调试，或使用调试面板选择配置。

## Maven 任务

已配置的 Maven 任务（按 `Cmd+Shift+P` 输入 "Tasks: Run Task"）：
- `maven: clean` - 清理项目
- `maven: compile` - 编译项目
- `maven: package` - 打包项目
- `maven: spring-boot:run` - 运行 Spring Boot 应用
- `maven: test` - 运行测试

