@echo off
setlocal

echo [1/3] Building images...
docker compose build
if errorlevel 1 goto :error

echo [2/3] Starting PostgreSQL, backend and frontend...
docker compose up -d
if errorlevel 1 goto :error

echo [3/3] Service status:
docker compose ps
echo.
echo Open http://localhost:8080
start "" http://localhost:8080
exit /b 0

:error
echo.
echo Startup failed. Run: docker compose logs
exit /b 1

