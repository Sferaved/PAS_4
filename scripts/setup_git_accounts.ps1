# One-time / repair: GitHub PAT for PAS, MDDocImport*, taxi_repo (no browser prompts).
# Run: powershell -ExecutionPolicy Bypass -File "PAS_4\scripts\setup_git_accounts.ps1"
# Auto (read PAT from github.env / Credential Manager, no prompts):
#   powershell -ExecutionPolicy Bypass -File "PAS_4\scripts\setup_git_accounts.ps1" -Auto

param(
    [switch]$Auto
)

$ErrorActionPreference = "Stop"

$pas4Root = Split-Path $PSScriptRoot -Parent
$workspaceRoot = Split-Path $pas4Root -Parent
$userRoot = $env:USERPROFILE

function Resolve-TaxiDriverPath {
    param([string]$Root)
    foreach ($folder in @("Taxi_driver", "Taxi_dariver")) {
        $candidate = Join-Path $Root $folder
        if (Test-Path $candidate) { return $candidate }
    }
    return Join-Path $Root "Taxi_driver"
}

$pasRepos = @(
    @{ Name = "PAS_1"; Path = Join-Path $workspaceRoot "PAS_1"; Remote = "https://sferaved@github.com/Sferaved/TaxiEasyUaMain.git" },
    @{ Name = "PAS_2"; Path = Join-Path $workspaceRoot "PAS_2"; Remote = "https://sferaved@github.com/Sferaved/PAS2FINAL.git" },
    @{ Name = "PAS_3"; Path = Join-Path $workspaceRoot "PAS_3"; Remote = "https://sferaved@github.com/Sferaved/PAS_3.git" },
    @{ Name = "PAS_4"; Path = $pas4Root; Remote = "https://sferaved@github.com/Sferaved/PAS_4.git" },
    @{ Name = "Taxi_driver"; Path = (Resolve-TaxiDriverPath -Root $workspaceRoot); Remote = "https://sferaved@github.com/Sferaved/taxi_driver.git" },
    @{ Name = "MDDocImport"; Path = Join-Path $userRoot "MDDocImport"; Remote = "https://sferaved@github.com/Sferaved/MDDocImport.git" },
    @{ Name = "MDDocImport.Access"; Path = Join-Path $userRoot "MDDocImport.Access"; Remote = "https://sferaved@github.com/Sferaved/MDDocImport.Access.git" },
    @{ Name = "MDDocImport.Admin"; Path = Join-Path $userRoot "MDDocImport.Admin"; Remote = "https://sferaved@github.com/Sferaved/MDDocImport.Admin.git" }
)

$taxiRepoPath = $null
foreach ($candidate in @(
        (Join-Path (Split-Path $workspaceRoot -Parent) "PhpstormProjects\taxi_repo"),
        (Join-Path $userRoot "PhpstormProjects\taxi_repo"),
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

function Get-StoredPat {
    param(
        [string]$Username,
        [string]$PathSuffix
    )
    $fillIn = @(
        "protocol=https",
        "host=github.com",
        "username=$Username",
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

function Read-PatFromGithubEnv {
    param([string]$RepoRoot)
    $envFile = Join-Path $RepoRoot "github.env"
    if (-not (Test-Path $envFile)) { return $null }
    foreach ($line in Get-Content $envFile) {
        if ($line -match '^\s*GITHUB_TOKEN\s*=\s*(.+)\s*$') {
            $token = $matches[1].Trim()
            if ($token -and $token -notmatch '^ghp_xxx') {
                return $token
            }
        }
    }
    return $null
}

function Resolve-SferavedPat {
    foreach ($root in @(
            (Join-Path $userRoot "MDDocImport"),
            (Join-Path $userRoot "MDDocImport.Access"),
            (Join-Path $userRoot "MDDocImport.Admin")
        )) {
        $fromEnv = Read-PatFromGithubEnv -RepoRoot $root
        if ($fromEnv) { return $fromEnv }
    }

    foreach ($pathSuffix in @(
            "Sferaved/PAS_4.git",
            "Sferaved/MDDocImport.git",
            "Sferaved/MDDocImport.Access.git",
            "Sferaved/MDDocImport.Admin.git",
            "Sferaved/PAS_3.git",
            "Sferaved/PAS2FINAL.git",
            "Sferaved/TaxiEasyUaMain.git",
            "Sferaved/taxi_driver.git"
        )) {
        $stored = Get-StoredPat -Username "sferaved" -PathSuffix $pathSuffix
        if ($stored) { return $stored }
    }

    return $null
}

function Resolve-TaxiPat {
    $stored = Get-StoredPat -Username "andrey18051" -PathSuffix "andrey18051/taxi_repo.git"
    if ($stored) { return $stored }
    return $null
}

function Save-GitCredential {
    param(
        [string]$Username,
        [string]$Pat,
        [string]$PathSuffix
    )
    if ([string]::IsNullOrWhiteSpace($Pat) -or [string]::IsNullOrWhiteSpace($PathSuffix)) {
        return
    }

    $reject = @(
        "protocol=https",
        "host=github.com",
        "username=$Username",
        "path=$PathSuffix",
        "",
        ""
    ) -join "`n"
    $reject | git credential reject 2>$null | Out-Null

    $approve = @(
        "protocol=https",
        "host=github.com",
        "username=$Username",
        "path=$PathSuffix",
        "password=$Pat",
        "",
        ""
    ) -join "`n"
    $approve | git credential approve | Out-Null
}

function Remove-LegacyGitCredential {
    param([string]$Target)
    $found = cmdkey /list 2>$null | Select-String -SimpleMatch $Target
    if ($found) {
        Write-Host "Removing legacy credential: $Target" -ForegroundColor Yellow
        cmdkey /delete:$Target 2>$null | Out-Null
    }
}

function Remove-StaleGithubCredentialTargets {
    $targets = cmdkey /list 2>$null | Select-String -Pattern 'Target: LegacyGeneric:target=git:https://.*github\.com'
    foreach ($line in $targets) {
        if ($line -match 'target=(.+)$') {
            $target = $matches[1].Trim()
            if ($target -match 'x-access-token@' -or $target -eq 'git:https://github.com') {
                cmdkey /delete:"LegacyGeneric:target=$target" 2>$null | Out-Null
            }
        }
    }
}

Write-Host "===== Git multi-account setup =====" -ForegroundColor Cyan
Write-Host "Workspace: $workspaceRoot" -ForegroundColor Gray

git config --global credential.helper manager
git config --global credential.useHttpPath true
git config --global credential.interactive never 2>$null
Write-Host "OK: credential.helper=manager, useHttpPath=true, interactive=never" -ForegroundColor Green

foreach ($legacyTarget in @(
        "LegacyGeneric:target=git:https://github.com",
        "LegacyGeneric:target=git:https://sferaved@github.com",
        "LegacyGeneric:target=git:https://Sferaved@github.com",
        "LegacyGeneric:target=git:https://andrey18051@github.com"
    )) {
    Remove-LegacyGitCredential -Target $legacyTarget
}
Remove-StaleGithubCredentialTargets
Write-Host "OK: legacy / x-access-token GitHub credentials cleaned." -ForegroundColor Green

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
}
else {
    Write-Host "SKIP: taxi_repo not found (looked under PhpstormProjects)" -ForegroundColor Yellow
}

$useAuto = $Auto -or [bool]$env:SETUP_GIT_SKIP_PAT_PROMPT
$pasPat = $null
$taxiPat = $null

if ($useAuto) {
    $pasPat = Resolve-SferavedPat
    $taxiPat = Resolve-TaxiPat
    if ($pasPat) {
        Write-Host "OK: sferaved PAT loaded from github.env or Credential Manager" -ForegroundColor Green
    }
    if ($taxiPat) {
        Write-Host "OK: andrey18051 PAT loaded from Credential Manager" -ForegroundColor Green
    }
}
else {
    Write-Host ""
    Write-Host "Store Personal Access Tokens (classic, scope: repo)." -ForegroundColor Cyan
    Write-Host "Leave empty to skip - or re-run with -Auto to use saved PAT." -ForegroundColor Gray
    $pasPat = Read-Host "PAT for sferaved@gmail.com (PAS + MDDocImport*)"
    $taxiPat = Read-Host "PAT for andrey18051@gmail.com (taxi_repo)"
}

if ($pasPat) {
    foreach ($repo in $pasRepos) {
        if (-not (Test-Path $repo.Path)) { continue }
        $pathSuffix = ($repo.Remote -replace '^https://sferaved@github.com/', '')
        Save-GitCredential -Username "sferaved" -Pat $pasPat -PathSuffix $pathSuffix
    }
    Write-Host "OK: sferaved PAT registered for all Sferaved/* repos" -ForegroundColor Green
}
elseif ($useAuto) {
    Write-Host "WARN: sferaved PAT not found. Fill github.env in MDDocImport or run without -Auto." -ForegroundColor Yellow
}

if ($taxiPat -and $taxiRepo.Path -and (Test-Path $taxiRepo.Path)) {
    Save-GitCredential -Username "andrey18051" -Pat $taxiPat -PathSuffix "andrey18051/taxi_repo.git"
    Write-Host "OK: andrey18051 PAT registered for taxi_repo" -ForegroundColor Green
}
elseif ($useAuto -and $taxiRepo.Path -and (Test-Path $taxiRepo.Path)) {
    Write-Host "WARN: andrey18051 PAT not found in Credential Manager." -ForegroundColor Yellow
}

$registerScript = Join-Path $PSScriptRoot "register_taxi_driver_git.ps1"
if ((Test-Path $registerScript) -and $pasPat) {
    Write-Host "Syncing Taxi_driver credential..." -ForegroundColor Gray
    & $registerScript
}

Write-Host ""
Write-Host "Done. Git should not open browser / Cursor auth for registered repos." -ForegroundColor Green
