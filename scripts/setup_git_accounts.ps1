# One-time / repair setup: separate GitHub accounts for PAS (Sferaved) and taxi_repo (andrey18051).
# Run from any folder: powershell -ExecutionPolicy Bypass -File "...\PAS_4\scripts\setup_git_accounts.ps1"

$ErrorActionPreference = "Stop"

$pasRepos = @(
    @{ Name = "PAS_1"; Path = "C:\Users\Lenovo\AndroidStudioProjects\PAS_1"; Remote = "https://sferaved@github.com/Sferaved/TaxiEasyUaMain.git" },
    @{ Name = "PAS_2"; Path = "C:\Users\Lenovo\AndroidStudioProjects\PAS_2"; Remote = "https://sferaved@github.com/Sferaved/PAS2FINAL.git" },
    @{ Name = "PAS_3"; Path = "C:\Users\Lenovo\AndroidStudioProjects\PAS_3"; Remote = "https://sferaved@github.com/Sferaved/PAS_3.git" },
    @{ Name = "PAS_4"; Path = "C:\Users\Lenovo\AndroidStudioProjects\PAS_4"; Remote = "https://sferaved@github.com/Sferaved/PAS_4.git" }
)
$taxiRepo = @{
    Name = "taxi_repo"
    Path = "C:\Users\Lenovo\PhpstormProjects\taxi_repo"
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

git config --global credential.helper manager
git config --global credential.useHttpPath true
Write-Host "OK: credential.useHttpPath=true (separate tokens per repo path)" -ForegroundColor Green

$legacyTarget = "LegacyGeneric:target=git:https://github.com"
$legacy = cmdkey /list 2>$null | Select-String -SimpleMatch $legacyTarget
if ($legacy) {
    Write-Host "Removing old shared GitHub credential ($legacyTarget)..." -ForegroundColor Yellow
    cmdkey /delete:$legacyTarget 2>$null | Out-Null
    Write-Host "OK: removed. You will store two PATs below." -ForegroundColor Green
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

if (Test-Path $taxiRepo.Path) {
    git -C $taxiRepo.Path remote set-url origin $taxiRepo.Remote
    git -C $taxiRepo.Path config user.email "andrey18051@gmail.com"
    git -C $taxiRepo.Path config user.name "manager"
    Write-Host "OK: taxi_repo -> $($taxiRepo.Remote)" -ForegroundColor Green
} else {
    Write-Host "SKIP: taxi_repo not found" -ForegroundColor Yellow
}

if (-not $env:SETUP_GIT_SKIP_PAT_PROMPT) {
    Write-Host ""
    Write-Host "Store Personal Access Tokens (classic, scope: repo)." -ForegroundColor Cyan
    Write-Host "Leave empty to skip and enter PAT later on first push." -ForegroundColor Gray
    $pasPat = Read-Host "PAT for sferaved@gmail.com (PAS repos)"
    $taxiPat = Read-Host "PAT for andrey18051@gmail.com (taxi_repo)"

    foreach ($repo in $pasRepos) {
        if (-not (Test-Path $repo.Path)) { continue }
        $pathSuffix = ($repo.Remote -replace '^https://sferaved@github.com/', '')
        Save-GitCredential -Username "sferaved" -Pat $pasPat -PathSuffix $pathSuffix
    }
    if ((Test-Path $taxiRepo.Path) -and -not [string]::IsNullOrWhiteSpace($taxiPat)) {
        Save-GitCredential -Username "andrey18051" -Pat $taxiPat -PathSuffix "andrey18051/taxi_repo.git"
    }
}

Write-Host ""
Write-Host "Verify:" -ForegroundColor Cyan
Write-Host "  PAS_4:  git -C C:\Users\Lenovo\AndroidStudioProjects\PAS_4 push --dry-run"
Write-Host "  taxi:   git -C C:\Users\Lenovo\PhpstormProjects\taxi_repo push --dry-run"
Write-Host "Done." -ForegroundColor Green
