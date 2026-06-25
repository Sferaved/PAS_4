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

function Resolve-TaxiDriverPath {
    param([string]$Root)
    foreach ($folder in @("Taxi_driver", "Taxi_dariver")) {
        $candidate = Join-Path $Root $folder
        if (Test-Path $candidate) { return $candidate }
    }
    return Join-Path $Root "Taxi_driver"
}

$pasRepos += @(
    @{ Name = "Taxi_driver"; Path = (Resolve-TaxiDriverPath -Root $workspaceRoot); Remote = "https://sferaved@github.com/Sferaved/taxi_driver.git" }

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

function Remove-LegacyGitCredential {
    param([string]$Target)
    $found = cmdkey /list 2>$null | Select-String -SimpleMatch $Target
    if ($found) {
        Write-Host "Removing legacy credential: $Target" -ForegroundColor Yellow
        cmdkey /delete:$Target 2>$null | Out-Null
    }
}

# Host-only entries conflict with credential.useHttpPath (per-repo PAT).
foreach ($legacyTarget in @(
        "LegacyGeneric:target=git:https://github.com",
        "LegacyGeneric:target=git:https://sferaved@github.com",
        "LegacyGeneric:target=git:https://Sferaved@github.com",
        "LegacyGeneric:target=git:https://andrey18051@github.com"
    )) {
    Remove-LegacyGitCredential -Target $legacyTarget
}
Write-Host "OK: legacy host-only GitHub credentials removed (if any)." -ForegroundColor Green

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
    Write-Host "Leave empty to skip - Windows will ask once per account on first push." -ForegroundColor Gray
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
} else {
    $registerScript = Join-Path $PSScriptRoot "register_taxi_driver_git.ps1"
    if (Test-Path $registerScript) {
        Write-Host "Copying PAS PAT to Taxi_driver (if available)..." -ForegroundColor Gray
        & $registerScript
    }
}

Write-Host ""
Write-Host "Remotes updated. PAT is in Credential Manager - no need to test with ls-remote or push --dry-run." -ForegroundColor Green
Write-Host "Done." -ForegroundColor Green
