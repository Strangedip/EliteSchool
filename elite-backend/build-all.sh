#!/bin/bash
# ==============================================================================
# EliteSchool Backend - Build All Services Script
# ==============================================================================
# Purpose: Build all backend services starting from common-utils
# Usage: ./build-all.sh
# ==============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$SCRIPT_DIR/logs"
BUILD_LOG="$LOG_DIR/build-$(date +%Y%m%d-%H%M%S).log"

# Maven path detection - use system PATH or fallback to wrapper
if command -v mvn &> /dev/null; then
    MVN_CMD="mvn"
    echo "[INFO] Using Maven from system PATH"
else
    MVN_WRAPPER="$HOME/.m2/wrapper/dists/apache-maven-3.9.9-bin/4nf9hui3q3djbarqar9g711ggc/apache-maven-3.9.9/bin/mvn"
    if [ -f "$MVN_WRAPPER" ]; then
        MVN_CMD="$MVN_WRAPPER"
        echo "[INFO] Using Maven from wrapper: $MVN_WRAPPER"
    else
        echo "[ERROR] Maven not found in PATH or wrapper location"
        echo "[ERROR] Please install Maven or check wrapper path"
        exit 1
    fi
fi

# Create logs directory
mkdir -p "$LOG_DIR"

# Function to log messages
log() {
    echo -e "$1" | tee -a "$BUILD_LOG"
}

# Function to log without newline
log_n() {
    echo -n -e "$1" | tee -a "$BUILD_LOG"
}

# Print header
clear
log "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
log "${CYAN}║           EliteSchool Backend - Build All Services            ║${NC}"
log "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
log ""
log "${BLUE}[INFO]${NC} Build started at: $(date '+%Y-%m-%d %H:%M:%S')"
log "${BLUE}[INFO]${NC} Build log: $BUILD_LOG"
log "${BLUE}[INFO]${NC} Working directory: $SCRIPT_DIR"
log ""

# Display Maven version
MAVEN_VERSION=$("$MVN_CMD" -version | head -n 1)
log "${YELLOW}[CHECK]${NC} $MAVEN_VERSION"
log ""

# Function to build a service
build_service() {
    local service_name=$1
    local service_path=$2
    local is_util=$3
    
    local full_path="$SCRIPT_DIR/$service_path"
    
    if [ ! -d "$full_path" ]; then
        log "${RED}[ERROR]${NC} Directory not found: $full_path"
        return 1
    fi
    
    log "${CYAN}╭────────────────────────────────────────────────────────────────╮${NC}"
    log "${CYAN}│ Building: $service_name${NC}"
    log "${CYAN}╰────────────────────────────────────────────────────────────────╯${NC}"
    
    log "${BLUE}[BUILD]${NC} Location: $service_path"
    log_n "${YELLOW}[PROGRESS]${NC} Cleaning..."
    
    cd "$full_path"
    
    # Clean
    if "$MVN_CMD" clean >> "$BUILD_LOG" 2>&1; then
        log " ${GREEN}✓${NC}"
    else
        log " ${RED}✗${NC}"
        log "${RED}[ERROR]${NC} Clean failed for $service_name"
        return 1
    fi
    
    # Build/Install
    if [ "$is_util" = "true" ]; then
        log_n "${YELLOW}[PROGRESS]${NC} Installing to local repository..."
        if "$MVN_CMD" install -DskipTests >> "$BUILD_LOG" 2>&1; then
            log " ${GREEN}✓${NC}"
        else
            log " ${RED}✗${NC}"
            log "${RED}[ERROR]${NC} Install failed for $service_name"
            return 1
        fi
    else
        log_n "${YELLOW}[PROGRESS]${NC} Packaging..."
        if "$MVN_CMD" package -DskipTests >> "$BUILD_LOG" 2>&1; then
            log " ${GREEN}✓${NC}"
        else
            log " ${RED}✗${NC}"
            log "${RED}[ERROR]${NC} Package failed for $service_name"
            return 1
        fi
    fi
    
    # Verify JAR
    if [ "$is_util" = "true" ]; then
        JAR_FILE="$full_path/target/$service_name.jar"
    else
        JAR_FILE="$full_path/target/$service_name.jar"
    fi
    
    if [ -f "$JAR_FILE" ]; then
        JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
        log "${GREEN}[SUCCESS]${NC} $service_name.jar created (${JAR_SIZE})"
    else
        log "${RED}[ERROR]${NC} JAR file not found: $JAR_FILE"
        return 1
    fi
    
    cd "$SCRIPT_DIR"
    log ""
    return 0
}

# Build order
log "${BLUE}[INFO]${NC} Build order: common-utils → services → eureka → gateway"
log ""

# Track success/failure
TOTAL_SERVICES=7
BUILT_SERVICES=0
FAILED_SERVICES=()

# 1. Build common-utils first (dependency for all services)
if build_service "common-utils" "common-utils" "true"; then
    ((BUILT_SERVICES++))
else
    FAILED_SERVICES+=("common-utils")
fi

# 2. Build Eureka Server
if build_service "eureka-server" "eureka-server" "false"; then
    ((BUILT_SERVICES++))
else
    FAILED_SERVICES+=("eureka-server")
fi

# 3. Build Auth Service
if build_service "auth-service" "auth-service" "false"; then
    ((BUILT_SERVICES++))
else
    FAILED_SERVICES+=("auth-service")
fi

# 4. Build Task Service
if build_service "task-service" "task-service" "false"; then
    ((BUILT_SERVICES++))
else
    FAILED_SERVICES+=("task-service")
fi

# 5. Build Wallet Service
if build_service "wallet-service" "wallet-service" "false"; then
    ((BUILT_SERVICES++))
else
    FAILED_SERVICES+=("wallet-service")
fi

# 6. Build Store Service
if build_service "store-service" "store-service" "false"; then
    ((BUILT_SERVICES++))
else
    FAILED_SERVICES+=("store-service")
fi

# 7. Build API Gateway
if build_service "api-gateway" "api-gateway" "false"; then
    ((BUILT_SERVICES++))
else
    FAILED_SERVICES+=("api-gateway")
fi

# Summary
log "${CYAN}╔════════════════════════════════════════════════════════════════╗${NC}"
log "${CYAN}║                         BUILD SUMMARY                          ║${NC}"
log "${CYAN}╚════════════════════════════════════════════════════════════════╝${NC}"
log ""
log "${BLUE}[INFO]${NC} Build completed at: $(date '+%Y-%m-%d %H:%M:%S')"
log "${BLUE}[INFO]${NC} Services built: $BUILT_SERVICES / $TOTAL_SERVICES"

if [ $BUILT_SERVICES -eq $TOTAL_SERVICES ]; then
    log ""
    log "${GREEN}╔════════════════════════════════════════════════════════════════╗${NC}"
    log "${GREEN}║                  ✓ ALL BUILDS SUCCESSFUL! ✓                   ║${NC}"
    log "${GREEN}╚════════════════════════════════════════════════════════════════╝${NC}"
    log ""
    log "${GREEN}[SUCCESS]${NC} All services built successfully!"
    log "${BLUE}[INFO]${NC} JARs are located in each service's target/ directory"
    log "${BLUE}[INFO]${NC} To run services: ./run-all.sh"
    log ""
    exit 0
else
    log ""
    log "${RED}╔════════════════════════════════════════════════════════════════╗${NC}"
    log "${RED}║                    ✗ BUILD FAILED! ✗                          ║${NC}"
    log "${RED}╚════════════════════════════════════════════════════════════════╝${NC}"
    log ""
    log "${RED}[FAILED]${NC} The following services failed to build:"
    for service in "${FAILED_SERVICES[@]}"; do
        log "${RED}  - $service${NC}"
    done
    log ""
    log "${YELLOW}[INFO]${NC} Check the build log for details: $BUILD_LOG"
    log ""
    exit 1
fi

