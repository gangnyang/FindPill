#!/bin/bash

# 사용자 입력 받기
read -p "Enter MySQL username: " DB_USER
read -s -p "Enter MySQL password: " DB_PASS
echo

# JDK 경로 설정 (상대경로 또는 절대경로로 수정 가능)
JAVA_HOME="./jdk-17.0.12"
JAR_FILE="build/libs/med_classification-0.0.1-SNAPSHOT.jar"

# 실행 메시지
echo "Starting Spring Boot Server..."

# 실행
"$JAVA_HOME/bin/java" -jar "$JAR_FILE" \
  --server.port=9998 \
  --spring.datasource.url=jdbc:mysql://localhost:3306/med_db \
  --spring.datasource.username="$DB_USER" \
  --spring.datasource.password="$DB_PASS"
