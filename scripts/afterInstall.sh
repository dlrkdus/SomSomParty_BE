#!/bin/bash

set -e  # 에러 발생 시 스크립트 종료

echo "AfterInstall: Starting the deployment process..."

# 1. AWS ECR 로그인
echo "Logging in to Amazon ECR..."
aws ecr get-login-password --region ap-northeast-2 | sudo docker login --username AWS --password-stdin 084828566517.dkr.ecr.ap-northeast-2.amazonaws.com

# 2. 기존 Docker Compose 서비스 중지 및 제거
echo "Stopping and removing existing Docker Compose services..."
sudo docker-compose -f /home/ubuntu/somparty/docker-compose.yml down || true

# 3. Docker Compose 이미지 최신화 (ECR에서 Pull)
echo "Pulling the latest Docker images..."
sudo docker-compose -f /home/ubuntu/somparty/docker-compose.yml pull

# 4. Docker Compose 서비스 재시작
echo "Starting new Docker Compose services..."
sudo docker-compose -f /home/ubuntu/somparty/docker-compose.yml up -d

# 5. Spring Boot 애플리케이션 상태 확인
echo "Validating the Spring Boot application health..."
status_code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health || echo "000")
if [ "$status_code" -eq 200 ]; then
  echo "Spring Boot application is running successfully with status code $status_code."
else
  echo "Spring Boot application validation failed with status code $status_code."
  exit 1
fi