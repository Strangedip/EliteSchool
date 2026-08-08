@echo off
REM EliteSchool Backend - Start All Services (native, no Docker)
REM Needs: Java 25+, Consul on PATH, PostgreSQL with DB EliteSchool, pre-built JARs
REM Order: Consul → seed KV → auth/task/wallet/store → api-gateway

setlocal enabledelayedexpansion
title EliteSchool Backend - Start All Services

set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

REM Prefer JDK 25 when present (PATH may still point at an older JDK)
if exist "C:\Program Files\Java\jdk-25.0.4" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-25.0.4"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

cls
echo ================================================================
echo           EliteSchool Backend - Start All Services
echo ================================================================
echo.
echo [INFO] Script Location : %SCRIPT_DIR%
echo [INFO] Started at      : %date% %time%
if defined JAVA_HOME echo [INFO] JAVA_HOME       : %JAVA_HOME%
echo.

REM --------------------------------------------------------------------------
REM Load environment variables from .env file
REM --------------------------------------------------------------------------
set "ENV_FILE=%SCRIPT_DIR%\..\.env"
if not exist "%ENV_FILE%" set "ENV_FILE=%SCRIPT_DIR%\.env"

if exist "%ENV_FILE%" (
    echo [INFO] Loading environment variables from %ENV_FILE%...
    for /f "usebackq delims=" %%x in ("%ENV_FILE%") do (
        set "line=%%x"
        rem Strip comments and empty lines
        if not "!line:~0,1!"=="#" (
            for /f "tokens=1* delims==" %%a in ("!line!") do (
                set "key=%%a"
                set "val=%%b"
                rem Trim trailing and leading spaces from key and val
                for /f "tokens=* delims= " %%i in ("!key!") do set "key=%%i"
                for /f "tokens=* delims= " %%i in ("!val!") do set "val=%%i"
                if not "!key!"=="" (
                    set "!key!=!val!"
                )
            )
        )
    )
    echo [OK]   Environment loaded successfully.
) else (
    echo [WARN] .env file not found. Falling back to system defaults.
)
echo.

REM --------------------------------------------------------------------------
REM Check Java
REM --------------------------------------------------------------------------
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java is not found in PATH!
    echo [ERROR] Please install Java 25+ and add it to your PATH.
    pause
    exit /b 1
)
echo [CHECK] Java detected:
java -version
echo.

REM --------------------------------------------------------------------------
REM Environment variable defaults (if not set in environment or .env)
REM --------------------------------------------------------------------------
if not defined CONSUL_HOST             set "CONSUL_HOST=localhost"
if not defined CONSUL_PORT             set "CONSUL_PORT=8500"
if not defined SPRING_PROFILES_ACTIVE  set "SPRING_PROFILES_ACTIVE=local"

echo [INFO] Profile         : %SPRING_PROFILES_ACTIVE%
echo [INFO] Consul Address  : %CONSUL_HOST%:%CONSUL_PORT%
echo.

REM --------------------------------------------------------------------------
REM Create logs directory
REM --------------------------------------------------------------------------
if not exist "%SCRIPT_DIR%\logs" mkdir "%SCRIPT_DIR%\logs"

set "TIMESTAMP=%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
set "TIMESTAMP=%TIMESTAMP: =0%"

echo [INFO] Starting services in correct order...
echo.

REM ==========================================================================
REM STEP 1 — Consul Agent (Service Registry + KV Config)   [MUST START FIRST]
REM ==========================================================================
echo ================================================================
echo [%time%] STEP 1/7 — Starting Consul agent (port 8500)...
echo ================================================================
where consul >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] consul executable not found in PATH.
    echo [ERROR] Install it from https://developer.hashicorp.com/consul/downloads
    echo [ERROR] or run "docker compose up" at the repo root instead.
    pause
    exit /b 1
)
start "Consul Agent [8500]" cmd /k "consul agent -dev -client=0.0.0.0"
echo [OK]   Consul Agent window opened
echo [WAIT] Giving Consul 10 seconds to initialize...
timeout /t 10 /nobreak >nul
echo.

REM ==========================================================================
REM STEP 2 — Seed Consul KV with service configuration
REM ==========================================================================
echo ================================================================
echo [%time%] STEP 2/7 — Seeding Consul KV config...
echo ================================================================
call "%SCRIPT_DIR%\consul\seed-kv.bat"
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Failed to seed Consul KV.
    pause
    exit /b 1
)
echo.

REM ==========================================================================
REM STEP 3 — Auth Service
REM ==========================================================================
echo ================================================================
echo [%time%] STEP 3/7 — Starting Auth Service (port 8081)...
echo ================================================================
if not exist "%SCRIPT_DIR%\auth-service\target\auth-service.jar" (
    echo [WARNING] auth-service.jar not found, skipping...
) else (
    start "Auth Service [8081]" cmd /k "cd /d "%SCRIPT_DIR%\auth-service\target" && set CONSUL_HOST=%CONSUL_HOST%&& set CONSUL_PORT=%CONSUL_PORT%&& set SPRING_PROFILES_ACTIVE=%SPRING_PROFILES_ACTIVE%&& java -jar auth-service.jar --spring.profiles.active=%SPRING_PROFILES_ACTIVE%"
    echo [OK]   Auth Service window opened
    echo [WAIT] Waiting 15 seconds...
    timeout /t 15 /nobreak >nul
)
echo.

REM ==========================================================================
REM STEP 4 — Task Service
REM ==========================================================================
echo ================================================================
echo [%time%] STEP 4/7 — Starting Task Service (port 8082)...
echo ================================================================
if not exist "%SCRIPT_DIR%\task-service\target\task-service.jar" (
    echo [WARNING] task-service.jar not found, skipping...
) else (
    start "Task Service [8082]" cmd /k "cd /d "%SCRIPT_DIR%\task-service\target" && set CONSUL_HOST=%CONSUL_HOST%&& set CONSUL_PORT=%CONSUL_PORT%&& set SPRING_PROFILES_ACTIVE=%SPRING_PROFILES_ACTIVE%&& java -jar task-service.jar --spring.profiles.active=%SPRING_PROFILES_ACTIVE%"
    echo [OK]   Task Service window opened
    echo [WAIT] Waiting 15 seconds...
    timeout /t 15 /nobreak >nul
)
echo.

REM ==========================================================================
REM STEP 5 — Wallet Service
REM ==========================================================================
echo ================================================================
echo [%time%] STEP 5/7 — Starting Wallet Service (port 8083)...
echo ================================================================
if not exist "%SCRIPT_DIR%\wallet-service\target\wallet-service.jar" (
    echo [WARNING] wallet-service.jar not found, skipping...
) else (
    start "Wallet Service [8083]" cmd /k "cd /d "%SCRIPT_DIR%\wallet-service\target" && set CONSUL_HOST=%CONSUL_HOST%&& set CONSUL_PORT=%CONSUL_PORT%&& set SPRING_PROFILES_ACTIVE=%SPRING_PROFILES_ACTIVE%&& java -jar wallet-service.jar --spring.profiles.active=%SPRING_PROFILES_ACTIVE%"
    echo [OK]   Wallet Service window opened
    echo [WAIT] Waiting 15 seconds...
    timeout /t 15 /nobreak >nul
)
echo.

REM ==========================================================================
REM STEP 6 — Store Service
REM ==========================================================================
echo ================================================================
echo [%time%] STEP 6/7 — Starting Store Service (port 8084)...
echo ================================================================
if not exist "%SCRIPT_DIR%\store-service\target\store-service.jar" (
    echo [WARNING] store-service.jar not found, skipping...
) else (
    start "Store Service [8084]" cmd /k "cd /d "%SCRIPT_DIR%\store-service\target" && set CONSUL_HOST=%CONSUL_HOST%&& set CONSUL_PORT=%CONSUL_PORT%&& set SPRING_PROFILES_ACTIVE=%SPRING_PROFILES_ACTIVE%&& java -jar store-service.jar --spring.profiles.active=%SPRING_PROFILES_ACTIVE%"
    echo [OK]   Store Service window opened
    echo [WAIT] Waiting 15 seconds...
    timeout /t 15 /nobreak >nul
)
echo.

REM ==========================================================================
REM STEP 7 — API Gateway                         [STARTS LAST]
REM ==========================================================================
echo ================================================================
echo [%time%] STEP 7/7 — Starting API Gateway (port 8080)...
echo ================================================================
if not exist "%SCRIPT_DIR%\api-gateway\target\api-gateway.jar" (
    echo [ERROR] api-gateway.jar not found. Please run build-all.bat first.
    pause
    exit /b 1
)
start "API Gateway [8080]" cmd /k "cd /d "%SCRIPT_DIR%\api-gateway\target" && set CONSUL_HOST=%CONSUL_HOST%&& set CONSUL_PORT=%CONSUL_PORT%&& set SPRING_PROFILES_ACTIVE=%SPRING_PROFILES_ACTIVE%&& java -jar api-gateway.jar --spring.profiles.active=%SPRING_PROFILES_ACTIVE%"
echo [OK]   API Gateway window opened
echo [WAIT] Waiting 10 seconds...
timeout /t 10 /nobreak >nul
echo.

REM ==========================================================================
REM SUMMARY
REM ==========================================================================
echo ================================================================
echo              All Services Started Successfully!
echo ================================================================
echo.
echo   Service              URL
echo   -------              ---
echo   Consul UI         :  http://localhost:8500
echo   API Gateway       :  http://localhost:8080
echo   Auth Service      :  http://localhost:8081
echo   Task Service      :  http://localhost:8082
echo   Wallet Service    :  http://localhost:8083
echo   Store Service     :  http://localhost:8084
echo.
echo [TIP] Open the Consul UI to verify all services are registered and healthy.
echo [TIP] Each service runs in its own CMD window — close it to stop that service.
echo [TIP] To stop everything at once: stop-all.bat
echo.
pause
