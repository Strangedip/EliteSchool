# EliteSchool Backend - Scripts Guide

Complete guide for building and running EliteSchool backend services using JAR files.

---

## 📋 Overview

The EliteSchool backend provides **4 main scripts** for managing all services:

| Script | Purpose | Platform |
|--------|---------|----------|
| `build-all.sh` / `build-all.bat` | Build all services from source | Unix / Windows |
| `run-all.sh` / `run-all.bat` | Run all services from JARs | Unix / Windows |
| `stop-all.sh` / `stop-all.bat` | Stop all running services | Unix / Windows |
| `start-all.sh` / `start-all.bat` | ⚠️ DEPRECATED - Build & run with Maven | Unix / Windows |

---

## 🆕 New Approach: JAR Execution

### Why JAR Execution?

✅ **Faster Startup** - No Maven overhead  
✅ **Production-Like** - Same as deployment  
✅ **Resource Efficient** - Lower memory usage  
✅ **Better Logging** - Dedicated log files  
✅ **Clean Separation** - Build once, run many times  

### Old vs New:

```bash
# ❌ OLD: Maven-based (slow, runs in single terminal)
./start-all.sh  # Runs: mvn spring-boot:run for each service

# ✅ NEW: JAR-based (fast, proper logging)
./build-all.sh  # Build once: mvn package
./run-all.sh    # Run many times: java -jar
```

---

## 🔨 Build Scripts

### build-all.sh / build-all.bat

**Purpose:** Build all services from source code into executable JARs.

**Features:**
- ✅ Builds `common-utils` first (dependency for all services)
- ✅ Builds services in correct order
- ✅ Standardized JAR names (no version numbers)
- ✅ Comprehensive logging with timestamps
- ✅ Success/failure tracking
- ✅ Color-coded output
- ✅ Detailed build logs

**JAR Naming:**
```
# Before (version-specific):
auth-service-0.0.1-SNAPSHOT.jar

# After (standardized):
auth-service.jar
```

This is achieved using `<finalName>` in `pom.xml`:
```xml
<build>
    <finalName>auth-service</finalName>
    <!-- ... -->
</build>
```

---

### 📖 build-all Usage

#### Unix/Linux/macOS:
```bash
cd elite-backend

# Make executable (first time only)
chmod +x build-all.sh

# Run build
./build-all.sh
```

#### Windows:
```cmd
cd elite-backend
build-all.bat
```

---

### Build Process:

```
1. Verify Maven installation
2. Build common-utils (mvn install)
   ├─> Installs to ~/.m2/repository
   └─> Available for other services
3. Build eureka-server (mvn package)
4. Build auth-service (mvn package)
5. Build task-service (mvn package)
6. Build wallet-service (mvn package)
7. Build store-service (mvn package)
8. Build api-gateway (mvn package)
9. Summary report
```

---

### Build Output:

```
╔════════════════════════════════════════════════════════════════╗
║           EliteSchool Backend - Build All Services            ║
╚════════════════════════════════════════════════════════════════╝

[INFO] Build started at: 2025-12-12 14:30:00
[INFO] Build log: logs/build-20251212-143000.log

[CHECK] Verifying Maven installation...
[OK] Apache Maven 3.9.5

╭────────────────────────────────────────────────────────────────╮
│ Building: common-utils
╰────────────────────────────────────────────────────────────────╯
[BUILD] Location: common-utils
[PROGRESS] Cleaning... ✓
[PROGRESS] Installing to local repository... ✓
[SUCCESS] common-utils.jar created (50 KB)

... (repeats for each service) ...

╔════════════════════════════════════════════════════════════════╗
║                  ✓ ALL BUILDS SUCCESSFUL! ✓                   ║
╚════════════════════════════════════════════════════════════════╝

[SUCCESS] All services built successfully!
[INFO] JARs are located in each service's target/ directory
[INFO] To run services: ./run-all.sh
```

---

### Build Artifacts:

After successful build, you'll have:

```
elite-backend/
├── common-utils/target/common-utils.jar
├── eureka-server/target/eureka-server.jar
├── auth-service/target/auth-service.jar
├── task-service/target/task-service.jar
├── wallet-service/target/wallet-service.jar
├── store-service/target/store-service.jar
└── api-gateway/target/api-gateway.jar
```

---

## 🚀 Run Scripts

### run-all.sh / run-all.bat

**Purpose:** Run all services from built JARs.

**Features:**
- ✅ Verifies JARs exist before starting
- ✅ Starts services in correct order
- ✅ Proper wait times between services
- ✅ Individual log files per service
- ✅ PID tracking (Unix)
- ✅ Separate windows per service (Windows)
- ✅ Health check after startup

**Prerequisites:**
- Services must be built first (`./build-all.sh`)
- PostgreSQL must be running
- Ports must be available (8080, 8081-8084, 8761)

---

### 📖 run-all Usage

#### Unix/Linux/macOS:
```bash
cd elite-backend

# Make executable (first time only)
chmod +x run-all.sh

# Run services
./run-all.sh
```

#### Windows:
```cmd
cd elite-backend
run-all.bat
```

---

### Startup Process:

```
1. Verify Java installation (Java 21 required)
2. Verify JARs exist
3. Start Eureka Server (8761) - Wait 30s
4. Start Auth Service (8081) - Wait 20s
5. Start Task Service (8082) - Wait 20s
6. Start Wallet Service (8083) - Wait 20s
7. Start Store Service (8084) - Wait 20s
8. Start API Gateway (8080) - Wait 15s
9. Summary report
```

**Total startup time: ~2 minutes**

---

### Run Output:

```
╔════════════════════════════════════════════════════════════════╗
║            EliteSchool Backend - Run All Services             ║
╚════════════════════════════════════════════════════════════════╝

[INFO] Run started at: 2025-12-12 14:35:00
[INFO] PID file: .running-services.pid

[CHECK] Verifying Java installation...
[OK] openjdk version "21.0.1"

╭────────────────────────────────────────────────────────────────╮
│ Starting: Service Discovery (eureka-server)
╰────────────────────────────────────────────────────────────────╯
[START] Port: 8761
[START] JAR: elite-backend/eureka-server/target/eureka-server.jar
[START] Log: logs/eureka-server.log
[STARTED] PID: 12345
[WAIT] Waiting 30 seconds for initialization...
[RUNNING] eureka-server is running on port 8761

... (repeats for each service) ...

╔════════════════════════════════════════════════════════════════╗
║              ✓ ALL SERVICES STARTED SUCCESSFULLY! ✓           ║
╚════════════════════════════════════════════════════════════════╝

[ENDPOINTS] Service URLs:
  • Eureka Dashboard: http://localhost:8761
  • API Gateway:      http://localhost:8080
  • Auth Service:     http://localhost:8081
  • Task Service:     http://localhost:8082
  • Wallet Service:   http://localhost:8083
  • Store Service:    http://localhost:8084

[LOGS] Service logs are in: logs/
[STOP] To stop all services: ./stop-all.sh

[INFO] Running services:
  ● eureka-server (PID: 12345, Port: 8761)
  ● auth-service (PID: 12346, Port: 8081)
  ● task-service (PID: 12347, Port: 8082)
  ● wallet-service (PID: 12348, Port: 8083)
  ● store-service (PID: 12349, Port: 8084)
  ● api-gateway (PID: 12350, Port: 8080)
```

---

### Platform Differences:

#### Unix/Linux/macOS:
- Services run in **background**
- PIDs tracked in `.running-services.pid`
- Logs written to `logs/service-name.log`
- Use `ps -p PID` to check status
- Use `./stop-all.sh` to stop all

#### Windows:
- Services run in **separate windows**
- Each window shows service output in real-time
- Logs also written to `logs\service-name.log`
- Close windows to stop individual services
- Use `stop-all.bat` to stop all at once

---

## 🛑 Stop Scripts

### stop-all.sh / stop-all.bat

**Purpose:** Stop all running backend services.

**Features:**
- ✅ Stops services by PID (Unix)
- ✅ Stops services by port (fallback)
- ✅ Graceful shutdown
- ✅ Verification of service status
- ✅ Cleanup of PID files

---

### 📖 stop-all Usage

#### Unix/Linux/macOS:
```bash
cd elite-backend
./stop-all.sh
```

#### Windows:
```cmd
cd elite-backend
stop-all.bat
```

---

### Stop Process:

```
1. Check for PID file
2. Stop services from PID file (if exists)
3. Stop services by port (cleanup)
   ├─> API Gateway (8080)
   ├─> Store Service (8084)
   ├─> Wallet Service (8083)
   ├─> Task Service (8082)
   ├─> Auth Service (8081)
   └─> Eureka Server (8761)
4. Remove PID file
5. Summary report
```

---

### Stop Output:

```
╔════════════════════════════════════════════════════════════════╗
║           EliteSchool Backend - Stop All Services             ║
╚════════════════════════════════════════════════════════════════╝

[INFO] Found PID file, stopping tracked services...

[STOP] Stopping eureka-server (PID: 12345, Port: 8761)...
  ✓ eureka-server stopped
[STOP] Stopping auth-service (PID: 12346, Port: 8081)...
  ✓ auth-service stopped
... (repeats for each service) ...

[INFO] Stopping services by port (cleanup)...

[STOP] Stopping API Gateway (port 8080)...
  ○ No process found on port 8080

╔════════════════════════════════════════════════════════════════╗
║                    ALL SERVICES STOPPED                        ║
╚════════════════════════════════════════════════════════════════╝
```

---

## 📂 Log Files

All scripts generate detailed logs:

```
elite-backend/logs/
├── build-20251212-143000.log      # Build script log
├── run-20251212-143500.log        # Run script log
├── eureka-server.log              # Eureka service log
├── auth-service.log               # Auth service log
├── task-service.log               # Task service log
├── wallet-service.log             # Wallet service log
├── store-service.log              # Store service log
└── api-gateway.log                # Gateway service log
```

### Log Retention:

- Build/run logs: Timestamped (kept indefinitely)
- Service logs: Overwritten on each run
- Manual cleanup: `rm -rf logs/*.log`

---

## 🔧 Complete Workflow

### Development Workflow:

```bash
# 1. Initial setup (one time)
cd elite-backend
chmod +x *.sh
./build-all.sh

# 2. Start services
./run-all.sh

# 3. Develop (edit code)
# ... make changes ...

# 4. Rebuild (only changed services or all)
./build-all.sh

# 5. Restart services
./stop-all.sh
./run-all.sh

# 6. Stop when done
./stop-all.sh
```

---

### Selective Build:

To build just one service:

```bash
cd auth-service
mvn clean package

# Or with common-utils:
cd ../common-utils && mvn clean install
cd ../auth-service && mvn clean package
```

---

### Selective Run:

To run just one service manually:

```bash
cd auth-service/target
java -jar auth-service.jar
```

---

## 🐛 Troubleshooting

### Issue: Build Fails

**Error:** `Package common-utils does not exist`

**Solution:**
```bash
cd common-utils
mvn clean install
cd ..
./build-all.sh
```

---

### Issue: JAR Not Found

**Error:** `JAR not found: elite-backend/auth-service/target/auth-service.jar`

**Solution:**
```bash
# Run build first
./build-all.sh

# Then run
./run-all.sh
```

---

### Issue: Port Already in Use

**Error:** `Address already in use: bind`

**Solution:**
```bash
# Stop all services first
./stop-all.sh

# Or manually kill process
# Unix:
lsof -ti :8081 | xargs kill -9

# Windows:
netstat -ano | findstr :8081
taskkill /F /PID <PID>
```

---

### Issue: Service Fails to Start

**Check:**
1. Is PostgreSQL running?
2. Does database `EliteSchool` exist?
3. Are credentials correct in `application.properties`?
4. Check service log: `cat logs/auth-service.log`

---

### Issue: Eureka Shows "Emergency Mode"

**Cause:** Services restarted too frequently (normal in dev)

**Solution:** Wait 30 seconds, or disable self-preservation in `eureka-server/application.properties`:
```properties
eureka.server.enable-self-preservation=false
```

---

## 📊 Script Comparison

| Feature | start-all (old) | build-all + run-all (new) |
|---------|----------------|---------------------------|
| Build method | `mvn spring-boot:run` | `mvn package` |
| Execution | Maven | Java JAR |
| Startup time | ~5 minutes | ~2 minutes |
| Memory usage | High (Maven + service) | Low (service only) |
| Logging | Mixed in terminal | Individual files |
| Production-like | No | Yes |
| Restart speed | Slow (rebuild) | Fast (reuse JAR) |
| **Recommendation** | ❌ Deprecated | ✅ **Use this** |

---

## ✅ Best Practices

1. **Build once, run many times**
   ```bash
   ./build-all.sh  # Once per day or after code changes
   ./run-all.sh    # Multiple times for testing
   ```

2. **Check logs when debugging**
   ```bash
   tail -f logs/auth-service.log
   ```

3. **Stop services before rebuilding**
   ```bash
   ./stop-all.sh
   ./build-all.sh
   ./run-all.sh
   ```

4. **Use Eureka Dashboard to verify**
   - Open http://localhost:8761
   - All 5 services should be UP (green)

---

## 🎯 Quick Reference

```bash
# Complete workflow
./build-all.sh   # Build all services (2-3 minutes)
./run-all.sh     # Start all services (2 minutes)
./stop-all.sh    # Stop all services (5 seconds)

# Check status
curl http://localhost:8761  # Eureka
ps aux | grep java          # Unix: Running services
netstat -ano | findstr 8080 # Windows: Port check

# View logs
tail -f logs/auth-service.log     # Unix: Live log
type logs\auth-service.log        # Windows: View log
```

---

**Scripts are production-ready and fully tested!** 🚀

