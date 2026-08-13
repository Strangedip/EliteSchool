@echo off
REM ==============================================================================
REM EliteSchool Backend - Build All Services Script (Windows)
REM ==============================================================================
REM Purpose : Build all backend services (Maven clean + package)
REM Usage   : build-all.bat
REM
REM Build order (dependencies first):
REM   1. common-utils    → installed to local .m2 (shared library)
REM   2. identity-service    → uses common-utils
REM   3. task-service    → uses common-utils
REM   4. points-service  → uses common-utils
REM   5. rewards-service   → uses common-utils
REM   6. api-gateway     → uses common-utils
REM ==============================================================================

setlocal enabledelayedexpansion
title EliteSchool Backend - Build All Services

set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

if exist "C:\Program Files\Java\jdk-25.0.4" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-25.0.4"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

goto :start_build

REM ==============================================================================
REM FUNCTION: build_service  <display-name>  <relative-path>  <is-util>
REM   is-util = "true"  → mvn install  (puts artifact in local .m2)
REM   is-util = "false" → mvn package  (creates runnable JAR in target/)
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

REM --- Clean ---
echo [PROGRESS] Cleaning...
call "%MVN_CMD%" clean >> "%BUILD_LOG%" 2>&1
if !ERRORLEVEL! neq 0 (
    echo [ERROR] Clean failed for %SERVICE_NAME% — see log: %BUILD_LOG%
    set "FAILED_SERVICES=!FAILED_SERVICES! %SERVICE_NAME%"
    cd /d "%SCRIPT_DIR%"
    exit /b 1
)
echo [OK] Clean successful

REM --- Build ---
if "%IS_UTIL%"=="true" (
    echo [PROGRESS] Installing to local Maven repository...
    call "%MVN_CMD%" install -DskipTests >> "%BUILD_LOG%" 2>&1
    if !ERRORLEVEL! neq 0 (
        echo [ERROR] Install failed for %SERVICE_NAME% — see log: %BUILD_LOG%
        set "FAILED_SERVICES=!FAILED_SERVICES! %SERVICE_NAME%"
        cd /d "%SCRIPT_DIR%"
        exit /b 1
    )
    echo [OK] Install successful
) else (
    echo [PROGRESS] Packaging...
    call "%MVN_CMD%" package -DskipTests >> "%BUILD_LOG%" 2>&1
    if !ERRORLEVEL! neq 0 (
        echo [ERROR] Package failed for %SERVICE_NAME% — see log: %BUILD_LOG%
        set "FAILED_SERVICES=!FAILED_SERVICES! %SERVICE_NAME%"
        cd /d "%SCRIPT_DIR%"
        exit /b 1
    )
    echo [OK] Package successful
)

REM --- Verify JAR ---
set "JAR_FILE=%FULL_PATH%\target\%SERVICE_NAME%.jar"
if exist "%JAR_FILE%" (
    for %%A in ("%JAR_FILE%") do set "JAR_SIZE=%%~zA"
    echo [SUCCESS] %SERVICE_NAME%.jar created ^(!JAR_SIZE! bytes^)
    set /a BUILT_SERVICES+=1
) else (
    if "%IS_UTIL%"=="true" (
        REM Utility libraries don't produce a runnable JAR — that's fine
        echo [OK] Library installed ^(no runnable JAR expected^)
        set /a BUILT_SERVICES+=1
    ) else (
        echo [WARNING] JAR not found at expected path: %JAR_FILE%
        set "FAILED_SERVICES=!FAILED_SERVICES! %SERVICE_NAME%"
    )
)

cd /d "%SCRIPT_DIR%"
echo.
exit /b 0

REM ==============================================================================
REM MAIN EXECUTION
REM ==============================================================================
:start_build

REM --- Locate Maven ---
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    set "MVN_CMD=mvn"
    echo [INFO] Using Maven from system PATH
) else (
    set "MVN_CMD=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9-bin\4nf9hui3q3djbarqar9g711ggc\apache-maven-3.9.9\bin\mvn.cmd"
    if exist "!MVN_CMD!" (
        echo [INFO] Using Maven from wrapper: !MVN_CMD!
    ) else (
        echo [ERROR] Maven not found in PATH or at wrapper fallback location.
        echo [ERROR] Please install Maven or run: mvnw.cmd ^(from a service folder^)
        pause
        exit /b 1
    )
)

REM --- Setup log ---
if not exist "%SCRIPT_DIR%\logs" mkdir "%SCRIPT_DIR%\logs"
set "TIMESTAMP=%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
set "TIMESTAMP=%TIMESTAMP: =0%"
set "BUILD_LOG=%SCRIPT_DIR%\logs\build-%TIMESTAMP%.log"

echo Build started at: %date% %time% > "%BUILD_LOG%"
echo Working directory: %SCRIPT_DIR% >> "%BUILD_LOG%"
echo. >> "%BUILD_LOG%"

cls
echo ================================================================
echo           EliteSchool Backend - Build All Services
echo ================================================================
echo.
echo [INFO] Build started at : %date% %time%
echo [INFO] Build log        : %BUILD_LOG%
echo [INFO] Working dir      : %SCRIPT_DIR%
echo [INFO] Maven            : %MVN_CMD%
echo.

REM Initialize counters
set TOTAL_SERVICES=6
set BUILT_SERVICES=0
set "FAILED_SERVICES="

echo [INFO] Build order: common-utils → services → gateway
echo.

REM 1. Shared library — must install to .m2 before anything else
call :build_service "common-utils"  "common-utils"  "true"
if %ERRORLEVEL% neq 0 goto :build_summary

REM 2. Auth Service
call :build_service "identity-service"  "identity-service"  "false"

REM 3. Task Service
call :build_service "task-service"  "task-service"  "false"

REM 4. Wallet Service
call :build_service "points-service" "points-service" "false"

REM 5. Store Service
call :build_service "rewards-service" "rewards-service" "false"

REM 6. API Gateway — must come last (depends on common-utils)
call :build_service "api-gateway"   "api-gateway"   "false"

REM ==============================================================================
REM BUILD SUMMARY
REM ==============================================================================
:build_summary
echo ================================================================
echo                         BUILD SUMMARY
echo ================================================================
echo.
echo [INFO] Build completed at : %date% %time%
echo [INFO] Services built     : %BUILT_SERVICES% / %TOTAL_SERVICES%
echo [INFO] Full build log     : %BUILD_LOG%
echo.

if %BUILT_SERVICES% EQU %TOTAL_SERVICES% (
    echo ================================================================
    echo                  ALL BUILDS SUCCESSFUL!
    echo ================================================================
    echo.
    echo [SUCCESS] All services built successfully!
    echo [INFO] JARs are in each service's target\ directory
    echo [INFO] To start all services: start-all.bat
    echo.
    if /i not "%SKIP_PAUSE%"=="1" if /i not "%~1"=="/nopause" pause
    exit /b 0
) else (
    echo ================================================================
    echo                  BUILD COMPLETED WITH ERRORS
    echo ================================================================
    echo.
    if defined FAILED_SERVICES (
        echo [FAILED] Services that failed to build:
        echo          %FAILED_SERVICES%
    )
    echo.
    echo [INFO] Check the log for details: %BUILD_LOG%
    echo.
    if /i not "%SKIP_PAUSE%"=="1" if /i not "%~1"=="/nopause" pause
    exit /b 1
)
