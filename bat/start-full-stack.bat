@echo off
echo 启动沿途路书全栈应用...

echo.
echo 1. 启动后端服务...
start "后端服务" cmd /k "mvn spring-boot:run"

echo.
echo 2. 等待后端启动...
timeout /t 10 /nobreak > nul

echo.
echo 3. 启动前端服务...
start "前端服务" cmd /k "cd frontend && npm run dev"

echo.
echo 应用启动完成！
echo 后端服务: http://localhost:8080
echo 前端服务: http://localhost:3000
echo.
pause
