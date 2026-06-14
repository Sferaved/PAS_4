# One-time / repair setup: separate GitHub accounts for PAS (Sferaved) and taxi_repo (andrey18051).
# Run: powershell -ExecutionPolicy Bypass -File "...\PAS_4\scripts\setup_git_accounts.ps1"
# Paths are derived from this script location (PAS_4/scripts -> sibling PAS_1..3, taxi_repo).

$ErrorActionPreference = "Stop"

$pas4Root = Split-Path $PSScriptRoot -Parent
$workspaceRoot = Split-Path $pas4Root -Parent

$pasRepos = @(
    @{ Name = "PAS_1"; Path = Join-Path $workspaceRoot "PAS_1"; Remote = "https://sferaved@github.com/Sferaved/TaxiEasyUaMain.git" },
    @{ Name = "PAS_2"; Path = Join-Path $workspaceRoot "PAS_2"; Remote = "https://sferaved@github.com/Sferaved/PAS2FINAL.git" },
    @{ Name = "PAS_3"; Path = Join-Path $workspaceRoot "PAS_3"; Remote = "https://sferaved@github.com/Sferaved/PAS_3.git" },
    @{ Name = "PAS_4"; Path = $pas4Root; Remote = "https://sferaved@github.com/Sferaved/PAS_4.git" }
)

$taxiRepoPath = $null
foreach ($candidate in @(
        (Join-Path (Split-Path $workspaceRoot -Parent) "PhpstormProjects\taxi_repo"),
        (Join-Path $env:USERPROFILE "PhpstormProjects\taxi_repo"),
        "C:\Users\user\PhpstormProjects\taxi_repo"
    )) {
    if (Test-Path $candidate) {
        $taxiRepoPath = $candidate
        break
    }
}
$taxiRepo = @{
    Name   = "taxi_repo"
    Path   = $taxiRepoPath
    Remote = "https://andrey18051@github.com/andrey18051/taxi_repo.git"
}

function Save-GitCredential {
    param(
        [string]$Username,
        [string]$Pat,
        [string]$PathSuffix
    )
    if ([string]::IsNullOrWhiteSpace($Pat)) {
        return
    }
    $inputText = @(
        "protocol=https"
        "host=github.com"
        "username=$Username"
        "path=$PathSuffix"
        "password=$Pat"
    ) -join "`n"
    $inputText | git credential approve
}

Write-Host "===== Git multi-account setup =====" -ForegroundColor Cyan
Write-Host "Workspace: $workspaceRoot" -ForegroundColor Gray

git config --global credential.helper manager
git config --global credential.useHttpPath true
Write-Host "OK: credential.useHttpPath=true (separate tokens per repo path)" -ForegroundColor Green

$legacyTarget = "LegacyGeneric:target=git:https://github.com"
$legacy = cmdkey /list 2>$null | Select-String -SimpleMatch $legacyTarget
if ($legacy) {
    Write-Host "Removing old shared GitHub credential ($legacyTarget)..." -ForegroundColor Yellow
    cmdkey /delete:$legacyTarget 2>$null | Out-Null
    Write-Host "OK: removed. Store two PATs below (or on first push)." -ForegroundColor Green
}

foreach ($repo in $pasRepos) {
    if (-not (Test-Path $repo.Path)) {
        Write-Host "SKIP: $($repo.Name) not found at $($repo.Path)" -ForegroundColor Yellow
        continue
    }
    git -C $repo.Path remote set-url origin $repo.Remote
    git -C $repo.Path config user.email "sferaved@gmail.com"
    git -C $repo.Path config user.name "manager"
    Write-Host "OK: $($repo.Name) -> $($repo.Remote)" -ForegroundColor Green
}

if ($taxiRepo.Path -and (Test-Path $taxiRepo.Path)) {
    git -C $taxiRepo.Path remote set-url origin $taxiRepo.Remote
    git -C $taxiRepo.Path config user.email "andrey18051@gmail.com"
    git -C $taxiRepo.Path config user.name "manager"
    Write-Host "OK: taxi_repo -> $($taxiRepo.Remote)" -ForegroundColor Green
} else {
    Write-Host "SKIP: taxi_repo not found (looked under PhpstormProjects)" -ForegroundColor Yellow
}

if (-not $env:SETUP_GIT_SKIP_PAT_PROMPT) {
    Write-Host ""
    Write-Host "Store Personal Access Tokens (classic, scope: repo)." -ForegroundColor Cyan
    Write-Host "Leave empty to skip — Windows will ask once per account on first push." -ForegroundColor Gray
    $pasPat = Read-Host "PAT for sferaved@gmail.com (PAS repos)"
    $taxiPat = Read-Host "PAT for andrey18051@gmail.com (taxi_repo)"

    foreach ($repo in $pasRepos) {
        if (-not (Test-Path $repo.Path)) { continue }
        $pathSuffix = ($repo.Remote -replace '^https://sferaved@github.com/', '')
        Save-GitCredential -Username "sferaved" -Pat $pasPat -PathSuffix $pathSuffix
    }
    if ($taxiRepo.Path -and (Test-Path $taxiRepo.Path) -and -not [string]::IsNullOrWhiteSpace($taxiPat)) {
        Save-GitCredential -Username "andrey18051" -Pat $taxiPat -PathSuffix "andrey18051/taxi_repo.git"
    }
}

Write-Host ""
Write-Host "Verify (no real push):" -ForegroundColor Cyan
if (Test-Path $pas4Root) {
    Write-Host "  PAS_4:  git -C `"$pas4Root`" push --dry-run"
}
if ($taxiRepo.Path -and (Test-Path $taxiRepo.Path)) {
    Write-Host "  taxi:   git -C `"$($taxiRepo.Path)`" push --dry-run"
}
Write-Host "Done." -ForegroundColor Green
