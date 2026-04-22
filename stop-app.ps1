$ErrorActionPreference = "SilentlyContinue"

$pidFile = Join-Path $PSScriptRoot ".app.pid"
if (Test-Path $pidFile) {
  $pid = Get-Content $pidFile | Select-Object -First 1
  if ($pid) {
    Stop-Process -Id $pid -Force
    Write-Host "Stopped process from PID file: $pid"
  }
  Remove-Item $pidFile -Force
}

$conn = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -First 1
if ($conn) {
  Stop-Process -Id $conn.OwningProcess -Force
  Write-Host "Stopped remaining process on port 8080: PID $($conn.OwningProcess)"
} else {
  Write-Host "No process on port 8080"
}
