#!/bin/bash
# ==============================================================================
# EliteSchool Backend - Run All Services Script
# ==============================================================================
# Purpose: Run all backend services from built JARs
# Usage: ./run-all.sh
# Prerequisite: Run ./build-all.sh first
# ==============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$SCRIPT_DIR/logs"
RUN_LOG="$LOG_DIR/run-$(date +%Y%m%d-%H%M%S).log"
PID_FILE="$SCRIPT_DIR/.running-services.pid"

# Create logs directory
mkdir -p "$LOG_DIR"

# Clear PID file
> "$PID_FILE"

# Function to log messages
log() {
    echo -e "$1" | tee -a "$RUN_LOG"
}

# Print header
clear
log "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
log "${CYAN}║            EliteSchool Backend - Run All Services             ║${NC}"
log "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
log ""
log "${BLUE}[INFO]${NC} Run started at: $(date '+%Y-%m-%d %H:%M:%S')"
log "${BLUE}[INFO]${NC} Run log: $RUN_LOG"
log "${BLUE}[INFO]${NC} PID file: $PID_FILE"
log ""

# Check Java installation
log "${YELLOW}[CHECK]${NC} Verifying Java installation..."
if ! command -v java &> /dev/null; then
    log "${RED}[ERROR]${NC} Java is not found in PATH!"
    log "${RED}[ERROR]${NC} Please install Java 21 and add it to your PATH."
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -n 1)
log "${GREEN}[OK]${NC} $JAVA_VERSION"
log ""

# Function to verify JAR exists
verify_jar() {
    local service_name=$1
    local jar_path="$SCRIPT_DIR/$service_name/target/$service_name.jar"
    
    if [ ! -f "$jar_path" ]; then
        log "${RED}[ERROR]${NC} JAR not found: $jar_path"
        log "${YELLOW}[HINT]${NC} Run ./build-all.sh first to build all services"
        return 1
    fi
    return 0
}

# Function to run a service
run_service() {
    local service_name=$1
    local port=$2
    local wait_seconds=$3
    local description=$4
    
    local jar_path="$SCRIPT_DIR/$service_name/target/$service_name.jar"
    local service_log="$LOG_DIR/$service_name.log"
    
    # Verify JAR exists
    if ! verify_jar "$service_name"; then
        return 1
    fi
    
    log "${MAGENTA}╭────────────────────────────────────────────────────────────────╮${NC}"
    log "${MAGENTA}│ Starting: $description ($service_name)${NC}"
    log "${MAGENTA}╰────────────────────────────────────────────────────────────────╯${NC}"
    
    log "${BLUE}[START]${NC} Port: $port"
    log "${BLUE}[START]${NC} JAR: $jar_path"
    log "${BLUE}[START]${NC} Log: $service_log"
    
    # Start the service in background
    nohup java -jar "$jar_path" > "$service_log" 2>&1 &
    local pid=$!
    
    # Save PID
    echo "$service_name:$pid:$port" >> "$PID_FILE"
    
    log "${GREEN}[STARTED]${NC} PID: $pid"
    log "${YELLOW}[WAIT]${NC} Waiting $wait_seconds seconds for initialization..."
    
    # Wait for service to start
    sleep $wait_seconds
    
    # Check if process is still running
    if ps -p $pid > /dev/null 2>&1; then
        log "${GREEN}[RUNNING]${NC} $service_name is running on port $port"
    else
        log "${RED}[FAILED]${NC} $service_name failed to start (PID $pid not found)"
        log "${YELLOW}[INFO]${NC} Check log: $service_log"
        return 1
    fi
    
    log ""
    return 0
}

# Track services
STARTED_SERVICES=0
TOTAL_SERVICES=6

log "${BLUE}[INFO]${NC} Startup order: Eureka → Auth → Task → Wallet → Store → Gateway"
log "${BLUE}[INFO]${NC} All services will run in background"
log ""

# 1. Start Eureka Server (must start first)
if run_service "eureka-server" "8761" 30 "Service Discovery"; then
    ((STARTED_SERVICES++))
fi

# 2. Start Auth Service
if run_service "auth-service" "8081" 20 "Authentication Service"; then
    ((STARTED_SERVICES++))
fi

# 3. Start Task Service
if run_service "task-service" "8082" 20 "Task Management Service"; then
    ((STARTED_SERVICES++))
fi

# 4. Start Wallet Service
if run_service "wallet-service" "8083" 20 "Wallet Service"; then
    ((STARTED_SERVICES++))
fi

# 5. Start Store Service
if run_service "store-service" "8084" 20 "Store Service"; then
    ((STARTED_SERVICES++))
fi

# 6. Start API Gateway (must start last)
if run_service "api-gateway" "8080" 15 "API Gateway"; then
    ((STARTED_SERVICES++))
fi

# Summary
log "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
log "${CYAN}║                         STARTUP SUMMARY                        ║${NC}"
log "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
log ""
log "${BLUE}[INFO]${NC} Startup completed at: $(date '+%Y-%m-%d %H:%M:%S')"
log "${BLUE}[INFO]${NC} Services started: $STARTED_SERVICES / $TOTAL_SERVICES"

if [ $STARTED_SERVICES -eq $TOTAL_SERVICES ]; then
    log ""
    log "${GREEN}╔════════════════════════════════════════════════════════════════╗${NC}"
    log "${GREEN}║              ✓ ALL SERVICES STARTED SUCCESSFULLY! ✓           ║${NC}"
    log "${GREEN}╚════════════════════════════════════════════════════════════════╝${NC}"
    log ""
    log "${CYAN}[ENDPOINTS]${NC} Service URLs:"
    log "${CYAN}  • Eureka Dashboard:${NC} http://localhost:8761"
    log "${CYAN}  • API Gateway:${NC}      http://localhost:8080"
    log "${CYAN}  • Auth Service:${NC}     http://localhost:8081"
    log "${CYAN}  • Task Service:${NC}     http://localhost:8082"
    log "${CYAN}  • Wallet Service:${NC}   http://localhost:8083"
    log "${CYAN}  • Store Service:${NC}    http://localhost:8084"
    log ""
    log "${YELLOW}[LOGS]${NC} Service logs are in: $LOG_DIR/"
    log "${YELLOW}[STOP]${NC} To stop all services: ./stop-all.sh"
    log ""
else
    log ""
    log "${RED}╔════════════════════════════════════════════════════════════════╗${NC}"
    log "${RED}║                  ✗ STARTUP INCOMPLETE! ✗                      ║${NC}"
    log "${RED}╚════════════════════════════════════════════════════════════════╝${NC}"
    log ""
    log "${YELLOW}[WARNING]${NC} Not all services started successfully"
    log "${YELLOW}[INFO]${NC} Check individual service logs in: $LOG_DIR/"
    log "${YELLOW}[INFO]${NC} To stop running services: ./stop-all.sh"
    log ""
fi

# Show running services
log "${BLUE}[INFO]${NC} Running services:"
while IFS=: read -r name pid port; do
    if ps -p $pid > /dev/null 2>&1; then
        log "  ${GREEN}●${NC} $name (PID: $pid, Port: $port)"
    else
        log "  ${RED}●${NC} $name (PID: $pid) - NOT RUNNING"
    fi
done < "$PID_FILE"
log ""

