@echo off
setlocal enabledelayedexpansion

echo 🚀 Exchange Rates API - Development Setup
echo ==========================================

REM Check Java installation
echo 📋 Checking prerequisites...

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed. Please install Java 21 or higher.
    exit /b 1
)

echo ✅ Java detected

REM Check Docker installation (optional)
docker --version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Docker detected
    set DOCKER_AVAILABLE=true
) else (
    echo ⚠️  Docker not detected (optional for containerization)
    set DOCKER_AVAILABLE=false
)

REM Function to build the project
:build_project
echo 🔨 Building project...
call gradlew.bat clean build
if %errorlevel% equ 0 (
    echo ✅ Build completed successfully
) else (
    echo ❌ Build failed
    exit /b 1
)
goto :eof

REM Function to run tests
:run_tests
echo 🧪 Running tests...
call gradlew.bat test
if %errorlevel% equ 0 (
    echo ✅ Tests completed
) else (
    echo ❌ Tests failed
    exit /b 1
)
goto :eof

REM Function to start the application
:start_app
echo 🚀 Starting application...
call gradlew.bat bootRun
goto :eof

REM Function to build Docker image
:build_docker
if "%DOCKER_AVAILABLE%"=="true" (
    echo 🐳 Building Docker image...
    docker build -t exchangerates .
    if %errorlevel% equ 0 (
        echo ✅ Docker image built successfully
    ) else (
        echo ❌ Docker build failed
    )
) else (
    echo ❌ Docker not available
)
goto :eof

REM Function to run with Docker Compose
:run_docker_compose
if "%DOCKER_AVAILABLE%"=="true" (
    echo 🐳 Starting with Docker Compose...
    docker-compose up --build
) else (
    echo ❌ Docker not available
)
goto :eof

REM Function to show help
:show_help
echo Usage: %0 [COMMAND]
echo.
echo Commands:
echo   build     - Build the project
echo   test      - Run tests
echo   start     - Start the application
echo   docker    - Build Docker image
echo   compose   - Run with Docker Compose
echo   all       - Build, test, and start
echo   help      - Show this help message
echo.
echo Examples:
echo   %0 build
echo   %0 test
echo   %0 start
echo   %0 docker
goto :eof

REM Main script logic
if "%1"=="" goto show_help
if "%1"=="help" goto show_help
if "%1"=="build" goto build_project
if "%1"=="test" goto run_tests
if "%1"=="start" goto start_app
if "%1"=="docker" goto build_docker
if "%1"=="compose" goto run_docker_compose
if "%1"=="all" (
    call :build_project
    call :run_tests
    call :start_app
    goto :eof
)

echo Unknown command: %1
goto show_help 