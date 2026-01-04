#!/bin/bash

echo "=========================================="
echo "Docker 配置验证脚本"
echo "=========================================="

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker daemon 未运行，请先启动 Docker"
    exit 1
fi

echo "✅ Docker daemon 正在运行"

# 检查 docker-compose 配置
echo ""
echo "检查 docker-compose.yml 配置..."
if docker-compose config > /dev/null 2>&1; then
    echo "✅ docker-compose.yml 配置正确"
else
    echo "❌ docker-compose.yml 配置有误"
    docker-compose config
    exit 1
fi

# 检查必要的文件
echo ""
echo "检查必要的文件..."

files=(
    "docker-compose.yml"
    "data-backup-back/Dockerfile"
    "data-backup-front/Dockerfile"
    "data-backup-front/nginx.conf"
    "data-backup-back/src/main/resources/application-docker.yaml"
    "init.sql"
)

for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file 存在"
    else
        echo "❌ $file 不存在"
        exit 1
    fi
done

echo ""
echo "=========================================="
echo "✅ 所有检查通过！"
echo "=========================================="
echo ""
echo "现在可以运行以下命令启动服务："
echo "  docker-compose up -d --build"
echo ""

