#!/bin/bash
# EliteSchool Backend - Start All Services
# Cross-platform shell script for Unix/macOS/Linux

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Get the directory where this script is located (works regardless of where it's called from)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo ""
echo -e "${CYAN}========================================"
echo "   EliteSchool Backend Startup Script"
echo -e "========================================${NC}"
echo ""

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}ERROR: Maven (mvn) is not found in PATH!${NC}"
    echo "Please install Maven and add it to your PATH."
    exit 1
fi

echo -e "${GREEN}Maven found: $(which mvn)${NC}"
echo ""

# Function to start a service
start_service() {
    local service_name=$1
    local service_path=$2
    local wait_seconds=$3
    
    local full_path="$SCRIPT_DIR/$service_path"
    
    if [ ! -d "$full_path" ]; then
        echo -e "${RED}ERROR: Path not found - $full_path${NC}"
        return 1
    fi
    
    echo -e "${YELLOW}[$(date '+%H:%M:%S')] Starting $service_name...${NC}"
    
    # Start service in background, redirect output to log file
    cd "$full_path"
    nohup mvn spring-boot:run > "$SCRIPT_DIR/logs/${service_path}.log" 2>&1 &
    
    echo "  PID: $!"
    echo "  Log: logs/${service_path}.log"
    echo -e "  Waiting $wait_seconds seconds for $service_name to initialize..."
    sleep $wait_seconds
    
    echo -e "${GREEN}  $service_name started!${NC}"
    echo ""
    
    cd "$SCRIPT_DIR"
}

# Create logs directory
mkdir -p "$SCRIPT_DIR/logs"

echo -e "${CYAN}Starting services in order...${NC}"
echo ""

# 1. Eureka Server (Service Discovery) - Must start first
start_service "Eureka Server" "eureka-server" 25

# 2. Auth Service
start_service "Auth Service" "auth-service" 15

# 3. Task Service
start_service "Task Service" "task-service" 15

# 4. Wallet Service
start_service "Wallet Service" "wallet-service" 15

# 5. Store Service
start_service "Store Service" "store-service" 15

# 6. API Gateway - Must start last
start_service "API Gateway" "api-gateway" 10

echo -e "${GREEN}========================================"
echo "   All Services Started Successfully!"
echo -e "========================================${NC}"
echo ""
echo -e "${CYAN}Service URLs:${NC}"
echo "  Eureka Dashboard : http://localhost:8761"
echo "  API Gateway      : http://localhost:8080"
echo "  Auth Service     : http://localhost:8081"
echo "  Task Service     : http://localhost:8082"
echo "  Wallet Service   : http://localhost:8083"
echo "  Store Service    : http://localhost:8084"
echo ""
echo -e "${CYAN}Logs Location:${NC} $SCRIPT_DIR/logs/"
echo ""
echo "To view logs: tail -f logs/<service-name>.log"
echo "To stop all services: ./stop-all.sh"

