#!/bin/bash
# EliteSchool Backend - Stop All Services
# Cross-platform shell script for Unix/macOS/Linux

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo ""
echo -e "${CYAN}========================================"
echo "   EliteSchool Backend Shutdown Script"
echo -e "========================================${NC}"
echo ""

# Function to stop service on a specific port
stop_port() {
    local port=$1
    local service_name=$2
    
    echo -e "${YELLOW}Stopping $service_name (port $port)...${NC}"
    
    # Find PID using the port (works on macOS and Linux)
    if command -v lsof &> /dev/null; then
        # macOS / Linux with lsof
        local pid=$(lsof -ti :$port 2>/dev/null)
    else
        # Linux fallback using netstat or ss
        local pid=$(ss -tlnp 2>/dev/null | grep ":$port " | sed -n 's/.*pid=\([0-9]*\).*/\1/p')
        if [ -z "$pid" ]; then
            pid=$(netstat -tlnp 2>/dev/null | grep ":$port " | awk '{print $7}' | cut -d'/' -f1)
        fi
    fi
    
    if [ -n "$pid" ]; then
        echo "  Killing PID: $pid"
        kill -9 $pid 2>/dev/null
        echo -e "${GREEN}  $service_name stopped${NC}"
    else
        echo "  No process found on port $port"
    fi
}

# Stop services (in reverse order)
stop_port 8080 "API Gateway"
stop_port 8084 "Store Service"
stop_port 8083 "Wallet Service"
stop_port 8082 "Task Service"
stop_port 8081 "Auth Service"
stop_port 8761 "Eureka Server"

echo ""
echo -e "${GREEN}========================================"
echo "   All Services Stopped!"
echo -e "========================================${NC}"
echo ""

