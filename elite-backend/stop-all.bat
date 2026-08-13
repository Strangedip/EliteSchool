@echo off
REM ==============================================================================
REM EliteSchool Backend - Stop All Services Script (Windows)
REM ==============================================================================
REM Purpose : Stop all running backend services by killing their ports
REM Usage   : stop-all.bat
REM
REM Stop order (reverse of startup):
REM   api-gateway → business services → consul agent
REM ==============================================================================

setlocal enabledelayedexpansion
title EliteSchool Backend - Stop All Services

REM Jump to main execution (skip function definition)
goto :start_stop

REM ==============================================================================
REM FUNCTION: stop_port  <port>  <service-name>
REM ==============================================================================
:stop_port
set "PORT=%~1"
set "SERVICE_NAME=%~2"
set "FOUND=0"

echo [STOP] Stopping %SERVICE_NAME% on port %PORT%...

for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":%PORT% " ^| findstr "LISTENING"') do (
    set "FOUND=1"
    echo   [ACTION] Killing PID: %%a
    taskkill /F /PID %%a >nul 2>&1
    if !ERRORLEVEL! equ 0 (
        echo   [OK] %SERVICE_NAME% stopped
    ) else (
        echo   [WARN] Could not kill PID %%a ^(may already be gone^)
    )
)

if "!FOUND!"=="0" (
    echo   [SKIP] %SERVICE_NAME% is not running on port %PORT%
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
echo [INFO] Stopping services in reverse startup order...
echo [INFO] Started at: %date% %time%
echo.

REM Stop in reverse order — gateway first, consul last
call :stop_port "8080" "API Gateway"
call :stop_port "8084" "Rewards Service"
call :stop_port "8083" "Points Service"
call :stop_port "8082" "Task Service"
call :stop_port "8081" "Identity Service"
call :stop_port "8500" "Consul Agent"

echo ================================================================
echo                    ALL SERVICES STOPPED
echo ================================================================
echo.
echo [INFO] Completed at: %date% %time%
echo.
pause
exit /b 0
