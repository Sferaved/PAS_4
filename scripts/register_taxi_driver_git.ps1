# Copy saved GitHub PAT from any PAS repo to Taxi_driver (same Sferaved account).
# Run once if Taxi Driver asks for GitHub login on every push.
#   powershell -ExecutionPolicy Bypass -File "PAS_4\scripts\register_taxi_driver_git.ps1"

$ErrorActionPreference = "Stop"

$pas4Root = Split-Path $PSScriptRoot -Parent
$workspaceRoot = Split-Path $pas4Root -Parent

function Resolve-TaxiDriverPath {
    param([string]$Root)
    foreach ($folder in @("Taxi_driver", "Taxi_dariver")) {
        $candidate = Join-Path $Root $folder
        if (Test-Path $candidate) { return $candidate }
    }
    return Join-Path $Root "Taxi_driver"
}

$taxiDriverPath = Resolve-TaxiDriverPath -Root $workspaceRoot
$taxiRemote = "https://sferaved@github.com/Sferaved/taxi_driver.git"

$sourcePaths = @(
    "Sferaved/PAS_4.git",
    "Sferaved/PAS_3.git",
    "Sferaved/PAS2FINAL.git",
    "Sferaved/TaxiEasyUaMain.git"
)

function Get-StoredPat {
    param([string]$PathSuffix)
    $fillIn = @(
        "protocol=https",
        "host=github.com",
        "username=sferaved",
        "path=$PathSuffix",
        "",
        ""
    ) -join "`n"
    $filled = (($fillIn | git credential fill 2>$null) -join "`n").Trim()
    if ([string]::IsNullOrWhiteSpace($filled)) { return $null }
    $m = [regex]::Match($filled, "(?m)^password=(.+)$")
    if ($m.Success) { return $m.Groups[1].Value }
    return $null
}

function Save-PatForPath {
    param([string]$PathSuffix, [string]$Pat)
    $approve = @(
        "protocol=https",
        "host=github.com",
        "username=sferaved",
        "path=$PathSuffix",
        "password=$Pat"
    ) -join "`n"
    $approve | git credential approve | Out-Null
}

Write-Host "===== Taxi_driver Git credential =====" -ForegroundColor Cyan

if (-not (Test-Path $taxiDriverPath)) {
    Write-Host "ERROR: Taxi_driver not found at $taxiDriverPath" -ForegroundColor Red
    exit 1
}

git config --global credential.helper manager | Out-Null
git config --global credential.useHttpPath true | Out-Null

git -C $taxiDriverPath remote set-url origin $taxiRemote
git -C $taxiDriverPath config user.email "sferaved@gmail.com"
git -C $taxiDriverPath config user.name "manager"
Write-Host "OK: remote -> $taxiRemote" -ForegroundColor Green

$pat = $null
foreach ($p in $sourcePaths) {
    $pat = Get-StoredPat -PathSuffix $p
    if ($pat) {
        Write-Host "OK: PAT found via $p" -ForegroundColor Gray
        break
    }
}

if (-not $pat) {
    Write-Host "No saved PAS PAT in Credential Manager." -ForegroundColor Yellow
    Write-Host "Run once: PAS_4\scripts\setup_git_accounts.ps1 (enter PAT for sferaved)" -ForegroundColor Yellow
    exit 1
}

Save-PatForPath -PathSuffix "Sferaved/taxi_driver.git" -Pat $pat
Write-Host "OK: PAT stored for Sferaved/taxi_driver.git" -ForegroundColor Green

$env:GIT_TERMINAL_PROMPT = "0"
$head = git -C $taxiDriverPath ls-remote --heads origin master 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "WARN: ls-remote failed - PAT may be expired. Regenerate at GitHub." -ForegroundColor Yellow
    Write-Host $head
    exit 1
}

Write-Host "OK: Taxi_driver GitHub auth works (no browser prompt)." -ForegroundColor Green
