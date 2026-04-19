@echo off
set /p DB_USER=Enter MySQL username: 
set /p DB_PASS=Enter MySQL password: 


echo Starting Spring Boot Server...
"C:\Program Files\Java\jdk-17\bin\java.exe" -jar build/libs/med_classification-0.0.1-SNAPSHOT.jar ^
  --server.port=9998 ^
  --spring.datasource.url=jdbc:mysql://localhost:3306/med_db ^
  --spring.datasource.username=%DB_USER% ^
  --spring.datasource.password=%DB_PASS%
pause