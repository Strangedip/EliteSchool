@echo off
REM ==============================================================================
REM EliteSchool Backend - Build All Services Script (Windows)
REM ==============================================================================
REM Purpose: Build all backend services starting from common-utils
REM Usage: build-all.bat
REM ==============================================================================

setlocal enabledelayedexpansion
title EliteSchool Backend - Build All Services

REM Get script directory
set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

REM Jump to main execution (skip function definitions)
goto :start_build

REM ==============================================================================
REM FUNCTION: build_service
REM ==============================================================================
:build_service
set "SERVICE_NAME=%~1"
set "SERVICE_PATH=%~2"
set "IS_UTIL=%~3"
set "FULL_PATH=%SCRIPT_DIR%\%SERVICE_PATH%"

if not exist "%FULL_PATH%" (
    echo [ERROR] Directory not found: %FULL_PATH%
    exit /b 1
)

echo ================================================================
echo  Building: %SERVICE_NAME%
echo ================================================================
echo [BUILD] Location: %SERVICE_PATH%

cd /d "%FULL_PATH%"

REM Clean
echo [PROGRESS] Cleaning...
call "%MVN_CMD%" clean >> "%BUILD_LOG%" 2>&1
if !ERRORLEVEL! neq 0 (
    echo [ERROR] Clean failed for %SERVICE_NAME%
    set "FAILED_SERVICES=!FAILED_SERVICES! %SERVICE_NAME%"
    cd /d "%SCRIPT_DIR%"
    exit /b 1
)
echo [OK] Clean successful

REM Build/Install
if "%IS_UTIL%"=="true" (
    echo [PROGRESS] Installing to local repository...
    call "%MVN_CMD%" install -DskipTests >> "%BUILD_LOG%" 2>&1
    if !ERRORLEVEL! neq 0 (
        echo [ERROR] Install failed for %SERVICE_NAME%
        set "FAILED_SERVICES=!FAILED_SERVICES! %SERVICE_NAME%"
        cd /d "%SCRIPT_DIR%"
        exit /b 1
    )
    echo [OK] Install successful
) else (
    echo [PROGRESS] Packaging...
    call "%MVN_CMD%" package -DskipTests >> "%BUILD_LOG%" 2>&1
    if !ERRORLEVEL! neq 0 (
        echo [ERROR] Package failed for %SERVICE_NAME%
        set "FAILED_SERVICES=!FAILED_SERVICES! %SERVICE_NAME%"
        cd /d "%SCRIPT_DIR%"
        exit /b 1
    )
    echo [OK] Package successful
)

REM Verify JAR
set "JAR_FILE=%FULL_PATH%\target\%SERVICE_NAME%.jar"
if exist "%JAR_FILE%" (
    for %%A in ("%JAR_FILE%") do set "JAR_SIZE=%%~zA"
    echo [SUCCESS] %SERVICE_NAME%.jar created ^(!JAR_SIZE! bytes^)
    set /a BUILT_SERVICES+=1
) else (
    echo [WARNING] JAR file not found: %JAR_FILE%
    set "FAILED_SERVICES=!FAILED_SERVICES! %SERVICE_NAME%"
)

cd /d "%SCRIPT_DIR%"
echo.
exit /b 0

REM ==============================================================================
REM MAIN EXECUTION
REM ==============================================================================
:start_build

REM Maven path detection - use system PATH or fallback to wrapper
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    set "MVN_CMD=mvn"
    echo [INFO] Using Maven from system PATH
) else (
    set "MVN_CMD=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9-bin\4nf9hui3q3djbarqar9g711ggc\apache-maven-3.9.9\bin\mvn.cmd"
    if exist "!MVN_CMD!" (
        echo [INFO] Using Maven from wrapper: !MVN_CMD!
    ) else (
        echo [ERROR] Maven not found in PATH or wrapper location
        echo [ERROR] Please install Maven or check wrapper path
        pause
        exit /b 1
    )
)

REM Create logs directory
if not exist "%SCRIPT_DIR%\logs" mkdir "%SCRIPT_DIR%\logs"

REM Log file with timestamp
set "TIMESTAMP=%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
set "TIMESTAMP=%TIMESTAMP: =0%"
set "BUILD_LOG=%SCRIPT_DIR%\logs\build-%TIMESTAMP%.log"

REM Initialize build log
echo Build started at: %date% %time% > "%BUILD_LOG%"
echo Working directory: %SCRIPT_DIR% >> "%BUILD_LOG%"
echo. >> "%BUILD_LOG%"

REM Display header
cls
echo ================================================================
echo           EliteSchool Backend - Build All Services
echo ================================================================
echo.
echo [INFO] Build started at: %date% %time%
echo [INFO] Build log: %BUILD_LOG%
echo [INFO] Working directory: %SCRIPT_DIR%
echo [INFO] Maven: Ready
echo.

REM Initialize counters
set TOTAL_SERVICES=8
set BUILT_SERVICES=0
set "FAILED_SERVICES="

REM Main build sequence
echo [INFO] Build order: common-utils then config-server then eureka then services then gateway
echo.

REM 1. Build common-utils
call :build_service "common-utils" "common-utils" "true"
if %ERRORLEVEL% neq 0 goto :build_summary

REM 2. Build Config Server
call :build_service "config-server" "config-server" "false"

REM 3. Build Eureka Server
call :build_service "eureka-server" "eureka-server" "false"

REM 4. Build Auth Service
call :build_service "auth-service" "auth-service" "false"

REM 5. Build Task Service
call :build_service "task-service" "task-service" "false"

REM 6. Build Wallet Service
call :build_service "wallet-service" "wallet-service" "false"

REM 7. Build Store Service
call :build_service "store-service" "store-service" "false"

REM 8. Build API Gateway
call :build_service "api-gateway" "api-gateway" "false"

REM ==============================================================================
REM BUILD SUMMARY
REM ==============================================================================
:build_summary
echo ================================================================
echo                         BUILD SUMMARY
echo ================================================================
echo.
echo [INFO] Build completed at: %date% %time%
echo [INFO] Services built: %BUILT_SERVICES% / %TOTAL_SERVICES%

if %BUILT_SERVICES% EQU %TOTAL_SERVICES% (
    echo.
    echo ================================================================
    echo                  ALL BUILDS SUCCESSFUL!
    echo ================================================================
    echo.
    echo [SUCCESS] All services built successfully!
    echo [INFO] JARs are located in each service's target\ directory
    echo [INFO] To run services: run-all.bat
    echo.
    echo [INFO] Full build log: %BUILD_LOG%
    echo.
    pause
    exit /b 0
) else (
    echo.
    echo ================================================================
    echo                      BUILD FAILED!
    echo ================================================================
    echo.
    echo [FAILED] The following services failed to build:
    echo          %FAILED_SERVICES%
    echo.
    echo [INFO] Check the build log for details: %BUILD_LOG%
    echo.
    pause
    exit /b 1
)
