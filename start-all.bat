@echo off
chcp 65001 >nul
title 电影点评系统 - 一键启动

echo ========================================
echo   电影点评系统 - 一键启动
echo ========================================
echo.

:: 1. 清理 8080 端口
echo [1/3] 检查端口 8080...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo   发现进程 PID=%%a 占用 8080 端口，正在终止...
    taskkill /F /PID %%a >nul 2>&1
    timeout /t 1 /nobreak >nul
)
echo   8080 端口已就绪

:: 2. 设置数据库环境变量
echo [2/3] 配置数据库连接...
set DB_HOST=dpshmy-nsoibg00vy6m3nxv-pub.proxy.dms.aliyuncs.com
set DB_PORT=3306
set DB_NAME=movie
set DB_USERNAME=uQ0sgL2iNbfkEirFiDrsqOZo
set DB_PASSWORD=P71JlvldMkny8yqZ3DUOt0msOqB7JC

:: 3. 启动后端
echo [3/3] 启动后端和前端...
echo.
echo   后端: http://localhost:8080
echo   前端: http://localhost:5173

start "后端-SpringBoot" cmd /c "cd /d %~dp0backend && mvn spring-boot:run"
timeout /t 2 /nobreak >nul
start "前端-Vite" cmd /c "cd /d %~dp0frontend && npm run dev"

timeout /t 8 /nobreak >nul
start http://localhost:5173

echo.
echo ========================================
echo   启动完成! 浏览器已打开前端页面
echo ========================================
echo.
echo   不要关闭弹出的两个命令行窗口
echo   关闭窗口 = 停止服务
echo.
pause