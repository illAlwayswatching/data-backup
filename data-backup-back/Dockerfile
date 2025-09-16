# 第一阶段：构建
FROM openjdk:17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# 构建项目（跳过测试加快构建）
RUN mvn clean package -DskipTests

# 第二阶段：运行
FROM openjdk:17-slim
WORKDIR /app
# 从 builder 阶段复制 jar 文件
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]