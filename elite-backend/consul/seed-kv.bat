@echo off
REM ==============================================================================
REM Pushes every YAML file in .\config into Consul KV at config/<name>/data,
REM which is where spring-cloud-consul-config (format=yaml) expects it.
REM Requires a consul agent already running and the consul CLI on PATH.
REM ==============================================================================

setlocal enabledelayedexpansion
set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"
if not defined CONSUL_HTTP_ADDR set "CONSUL_HTTP_ADDR=http://localhost:8500"

where consul >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] consul executable not found in PATH.
    echo [ERROR] Install Consul from https://developer.hashicorp.com/consul/downloads and retry.
    exit /b 1
)

for %%f in ("%SCRIPT_DIR%\config\*.yml") do (
    echo [SEED] config/%%~nf/data ^<- %%f
    consul kv put "config/%%~nf/data" @"%%f"
)

echo [OK] Consul KV seeded.
exit /b 0
