$ErrorActionPreference = "Stop"

$conn = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -First 1
if ($conn) {
  Write-Host "Port 8080 is used by PID $($conn.OwningProcess). Stopping it..."
  Stop-Process -Id $conn.OwningProcess -Force
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
