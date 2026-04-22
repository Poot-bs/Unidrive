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

$logPath = Join-Path $PSScriptRoot "runtime.log"
if (Test-Path $logPath) {
  Remove-Item $logPath -Force
}

Write-Host "Starting UniRide in background..."
$process = Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -RedirectStandardOutput $logPath -RedirectStandardError $logPath -PassThru
"$($process.Id)" | Set-Content (Join-Path $PSScriptRoot ".app.pid") -Encoding ascii
Write-Host "Started. PID=$($process.Id)"
Write-Host "Logs: $logPath"
