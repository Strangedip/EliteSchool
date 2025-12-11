@echo off
REM EliteSchool Backend - Start All Services
REM Windows Batch Script - Works from any directory

setlocal enabledelayedexpansion
title EliteSchool Backend Startup

REM Get the directory where this script is located
set "SCRIPT_DIR=%~dp0"
REM Remove trailing backslash
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

echo.
echo ========================================
echo    EliteSchool Backend Startup Script
echo ========================================
echo.
echo Script Location: %SCRIPT_DIR%
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven ^(mvn^) is not found in PATH!
    echo Please install Maven and add it to your PATH.
    pause
    exit /b 1
)

echo Maven found. Starting services...
echo.

REM Create logs directory
if not exist "%SCRIPT_DIR%\logs" mkdir "%SCRIPT_DIR%\logs"

REM 1. Start Eureka Server (Service Discovery) - Must start first
echo [%time%] Starting Eureka Server...
start "Eureka Server" cmd /k "cd /d "%SCRIPT_DIR%\eureka-server" && echo Starting Eureka Server... && mvn spring-boot:run"
echo   Waiting 25 seconds for Eureka to initialize...
timeout /t 25 /nobreak >nul
echo   Eureka Server started!
echo.

REM 2. Start Auth Service
echo [%time%] Starting Auth Service...
start "Auth Service" cmd /k "cd /d "%SCRIPT_DIR%\auth-service" && echo Starting Auth Service... && mvn spring-boot:run"
echo   Waiting 15 seconds for Auth Service to initialize...
timeout /t 15 /nobreak >nul
echo   Auth Service started!
echo.

REM 3. Start Task Service
echo [%time%] Starting Task Service...
start "Task Service" cmd /k "cd /d "%SCRIPT_DIR%\task-service" && echo Starting Task Service... && mvn spring-boot:run"
echo   Waiting 15 seconds for Task Service to initialize...
timeout /t 15 /nobreak >nul
echo   Task Service started!
echo.

REM 4. Start Wallet Service
echo [%time%] Starting Wallet Service...
start "Wallet Service" cmd /k "cd /d "%SCRIPT_DIR%\wallet-service" && echo Starting Wallet Service... && mvn spring-boot:run"
echo   Waiting 15 seconds for Wallet Service to initialize...
timeout /t 15 /nobreak >nul
echo   Wallet Service started!
echo.

REM 5. Start Store Service
echo [%time%] Starting Store Service...
start "Store Service" cmd /k "cd /d "%SCRIPT_DIR%\store-service" && echo Starting Store Service... && mvn spring-boot:run"
echo   Waiting 15 seconds for Store Service to initialize...
timeout /t 15 /nobreak >nul
echo   Store Service started!
echo.

REM 6. Start API Gateway - Must start last
echo [%time%] Starting API Gateway...
start "API Gateway" cmd /k "cd /d "%SCRIPT_DIR%\api-gateway" && echo Starting API Gateway... && mvn spring-boot:run"
echo   Waiting 10 seconds for API Gateway to initialize...
timeout /t 10 /nobreak >nul
echo   API Gateway started!
echo.

echo ========================================
echo    All Services Started Successfully!
echo ========================================
echo.
echo Service URLs:
echo   Eureka Dashboard : http://localhost:8761
echo   API Gateway      : http://localhost:8080
echo   Auth Service     : http://localhost:8081
echo   Task Service     : http://localhost:8082
echo   Wallet Service   : http://localhost:8083
echo   Store Service    : http://localhost:8084
echo.
echo To stop all services: stop-all.bat
echo.
pause
