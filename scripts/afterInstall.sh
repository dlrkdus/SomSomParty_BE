#!/bin/bash

echo "AfterInstall: Starting the deployment process..."

# 1. AWS ECR 로그인
echo "Logging in to Amazon ECR..."
aws ecr get-login-password --region ap-northeast-2 | sudo docker login --username AWS --password-stdin 084828566517.dkr.ecr.ap-northeast-2.amazonaws.com

# 2. 기존 Docker 컨테이너 중지 및 제거
echo "Stopping and removing existing Docker containers..."
sudo docker stop somparty || true
sudo docker rm somparty || true

# 3. 새 이미지 가져오기
echo "Pulling the latest Docker image from ECR..."
sudo docker pull 084828566517.dkr.ecr.ap-northeast-2.amazonaws.com/somparty:latest

# 4. 새 컨테이너 실행
echo "Running the new Docker container..."
sudo docker run -d -p 8080:8080 --name somparty 084828566517.dkr.ecr.ap-northeast-2.amazonaws.com/somparty:latest

# 5. 애플리케이션 상태 확인
echo "Validating the application health..."
status_code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)
if [ "$status_code" -eq 200 ]; then
  echo "Service is running successfully with status code $status_code."
else
  echo "Service validation failed with status code $status_code."
fi