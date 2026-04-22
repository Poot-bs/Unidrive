$ErrorActionPreference = "Stop"

$conn = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -First 1
if ($conn) {
  Write-Host "Port 8080 is used by PID $($conn.OwningProcess). Stopping it..."
  Stop-Process -Id $conn.OwningProcess -Force
}

Write-Host "Starting UniRide on http://localhost:8080 ..."
mvn spring-boot:run
