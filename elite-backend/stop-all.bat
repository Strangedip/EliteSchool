@echo off
REM EliteSchool Backend - Stop All Services
REM Windows Batch Script - Stops all services by port

setlocal enabledelayedexpansion
title EliteSchool Backend Shutdown

echo.
echo ========================================
echo    EliteSchool Backend Shutdown Script
echo ========================================
echo.

echo Stopping all services...
echo.

REM Function to kill process on port
call :StopPort 8080 "API Gateway"
call :StopPort 8084 "Store Service"
call :StopPort 8083 "Wallet Service"
call :StopPort 8082 "Task Service"
call :StopPort 8081 "Auth Service"
call :StopPort 8761 "Eureka Server"

echo.
echo ========================================
echo    All Services Stopped!
echo ========================================
echo.
pause
goto :eof

:StopPort
set PORT=%1
set SERVICE_NAME=%~2
echo Stopping %SERVICE_NAME% (port %PORT%)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":%PORT%" ^| findstr "LISTENING"') do (
    echo   Killing PID: %%a
    taskkill /F /PID %%a >nul 2>&1
)
echo   Done.
goto :eof
