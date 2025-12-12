@echo off
REM ==============================================================================
REM EliteSchool Backend - Run All Services Script (Windows)
REM ==============================================================================
REM Purpose: Run all backend services from built JARs
REM Usage: run-all.bat
REM Prerequisite: Run build-all.bat first
REM ==============================================================================

setlocal enabledelayedexpansion
title EliteSchool Backend - Run All Services

REM Get script directory
set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

REM Create logs directory
if not exist "%SCRIPT_DIR%\logs" mkdir "%SCRIPT_DIR%\logs"

REM Log file with timestamp
for /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c%%a%%b)
for /f "tokens=1-2 delims=/: " %%a in ('time /t') do (set mytime=%%a%%b)
set "RUN_LOG=%SCRIPT_DIR%\logs\run-%mydate%-%mytime%.log"

cls
echo ================================================================
echo            EliteSchool Backend - Run All Services
echo ================================================================
echo.
echo [INFO] Run started at: %date% %time%
echo [INFO] Run log: %RUN_LOG%
echo.

REM Check Java installation
echo [CHECK] Verifying Java installation...
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java is not found in PATH!
    echo [ERROR] Please install Java 21 and add it to your PATH.
    pause
    exit /b 1
)

for /f "tokens=*" %%i in ('java -version 2^>^&1 ^| findstr /C:"version"') do set JAVA_VERSION=%%i
echo [OK] %JAVA_VERSION%
echo.

REM Initialize counters
set STARTED_SERVICES=0
set TOTAL_SERVICES=6

echo [INFO] Startup order: Eureka - Auth - Task - Wallet - Store - Gateway
echo [INFO] All services will run in separate windows
echo.

REM Function to verify JAR
:verify_jar
set SERVICE_NAME=%~1
set JAR_PATH=%SCRIPT_DIR%\%SERVICE_NAME%\target\%SERVICE_NAME%.jar
if not exist "%JAR_PATH%" (
    echo [ERROR] JAR not found: %JAR_PATH%
    echo [HINT] Run build-all.bat first to build all services
    exit /b 1
)
goto :eof

REM Function to start a service
:start_service
set SERVICE_NAME=%~1
set PORT=%~2
set WAIT_SECONDS=%~3
set DESCRIPTION=%~4
set JAR_PATH=%SCRIPT_DIR%\%SERVICE_NAME%\target\%SERVICE_NAME%.jar
set SERVICE_LOG=%SCRIPT_DIR%\logs\%SERVICE_NAME%.log

echo ================================================================
echo  Starting: %DESCRIPTION% ^(%SERVICE_NAME%^)
echo ================================================================

REM Verify JAR exists
if not exist "%JAR_PATH%" (
    echo [ERROR] JAR not found: %JAR_PATH%
    echo [HINT] Run build-all.bat first
    goto :eof
)

echo [START] Port: %PORT%
echo [START] JAR: %JAR_PATH%
echo [START] Log: %SERVICE_LOG%

REM Start service in new window
start "%SERVICE_NAME%" cmd /k "cd /d %SCRIPT_DIR%\%SERVICE_NAME%\target && echo Starting %DESCRIPTION%... && echo Port: %PORT% && echo Log: %SERVICE_LOG% && java -jar %SERVICE_NAME%.jar 2>&1 | tee %SERVICE_LOG%"

echo [STARTED] Service window opened
echo [WAIT] Waiting %WAIT_SECONDS% seconds for initialization...

REM Wait for service to start
timeout /t %WAIT_SECONDS% /nobreak >nul

echo [RUNNING] %SERVICE_NAME% should be running on port %PORT%
echo.

set /a STARTED_SERVICES+=1
goto :eof

REM Start all services
echo ================================================================
echo                    STARTING SERVICES
echo ================================================================
echo.

REM 1. Start Eureka Server
call :start_service "eureka-server" "8761" "30" "Service Discovery"

REM 2. Start Auth Service
call :start_service "auth-service" "8081" "20" "Authentication Service"

REM 3. Start Task Service
call :start_service "task-service" "8082" "20" "Task Management Service"

REM 4. Start Wallet Service
call :start_service "wallet-service" "8083" "20" "Wallet Service"

REM 5. Start Store Service
call :start_service "store-service" "8084" "20" "Store Service"

REM 6. Start API Gateway
call :start_service "api-gateway" "8080" "15" "API Gateway"

REM Summary
echo ================================================================
echo                         STARTUP SUMMARY
echo ================================================================
echo.
echo [INFO] Startup completed at: %date% %time%
echo [INFO] Services started: %STARTED_SERVICES% / %TOTAL_SERVICES%
echo.

if %STARTED_SERVICES% equ %TOTAL_SERVICES% (
    echo ================================================================
    echo              ALL SERVICES STARTED SUCCESSFULLY!
    echo ================================================================
    echo.
    echo [ENDPOINTS] Service URLs:
    echo   * Eureka Dashboard: http://localhost:8761
    echo   * API Gateway:      http://localhost:8080
    echo   * Auth Service:     http://localhost:8081
    echo   * Task Service:     http://localhost:8082
    echo   * Wallet Service:   http://localhost:8083
    echo   * Store Service:    http://localhost:8084
    echo.
    echo [LOGS] Service logs are in: %SCRIPT_DIR%\logs\
    echo [STOP] To stop all services: stop-all.bat
    echo.
    echo [INFO] Each service is running in its own window
    echo [INFO] Close individual windows to stop services
    echo        OR use stop-all.bat to stop all at once
    echo.
) else (
    echo ================================================================
    echo                      STARTUP INCOMPLETE
    echo ================================================================
    echo.
    echo [WARNING] Not all services started successfully
    echo [INFO] Check individual service windows for errors
    echo [INFO] Check logs in: %SCRIPT_DIR%\logs\
    echo [STOP] To stop running services: stop-all.bat
    echo.
)

pause

