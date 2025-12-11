# EliteSchool Backend - Stop All Services
# PowerShell Script - Works on Windows, macOS, and Linux (with PowerShell Core)

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   EliteSchool Backend Shutdown Script " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

function Stop-ServiceOnPort {
    param (
        [int]$Port,
        [string]$ServiceName
    )
    
    Write-Host "Stopping $ServiceName (port $Port)..." -ForegroundColor Yellow
    
    if ($IsWindows -or $env:OS -match "Windows") {
        # Windows
        try {
            $connections = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue | Where-Object { $_.State -eq "Listen" }
            foreach ($conn in $connections) {
                $process = Get-Process -Id $conn.OwningProcess -ErrorAction SilentlyContinue
                if ($process) {
                    Write-Host "  Killing: $($process.Name) (PID: $($process.Id))" -ForegroundColor Gray
                    Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue
                }
            }
        }
        catch {
            # Fallback using netstat
            $netstatOutput = netstat -ano | Select-String ":$Port " | Select-String "LISTENING"
            foreach ($line in $netstatOutput) {
                $parts = $line -split '\s+'
                $pid = $parts[-1]
                if ($pid -match '^\d+$') {
                    Write-Host "  Killing PID: $pid" -ForegroundColor Gray
                    Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
                }
            }
        }
    }
    else {
        # macOS / Linux
        try {
            $pid = (lsof -ti :$Port 2>$null)
            if ($pid) {
                Write-Host "  Killing PID: $pid" -ForegroundColor Gray
                kill -9 $pid 2>$null
            }
        }
        catch {
            Write-Host "  No process found on port $Port" -ForegroundColor Gray
        }
    }
    
    Write-Host "  Done." -ForegroundColor Green
}

# Stop services in reverse order
Stop-ServiceOnPort -Port 8080 -ServiceName "API Gateway"
Stop-ServiceOnPort -Port 8084 -ServiceName "Store Service"
Stop-ServiceOnPort -Port 8083 -ServiceName "Wallet Service"
Stop-ServiceOnPort -Port 8082 -ServiceName "Task Service"
Stop-ServiceOnPort -Port 8081 -ServiceName "Auth Service"
Stop-ServiceOnPort -Port 8761 -ServiceName "Eureka Server"

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "   All Services Stopped!               " -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
