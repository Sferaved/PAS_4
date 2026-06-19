# Verify GitHub auth for all workspace repos without opening the browser repeatedly.
# Run: powershell -ExecutionPolicy Bypass -File "...\PAS_4\scripts\verify_git_auth.ps1"
# Optional fix (one PAT per account): -Fix

param(
    [switch]$Fix
)

$ErrorActionPreference = "Stop"

$pas4Root = Split-Path $PSScriptRoot -Parent
$workspaceRoot = Split-Path $pas4Root -Parent

$repos = @(
    @{ Name = "PAS_1"; Path = Join-Path $workspaceRoot "PAS_1"; User = "sferaved"; PathSuffix = "Sferaved/TaxiEasyUaMain.git" },
    @{ Name = "PAS_2"; Path = Join-Path $workspaceRoot "PAS_2"; User = "sferaved"; PathSuffix = "Sferaved/PAS2FINAL.git" },
    @{ Name = "PAS_3"; Path = Join-Path $workspaceRoot "PAS_3"; User = "sferaved"; PathSuffix = "Sferaved/PAS_3.git" },
    @{ Name = "PAS_4"; Path = $pas4Root; User = "sferaved"; PathSuffix = "Sferaved/PAS_4.git" }
)

$taxiRepoPath = $null
foreach ($candidate in @(
        (Join-Path (Split-Path $workspaceRoot -Parent) "PhpstormProjects\taxi_repo"),
        (Join-Path $env:USERPROFILE "PhpstormProjects\taxi_repo")
    )) {
    if (Test-Path $candidate) {
        $taxiRepoPath = $candidate
        break
    }
}
if ($taxiRepoPath) {
    $repos += @{ Name = "taxi_repo"; Path = $taxiRepoPath; User = "andrey18051"; PathSuffix = "andrey18051/taxi_repo.git" }
}

function Save-GitCredential {
    param([string]$Username, [string]$Pat, [string]$PathSuffix)
    if ([string]::IsNullOrWhiteSpace($Pat)) { return }
    @(
        "protocol=https"
        "host=github.com"
        "username=$Username"
        "path=$PathSuffix"
        "password=$Pat"
    ) -join "`n" | git credential approve
}

function Test-RepoAuth {
    param([string]$RepoPath)
    if (-not (Test-Path $RepoPath)) {
        return $false
    }
    $prevPrompt = $env:GIT_TERMINAL_PROMPT
    $prevGcm = $env:GCM_INTERACTIVE
    $env:GIT_TERMINAL_PROMPT = "0"
    $env:GCM_INTERACTIVE = "never"
    $prevEap = $ErrorActionPreference
    $ErrorActionPreference = "SilentlyContinue"
    try {
        git -C $RepoPath ls-remote origin HEAD 1>$null 2>$null
        return ($LASTEXITCODE -eq 0)
    } finally {
        $ErrorActionPreference = $prevEap
        if ($null -eq $prevPrompt) { Remove-Item Env:GIT_TERMINAL_PROMPT -ErrorAction SilentlyContinue }
        else { $env:GIT_TERMINAL_PROMPT = $prevPrompt }
        if ($null -eq $prevGcm) { Remove-Item Env:GCM_INTERACTIVE -ErrorAction SilentlyContinue }
        else { $env:GCM_INTERACTIVE = $prevGcm }
    }
}

Write-Host "===== GitHub auth check (no browser) =====" -ForegroundColor Cyan

$failed = @()
foreach ($repo in $repos) {
    if (-not (Test-Path $repo.Path)) {
        Write-Host "SKIP: $($repo.Name) not found" -ForegroundColor Yellow
        continue
    }
    $ok = Test-RepoAuth -RepoPath $repo.Path
    if ($ok) {
        Write-Host "OK:   $($repo.Name)" -ForegroundColor Green
    } else {
        Write-Host "FAIL: $($repo.Name) - would open GitHub login on fetch/push" -ForegroundColor Red
        $failed += $repo
    }
}

if ($failed.Count -eq 0) {
    Write-Host ""
    Write-Host "All repos authenticated. Browser should not pop up on git fetch/pull." -ForegroundColor Green
    exit 0
}

Write-Host ""
Write-Host "Failed: $($failed.Count) repo(s). Common cause: expired/missing PAT for that repo path." -ForegroundColor Yellow

if (-not $Fix) {
    Write-Host "Re-run with -Fix to store PAT once per GitHub account (no browser after that):" -ForegroundColor Cyan
    Write-Host '  powershell -ExecutionPolicy Bypass -File "scripts\verify_git_auth.ps1" -Fix' -ForegroundColor Gray
    exit 1
}

git config --global credential.helper manager | Out-Null
git config --global credential.useHttpPath true

$needSferaved = $failed | Where-Object { $_.User -eq "sferaved" }
$needTaxi = $failed | Where-Object { $_.User -eq "andrey18051" }

if ($needSferaved) {
    Write-Host ""
    Write-Host "PAT for sferaved@gmail.com (classic, scope repo) - saved to ALL PAS repos:" -ForegroundColor Cyan
    $pasPat = Read-Host "PAT sferaved"
    foreach ($repo in $repos | Where-Object { $_.User -eq "sferaved" }) {
        Save-GitCredential -Username "sferaved" -Pat $pasPat -PathSuffix $repo.PathSuffix
    }
}

if ($needTaxi) {
    Write-Host ""
    Write-Host "PAT for andrey18051@gmail.com (taxi_repo):" -ForegroundColor Cyan
    $taxiPat = Read-Host "PAT andrey18051"
    Save-GitCredential -Username "andrey18051" -Pat $taxiPat -PathSuffix "andrey18051/taxi_repo.git"
}

Write-Host ""
Write-Host "Re-checking..." -ForegroundColor Cyan
$stillFailed = @()
foreach ($repo in $repos) {
    if (-not (Test-Path $repo.Path)) { continue }
    if (Test-RepoAuth -RepoPath $repo.Path) {
        Write-Host "OK:   $($repo.Name)" -ForegroundColor Green
    } else {
        Write-Host "FAIL: $($repo.Name)" -ForegroundColor Red
        $stillFailed += $repo.Name
    }
}

if ($stillFailed.Count -gt 0) {
    Write-Host "Still failing: $($stillFailed -join ', '). Check PAT scope/expiry on GitHub." -ForegroundColor Red
    exit 1
}

Write-Host "Done. Git fetch/pull should work without repeated GitHub browser windows." -ForegroundColor Green
exit 0
