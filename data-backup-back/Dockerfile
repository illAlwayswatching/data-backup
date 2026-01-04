# 多阶段构建
FROM maven:3.8.6-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# 下载依赖（利用 Docker 缓存层）
RUN mvn dependency:go-offline -B
# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:17-jre-slim
WORKDIR /app

# 创建数据目录
RUN mkdir -p /app/data

# 复制构建好的 JAR 文件
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8888

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
