# ============================================
# One-click Start Script - Frontend & Backend
# ============================================

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Movie Review System - Starting..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# ---- Configure paths ----
$NodePath = "C:\Program Files\nodejs"
$MvnPath  = "C:\ProgramData\apache-maven-3.9.9\bin"
$BackendDir  = Join-Path $PSScriptRoot "..\movie-server"
$FrontendDir = Join-Path $PSScriptRoot "..\movie-web"

# ---- Fix PATH for this session ----
$env:Path = "$NodePath;$MvnPath;" + $env:Path

# ---- Check tools ----
$nodeExe = Join-Path $NodePath "node.exe"
$mvnCmd  = Join-Path $MvnPath "mvn.cmd"
$nodeOk = Test-Path $nodeExe
$mvnOk  = Test-Path $mvnCmd

if (-not $nodeOk) {
    Write-Host "[ERROR] Node.js not found at: $NodePath" -ForegroundColor Red
    Write-Host "Please install Node.js or update NodePath in this script." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

if (-not $mvnOk) {
    Write-Host "[ERROR] Maven not found at: $MvnPath" -ForegroundColor Red
    Write-Host "Please install Maven or update MvnPath in this script." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Database connection uses application.yml defaults (remote DMS proxy)
# No local override - let Spring Boot read its own config
$nodeVer = & $nodeExe --version
Write-Host "[OK] Node.js: $nodeVer" -ForegroundColor Green
Write-Host "[OK] Maven: ready" -ForegroundColor Green
Write-Host ""

# ---- 1. Start Backend (Spring Boot) ----
Write-Host ">>> Starting backend (Spring Boot on port 8080)..." -ForegroundColor Yellow
$argList = "-f", (Join-Path $BackendDir "pom.xml"), "spring-boot:run", "-DskipTests"
$backendProc = Start-Process -FilePath $mvnCmd `
    -ArgumentList $argList `
    -PassThru `
    -WindowStyle Minimized

Write-Host "    Backend PID: $($backendProc.Id)" -ForegroundColor Gray
Write-Host "    Waiting for backend to be ready (up to 30s)..." -ForegroundColor Gray

# ---- Wait for backend health check ----
$ready = $false
for ($i = 0; $i -lt 15; $i++) {
    Start-Sleep -Seconds 2
    try {
        $result = Invoke-RestMethod -Uri "http://localhost:8080/api/health" -Method Get -TimeoutSec 3 -ErrorAction Stop
        if ($result.code -eq 0) {
            $ready = $true
            break
        }
    } catch {
        # not ready yet
    }
}

if ($ready) {
    Write-Host "[OK] Backend is ready!" -ForegroundColor Green
} else {
    Write-Host "[WARN] Backend might still be starting, proceeding anyway..." -ForegroundColor Yellow
}
Write-Host ""

# ---- 2. Ensure .env exists ----
$envFile = Join-Path $FrontendDir ".env"
if (-not (Test-Path $envFile)) {
    "VITE_API_BASE_URL=http://localhost:8080/api" | Out-File -FilePath $envFile -Encoding ascii
    Write-Host "[OK] Created frontend/.env" -ForegroundColor Green
}

# ---- 3. Start Frontend (Vite) ----
Write-Host ">>> Starting frontend (Vite)..." -ForegroundColor Yellow
$npmCmd = Join-Path $NodePath "npm.cmd"
$frontendProc = Start-Process -FilePath $npmCmd `
    -ArgumentList "run", "dev" `
    -WorkingDirectory $FrontendDir `
    -PassThru `
    -WindowStyle Minimized

Write-Host "    Frontend PID: $($frontendProc.Id)" -ForegroundColor Gray
Start-Sleep -Seconds 5

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  ALL SERVICES STARTED!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Backend API : http://localhost:8080" -ForegroundColor White
Write-Host "  Frontend    : http://localhost:5173" -ForegroundColor White
Write-Host ""
Write-Host "  Login: admin / 123456" -ForegroundColor White
Write-Host ""
Write-Host "Press Enter to open frontend in browser..." -ForegroundColor Gray
Read-Host | Out-Null

Start-Process "http://localhost:5173"

Write-Host "Browser opened. Close this window to stop." -ForegroundColor Gray
Write-Host "To stop all services, end node.exe and java.exe in Task Manager." -ForegroundColor Gray
Write-Host ""
Read-Host "Press Enter to exit"
