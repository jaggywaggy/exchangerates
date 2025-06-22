#!/bin/bash

# Development Setup Script for Exchange Rates API
# This script helps with common development tasks

set -e

echo "🚀 Exchange Rates API - Development Setup"
echo "=========================================="

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check Java installation
echo "📋 Checking prerequisites..."

if ! command_exists java; then
    echo "❌ Java is not installed. Please install Java 21 or higher."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "❌ Java version $JAVA_VERSION detected. Please install Java 21 or higher."
    exit 1
fi

echo "✅ Java $JAVA_VERSION detected"

# Check Docker installation (optional)
if command_exists docker; then
    echo "✅ Docker detected"
    DOCKER_AVAILABLE=true
else
    echo "⚠️  Docker not detected (optional for containerization)"
    DOCKER_AVAILABLE=false
fi

# Function to build the project
build_project() {
    echo "🔨 Building project..."
    ./gradlew clean build
    echo "✅ Build completed successfully"
}

# Function to run tests
run_tests() {
    echo "🧪 Running tests..."
    ./gradlew test
    echo "✅ Tests completed"
}

# Function to start the application
start_app() {
    echo "🚀 Starting application..."
    ./gradlew bootRun
}

# Function to build Docker image
build_docker() {
    if [ "$DOCKER_AVAILABLE" = true ]; then
        echo "🐳 Building Docker image..."
        docker build -t exchangerates .
        echo "✅ Docker image built successfully"
    else
        echo "❌ Docker not available"
    fi
}

# Function to run with Docker Compose
run_docker_compose() {
    if [ "$DOCKER_AVAILABLE" = true ]; then
        echo "🐳 Starting with Docker Compose..."
        docker-compose up --build
    else
        echo "❌ Docker not available"
    fi
}

# Function to show help
show_help() {
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  build     - Build the project"
    echo "  test      - Run tests"
    echo "  start     - Start the application"
    echo "  docker    - Build Docker image"
    echo "  compose   - Run with Docker Compose"
    echo "  all       - Build, test, and start"
    echo "  help      - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 build"
    echo "  $0 test"
    echo "  $0 start"
    echo "  $0 docker"
}

# Main script logic
case "${1:-help}" in
    "build")
        build_project
        ;;
    "test")
        run_tests
        ;;
    "start")
        start_app
        ;;
    "docker")
        build_docker
        ;;
    "compose")
        run_docker_compose
        ;;
    "all")
        build_project
        run_tests
        start_app
        ;;
    "help"|*)
        show_help
        ;;
esac 