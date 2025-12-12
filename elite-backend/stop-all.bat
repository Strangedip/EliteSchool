@echo off
REM ==============================================================================
REM EliteSchool Backend - Stop All Services Script (Windows)
REM ==============================================================================
REM Purpose: Stop all running backend services
REM Usage: stop-all.bat
REM ==============================================================================

setlocal enabledelayedexpansion
title EliteSchool Backend - Stop All Services

REM Jump to main execution
goto :start_stop

REM ==============================================================================
REM FUNCTION: stop_port
REM ==============================================================================
:stop_port
set "PORT=%~1"
set "SERVICE_NAME=%~2"
set "FOUND=0"

echo [STOP] Stopping %SERVICE_NAME% on port %PORT%...

for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":%PORT%" ^| findstr "LISTENING"') do (
    set "FOUND=1"
    echo   [ACTION] Killing PID: %%a
    taskkill /F /PID %%a >nul 2>&1
    if !ERRORLEVEL! equ 0 (
        echo   [OK] %SERVICE_NAME% stopped successfully
    ) else (
        echo   [WARNING] Failed to stop PID %%a
    )
)

if "!FOUND!"=="0" (
    echo   [INFO] %SERVICE_NAME% not running on port %PORT%
)
echo.
exit /b 0

REM ==============================================================================
REM MAIN EXECUTION
REM ==============================================================================
:start_stop

cls
echo ================================================================
echo           EliteSchool Backend - Stop All Services
echo ================================================================
echo.
echo [INFO] Stopping services in reverse order...
echo [INFO] Started at: %date% %time%
echo.

REM Stop services in reverse order (gateway first, eureka last)
call :stop_port "8080" "API Gateway"
call :stop_port "8084" "Store Service"
call :stop_port "8083" "Wallet Service"
call :stop_port "8082" "Task Service"
call :stop_port "8081" "Auth Service"
call :stop_port "8761" "Eureka Server"

echo ================================================================
echo                    ALL SERVICES STOPPED
echo ================================================================
echo.
echo [INFO] Completed at: %date% %time%
echo [INFO] All EliteSchool services have been stopped
echo.
pause
exit /b 0
