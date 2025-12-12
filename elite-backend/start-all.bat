@echo off
REM ==============================================================================
REM EliteSchool Backend - Start All Services Script (Windows)
REM ==============================================================================
REM Purpose: Start all backend services using built JAR files
REM Usage: run-all.bat
REM ==============================================================================

setlocal enabledelayedexpansion
title EliteSchool Backend - Run All Services

REM Get script directory
set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

REM Display header
cls
echo ================================================================
echo           EliteSchool Backend - Start All Services
echo ================================================================
echo.
echo [INFO] Script Location: %SCRIPT_DIR%
echo [INFO] Started at: %date% %time%
echo.

REM Check if Java is available
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java is not found in PATH!
    echo [ERROR] Please install Java and add it to your PATH.
    pause
    exit /b 1
)

REM Display Java version
echo [CHECK] Java detected
java -version
echo.

REM Create logs directory
if not exist "%SCRIPT_DIR%\logs" mkdir "%SCRIPT_DIR%\logs"

REM Timestamp for log files
set "TIMESTAMP=%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
set "TIMESTAMP=%TIMESTAMP: =0%"

echo [INFO] Starting services in correct order...
echo.

REM 1. Start Eureka Server (Service Discovery) - Must start first
echo ================================================================
echo [%time%] Starting Eureka Server...
echo ================================================================
if not exist "%SCRIPT_DIR%\eureka-server\target\eureka-server.jar" (
    echo [ERROR] eureka-server.jar not found. Please run build-all.bat first.
    pause
    exit /b 1
)
start "Eureka Server" cmd /k "cd /d "%SCRIPT_DIR%\eureka-server\target" && java -jar eureka-server.jar"
echo [OK] Eureka Server window opened
echo [INFO] Waiting 30 seconds for Eureka to initialize...
timeout /t 30 /nobreak >nul
echo.

REM 2. Start Auth Service
echo ================================================================
echo [%time%] Starting Auth Service...
echo ================================================================
if not exist "%SCRIPT_DIR%\auth-service\target\auth-service.jar" (
    echo [WARNING] auth-service.jar not found, skipping...
) else (
    start "Auth Service" cmd /k "cd /d "%SCRIPT_DIR%\auth-service\target" && java -jar auth-service.jar"
    echo [OK] Auth Service window opened
    echo [INFO] Waiting 15 seconds...
    timeout /t 15 /nobreak >nul
)
echo.

REM 3. Start Task Service
echo ================================================================
echo [%time%] Starting Task Service...
echo ================================================================
if not exist "%SCRIPT_DIR%\task-service\target\task-service.jar" (
    echo [WARNING] task-service.jar not found, skipping...
) else (
    start "Task Service" cmd /k "cd /d "%SCRIPT_DIR%\task-service\target" && java -jar task-service.jar"
    echo [OK] Task Service window opened
    echo [INFO] Waiting 15 seconds...
    timeout /t 15 /nobreak >nul
)
echo.

REM 4. Start Wallet Service
echo ================================================================
echo [%time%] Starting Wallet Service...
echo ================================================================
if not exist "%SCRIPT_DIR%\wallet-service\target\wallet-service.jar" (
    echo [WARNING] wallet-service.jar not found, skipping...
) else (
    start "Wallet Service" cmd /k "cd /d "%SCRIPT_DIR%\wallet-service\target" && java -jar wallet-service.jar"
    echo [OK] Wallet Service window opened
    echo [INFO] Waiting 15 seconds...
    timeout /t 15 /nobreak >nul
)
echo.

REM 5. Start Store Service
echo ================================================================
echo [%time%] Starting Store Service...
echo ================================================================
if not exist "%SCRIPT_DIR%\store-service\target\store-service.jar" (
    echo [WARNING] store-service.jar not found, skipping...
) else (
    start "Store Service" cmd /k "cd /d "%SCRIPT_DIR%\store-service\target" && java -jar store-service.jar"
    echo [OK] Store Service window opened
    echo [INFO] Waiting 15 seconds...
    timeout /t 15 /nobreak >nul
)
echo.

REM 6. Start API Gateway - Must start last
echo ================================================================
echo [%time%] Starting API Gateway...
echo ================================================================
if not exist "%SCRIPT_DIR%\api-gateway\target\api-gateway.jar" (
    echo [ERROR] api-gateway.jar not found. Please run build-all.bat first.
    pause
    exit /b 1
)
start "API Gateway" cmd /k "cd /d "%SCRIPT_DIR%\api-gateway\target" && java -jar api-gateway.jar"
echo [OK] API Gateway window opened
echo [INFO] Waiting 10 seconds...
timeout /t 10 /nobreak >nul
echo.

REM Display summary
echo ================================================================
echo              All Services Started Successfully!
echo ================================================================
echo.
echo Service URLs:
echo   Eureka Dashboard : http://localhost:8761
echo   API Gateway      : http://localhost:8080
echo   Auth Service     : http://localhost:8081
echo   Task Service     : http://localhost:8082
echo   Wallet Service   : http://localhost:8083
echo   Store Service    : http://localhost:8084
echo.
echo [INFO] Each service is running in a separate window
echo [INFO] Close the window or press Ctrl+C to stop a service
echo [INFO] To stop all services at once: stop-all.bat
echo.
echo [INFO] Check Eureka Dashboard to verify all services are registered
echo.
pause
