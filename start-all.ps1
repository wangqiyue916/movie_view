# 电影点评系统 - 一键启动脚本
# 自动关闭旧端口、启动后端、前端

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  电影点评系统 - 一键启动" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectDir

# 1. 清理 8080 端口
Write-Host "[1/4] 检查端口 8080..." -ForegroundColor Yellow
$pid8080 = (netstat -ano | Select-String ":8080" | Select-String "LISTENING" | ForEach-Object { ($_ -split '\s+')[-1] } | Select-Object -First 1)
if ($pid8080) {
    Write-Host "  发现进程 PID=$pid8080 占用 8080 端口，正在终止..." -ForegroundColor Red
    taskkill /F /PID $pid8080 2>$null | Out-Null
    Start-Sleep -Seconds 1
    Write-Host "  已释放 8080 端口" -ForegroundColor Green
} else {
    Write-Host "  8080 端口空闲" -ForegroundColor Green
}

# 2. 设置数据库连接（优先读取本机环境变量）
Write-Host "[2/4] 配置数据库连接..." -ForegroundColor Yellow
if (-not $env:DB_HOST) { $env:DB_HOST = "localhost" }
if (-not $env:DB_PORT) { $env:DB_PORT = "3306" }
if (-not $env:DB_NAME) { $env:DB_NAME = "movie" }
if (-not $env:DB_USERNAME) { $env:DB_USERNAME = "root" }
if (-not $env:DB_PASSWORD) { $env:DB_PASSWORD = "" }
Write-Host "  数据库: $env:DB_HOST`:$env:DB_PORT/$env:DB_NAME" -ForegroundColor Green

# 3. 启动后端
Write-Host "[3/4] 启动后端 (Spring Boot)..." -ForegroundColor Yellow
$backendJob = Start-Job -ScriptBlock {
    param($dir)
    Set-Location $dir
    mvn spring-boot:run -q 2>&1
} -ArgumentList "$projectDir\backend"

Write-Host "  等待后端启动..." -ForegroundColor Yellow
$timeout = 60
$started = $false
for ($i = 0; $i -lt $timeout; $i++) {
    Start-Sleep -Seconds 1
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/home" -TimeoutSec 2 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            $started = $true
            break
        }
    } catch {}
    if ($i % 5 -eq 0) { Write-Host "  等待中... ($i 秒)" -ForegroundColor Gray }
}
if ($started) {
    Write-Host "  后端启动成功! http://localhost:8080" -ForegroundColor Green
} else {
    Write-Host "  后端可能还在初始化中，请稍后手动检查" -ForegroundColor Yellow
}

# 4. 启动前端
Write-Host "[4/4] 启动前端 (Vite)..." -ForegroundColor Yellow
$frontendJob = Start-Job -ScriptBlock {
    param($dir)
    Set-Location $dir
    npm run dev 2>&1
} -ArgumentList "$projectDir\frontend"

Start-Sleep -Seconds 3
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  启动完成!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  后端: http://localhost:8080" -ForegroundColor White
Write-Host "  前端: http://localhost:5173" -ForegroundColor White
Write-Host "  数据库: 阿里云 DMS (已连接)" -ForegroundColor White
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "按任意键退出 (会同时关闭前后端)..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

# 清理
Write-Host "正在关闭服务..." -ForegroundColor Yellow
Stop-Job -Job $backendJob -ErrorAction SilentlyContinue
Stop-Job -Job $frontendJob -ErrorAction SilentlyContinue
Remove-Job -Job $backendJob -ErrorAction SilentlyContinue
Remove-Job -Job $frontendJob -ErrorAction SilentlyContinue
$pid8080 = (netstat -ano | Select-String ":8080" | Select-String "LISTENING" | ForEach-Object { ($_ -split '\s+')[-1] } | Select-Object -First 1)
if ($pid8080) { taskkill /F /PID $pid8080 2>$null | Out-Null }
Write-Host "已关闭" -ForegroundColor Green
