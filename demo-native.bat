@echo off
REM Native demo helper for 16GB hosts (no Docker).
setlocal
title EliteSchool - Native Demo

set "ROOT=%~dp0"
if "%ROOT:~-1%"=="\" set "ROOT=%ROOT:~0,-1%"

if exist "C:\Program Files\Java\jdk-25.0.4" (
  set "JAVA_HOME=C:\Program Files\Java\jdk-25.0.4"
  set "PATH=%JAVA_HOME%\bin;%PATH%"
)

echo.
echo EliteSchool native demo
echo.

where consul >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Consul is not on PATH.
  echo Install: https://developer.hashicorp.com/consul/downloads
  echo Add consul.exe to PATH, then re-run this script.
  echo PostgreSQL service looks fine if "postgresql-x64-*" is Running.
  pause
  exit /b 1
)

echo 1^) Ensure DB name EliteSchool exists in PostgreSQL
echo 2^) Building backend...
echo.

cd /d "%ROOT%\elite-backend"
call build-all.bat /nopause
if errorlevel 1 (
  echo Build failed.
  pause
  exit /b 1
)

echo.
echo 3^) Starting backend ^(Consul + services + gateway^)...
start "EliteSchool Backend" cmd /k "cd /d \"%ROOT%\elite-backend\" && start-all.bat"

echo.
echo 4^) Starting Angular ^(http://localhost:4200^)...
cd /d "%ROOT%\elite-frontend"
if not exist "node_modules\" (
  echo Installing npm dependencies...
  call npm install
)
start "EliteSchool Frontend" cmd /k "npm start"

echo.
echo When ready: open http://localhost:4200
echo Login: admin / Admin@123
echo.
pause
endlocal
