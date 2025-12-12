#!/bin/bash
# ==============================================================================
# EliteSchool Backend - Stop All Services Script
# ==============================================================================
# Purpose: Stop all running backend services
# Usage: ./stop-all.sh
# ==============================================================================

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PID_FILE="$SCRIPT_DIR/.running-services.pid"

echo -e "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║           EliteSchool Backend - Stop All Services             ║${NC}"
echo -e "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Function to stop service on a specific port
stop_port() {
    local port=$1
    local service_name=$2
    
    echo -e "${YELLOW}[STOP]${NC} Stopping $service_name (port $port)..."
    
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
        echo -e "  ${RED}→${NC} Killing PID: $pid"
        kill -9 $pid 2>/dev/null
        if [ $? -eq 0 ]; then
            echo -e "  ${GREEN}✓${NC} $service_name stopped"
        else
            echo -e "  ${RED}✗${NC} Failed to stop $service_name"
        fi
    else
        echo -e "  ${YELLOW}○${NC} No process found on port $port"
    fi
}

# Check if PID file exists
if [ -f "$PID_FILE" ]; then
    echo -e "${CYAN}[INFO]${NC} Found PID file, stopping tracked services..."
    echo ""
    
    # Stop services from PID file
    while IFS=: read -r name pid port; do
        if [ -n "$pid" ] && ps -p $pid > /dev/null 2>&1; then
            echo -e "${YELLOW}[STOP]${NC} Stopping $name (PID: $pid, Port: $port)..."
            kill -9 $pid 2>/dev/null
            if [ $? -eq 0 ]; then
                echo -e "  ${GREEN}✓${NC} $name stopped"
            else
                echo -e "  ${RED}✗${NC} Failed to stop $name"
            fi
        else
            echo -e "${YELLOW}[INFO]${NC} $name (PID: $pid) - Already stopped"
        fi
    done < "$PID_FILE"
    
    # Remove PID file
    rm "$PID_FILE"
    echo ""
fi

echo -e "${CYAN}[INFO]${NC} Stopping services by port (cleanup)..."
echo ""

# Stop services in reverse order (gateway first, eureka last)
stop_port 8080 "API Gateway"
stop_port 8084 "Store Service"
stop_port 8083 "Wallet Service"
stop_port 8082 "Task Service"
stop_port 8081 "Auth Service"
stop_port 8761 "Eureka Server"

echo ""
echo -e "${GREEN}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║                    ALL SERVICES STOPPED                        ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""

