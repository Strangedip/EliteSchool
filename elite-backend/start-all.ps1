# EliteSchool Backend - Start All Services
# PowerShell Script - Works on Windows, macOS, and Linux (with PowerShell Core)

$ErrorActionPreference = "Continue"

# Get the directory where this script is located
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   EliteSchool Backend Startup Script  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Script Location: $ScriptDir" -ForegroundColor Gray
Write-Host ""

# Function to start a service
function Start-EliteService {
    param (
        [string]$ServiceName,
        [string]$ServicePath,
        [int]$WaitSeconds = 15
    )
    
    $FullPath = Join-Path $ScriptDir $ServicePath
    
    if (-not (Test-Path $FullPath)) {
        Write-Host "  ERROR: Path not found - $FullPath" -ForegroundColor Red
        return $false
    }
    
    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Starting $ServiceName..." -ForegroundColor Yellow
    
    # Detect OS and start appropriately
    if ($IsWindows -or $env:OS -match "Windows") {
        # Windows
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$FullPath'; Write-Host 'Starting $ServiceName...' -ForegroundColor Green; mvn spring-boot:run" -WindowStyle Normal
    }
    elseif ($IsMacOS -or $IsLinux) {
        # macOS / Linux - start in background
        $LogDir = Join-Path $ScriptDir "logs"
        if (-not (Test-Path $LogDir)) { New-Item -ItemType Directory -Path $LogDir | Out-Null }
        $LogFile = Join-Path $LogDir "$ServicePath.log"
        
        Push-Location $FullPath
        Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -RedirectStandardOutput $LogFile -RedirectStandardError $LogFile -NoNewWindow
        Pop-Location
        Write-Host "  Log: logs/$ServicePath.log" -ForegroundColor Gray
    }
    else {
        # Fallback for unknown OS
        Push-Location $FullPath
        Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -NoNewWindow
        Pop-Location
    }
    
    Write-Host "  Waiting $WaitSeconds seconds for $ServiceName to initialize..." -ForegroundColor Gray
    Start-Sleep -Seconds $WaitSeconds
    Write-Host "  $ServiceName started!" -ForegroundColor Green
    Write-Host ""
    return $true
}

# Check if Maven is available
$MvnCheck = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $MvnCheck) {
    Write-Host "ERROR: Maven (mvn) is not found in PATH!" -ForegroundColor Red
    Write-Host "Please install Maven and add it to your PATH." -ForegroundColor Red
    exit 1
}

Write-Host "Maven found: $($MvnCheck.Source)" -ForegroundColor Green
Write-Host ""
Write-Host "Starting services in order..." -ForegroundColor Cyan
Write-Host ""

# Start services in order
Start-EliteService -ServiceName "Eureka Server" -ServicePath "eureka-server" -WaitSeconds 25
Start-EliteService -ServiceName "Auth Service" -ServicePath "auth-service" -WaitSeconds 15
Start-EliteService -ServiceName "Task Service" -ServicePath "task-service" -WaitSeconds 15
Start-EliteService -ServiceName "Wallet Service" -ServicePath "wallet-service" -WaitSeconds 15
Start-EliteService -ServiceName "Store Service" -ServicePath "store-service" -WaitSeconds 15
Start-EliteService -ServiceName "API Gateway" -ServicePath "api-gateway" -WaitSeconds 10

Write-Host "========================================" -ForegroundColor Green
Write-Host "   All Services Started Successfully!  " -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Service URLs:" -ForegroundColor Cyan
Write-Host "  Eureka Dashboard : http://localhost:8761" -ForegroundColor White
Write-Host "  API Gateway      : http://localhost:8080" -ForegroundColor White
Write-Host "  Auth Service     : http://localhost:8081" -ForegroundColor White
Write-Host "  Task Service     : http://localhost:8082" -ForegroundColor White
Write-Host "  Wallet Service   : http://localhost:8083" -ForegroundColor White
Write-Host "  Store Service    : http://localhost:8084" -ForegroundColor White
Write-Host ""
Write-Host "To stop all services: .\stop-all.ps1" -ForegroundColor Gray
