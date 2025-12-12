@echo off
REM ==============================================================================
REM EliteSchool Backend - Stop All Services Script (Windows)
REM ==============================================================================
REM Purpose: Stop all running backend services
REM Usage: stop-all.bat
REM ==============================================================================

setlocal enabledelayedexpansion
title EliteSchool Backend - Stop All Services

echo ================================================================
echo           EliteSchool Backend - Stop All Services
echo ================================================================
echo.

REM Function to stop process on port
:stop_port
set PORT=%1
set SERVICE_NAME=%~2
echo [STOP] Stopping %SERVICE_NAME% ^(port %PORT%^)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":%PORT%" ^| findstr "LISTENING"') do (
    echo   -^> Killing PID: %%a
    taskkill /F /PID %%a >nul 2>&1
    if !ERRORLEVEL! equ 0 (
        echo   [OK] %SERVICE_NAME% stopped
    ) else (
        echo   [WARNING] Failed to stop PID %%a
    )
)
goto :eof

echo [INFO] Stopping services by port...
echo.

REM Stop services in reverse order (gateway first, eureka last)
call :stop_port 8080 "API Gateway"
call :stop_port 8084 "Store Service"
call :stop_port 8083 "Wallet Service"
call :stop_port 8082 "Task Service"
call :stop_port 8081 "Auth Service"
call :stop_port 8761 "Eureka Server"

echo.
echo ================================================================
echo                    ALL SERVICES STOPPED
echo ================================================================
echo.
pause

