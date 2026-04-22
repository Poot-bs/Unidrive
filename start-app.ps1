$ErrorActionPreference = "Stop"

$conn = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -First 1
if ($conn) {
  if ($conn.OwningProcess -gt 4) {
    Write-Host "Port 8080 is used by PID $($conn.OwningProcess). Stopping it..."
    Stop-Process -Id $conn.OwningProcess -Force
  } else {
    Write-Host "Port 8080 is bound to system PID $($conn.OwningProcess). Skipping forced stop."
  }
}

Write-Host "Starting UniRide on http://localhost:8080 ..."
mvn spring-boot:run
