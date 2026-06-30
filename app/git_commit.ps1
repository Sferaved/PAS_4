Write-Host "===== PAS_4 - Version Update =====" -ForegroundColor Cyan
Write-Host ""

# Скрипт находится в PAS_4/app/git_commit.ps1
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent $scriptDir  # Поднимаемся на уровень выше (PAS_4)
Set-Location $projectRoot

Write-Host "Project root: $projectRoot" -ForegroundColor Gray

$gradleFile = Join-Path $projectRoot "app/build.gradle"

if (!(Test-Path $gradleFile)) {
    Write-Host "ERROR: $gradleFile not found" -ForegroundColor Red
    exit 1
}

# ===== 0. Pre-release checks (unit tests + release build) =====
Write-Host "===== Pre-release checks =====" -ForegroundColor Cyan

if (-not $env:JAVA_HOME -or -not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $javaCandidates = @(
        "$env:LOCALAPPDATA\Programs\Android\Android Studio\jbr",
        "C:\Program Files\Android\Android Studio\jbr",
        "C:\Program Files\Java\jdk-21"
    )
    $foundJava = $false
    foreach ($candidate in $javaCandidates) {
        if (Test-Path "$candidate\bin\java.exe") {
            $env:JAVA_HOME = $candidate
            Write-Host "JAVA_HOME: $candidate" -ForegroundColor Gray
            $foundJava = $true
            break
        }
    }
    if (-not $foundJava) {
        Write-Host "ERROR: JAVA_HOME not set and Java not found" -ForegroundColor Red
        exit 1
    }
}

$gradlew = Join-Path $projectRoot "gradlew.bat"
if (-not (Test-Path $gradlew)) {
    Write-Host "ERROR: gradlew.bat not found" -ForegroundColor Red
    exit 1
}

Write-Host "Running unit tests (testDebugUnitTest)..." -ForegroundColor Yellow
& $gradlew testDebugUnitTest --no-daemon
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: unit tests failed - release cancelled" -ForegroundColor Red
    exit 1
}
Write-Host "OK: unit tests passed" -ForegroundColor Green

Write-Host "Running release build check (assembleRelease)..." -ForegroundColor Yellow
& $gradlew assembleRelease --no-daemon
if ($LASTEXITCODE -ne 0) {
    Write-Host "assembleRelease failed, trying compileReleaseJavaWithJavac..." -ForegroundColor Yellow
    & $gradlew compileReleaseJavaWithJavac --no-daemon
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: release build check failed - deploy cancelled" -ForegroundColor Red
        exit 1
    }
}
Write-Host "OK: release build check passed" -ForegroundColor Green
Write-Host ""

# ===== 1. Read build.gradle =====
$content = Get-Content $gradleFile -Raw -Encoding UTF8

$versionCodeMatch = [regex]::Match($content, "versionCode\s*=\s*(\d+)")
$versionNameMatch = [regex]::Match($content, "versionName\s*=\s*'([^']+)'")

if (!$versionCodeMatch.Success -or !$versionNameMatch.Success) {
    Write-Host "ERROR: Cannot find versionCode or versionName" -ForegroundColor Red
    exit 1
}

$currentVersionCode = [int]$versionCodeMatch.Groups[1].Value
$currentVersionName = $versionNameMatch.Groups[1].Value

Write-Host "Current versionCode: $currentVersionCode"
Write-Host "Current versionName: $currentVersionName"
Write-Host ""

# ===== 2. Increment version =====
$newVersionCode = $currentVersionCode + 1
# ВАЖНО: первая цифра версии фиксированная "4.", а хвост идёт 999 -> 1000 -> 1001 ...
# Для PAS_4 исторически versionCode = 4xxx, а отображаемая часть = versionCode - 4000
$visibleSuffix = $newVersionCode - 4000
if ($visibleSuffix -lt 0) {
    Write-Host "ERROR: versionCode too small for PAS_4 scheme (expected >= 4000)" -ForegroundColor Red
    exit 1
}
$newVersionName = "4.$visibleSuffix"

Write-Host "New versionCode: $newVersionCode"
Write-Host "New versionName: $newVersionName"
Write-Host ""

# ===== 3. Update build.gradle =====
$content = $content -replace "versionCode\s*=\s*\d+", "versionCode = $newVersionCode"
$content = $content -replace "versionName\s*=\s*'[^']+'", "versionName = '$newVersionName'"

$utf8NoBom = New-Object System.Text.UTF8Encoding $false

try {
    [System.IO.File]::WriteAllText($gradleFile, $content, $utf8NoBom)
    Write-Host "OK: build.gradle updated" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Failed to write build.gradle: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# ===== 4. Update XML files =====
# Исправлено: создаем массив правильно
$xmlFiles = @(
    "app/src/main/res/values/strings_app.xml",
    "app/src/main/res/values-ru/strings_app.xml",
    "app/src/main/res/values-uk/strings_app.xml",
    "app/src/main/res/values-en/strings_app.xml"
)

foreach ($file in $xmlFiles) {
    $fullPath = Join-Path $projectRoot $file

    if (!(Test-Path $fullPath)) {
        Write-Host "WARNING: File not found: $fullPath" -ForegroundColor Yellow
        continue
    }

    Write-Host "Updating: $fullPath"

    try {
        $content = Get-Content $fullPath -Raw -Encoding UTF8

        # Обновляем version_code
        $content = $content -replace '(?<=<string name="version_code">).*?(?=</string>)', $newVersionName

        # Обновляем version в зависимости от языка
        if ($file -like "*values-ru*") {
            $content = $content -replace '(?<=<string name="version">).*?(?=</string>)', "# $newVersionName"
        }
        elseif ($file -like "*values-uk*") {
            $content = $content -replace '(?<=<string name="version">).*?(?=</string>)', "# $newVersionName"
        }
        elseif ($file -like "*values-en*") {
            $content = $content -replace '(?<=<string name="version">).*?(?=</string>)', "# $newVersionName"
        }
        else {
            $content = $content -replace '(?<=<string name="version">).*?(?=</string>)', "# $newVersionName"
        }

        [System.IO.File]::WriteAllText($fullPath, $content, $utf8NoBom)
        Write-Host "   OK: updated" -ForegroundColor Green
    } catch {
        Write-Host "   ERROR: Failed to update $fullPath : $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "SUCCESS: Version updated to $newVersionName ($newVersionCode)" -ForegroundColor Cyan
Write-Host ""

# ===== 5. Build signed AAB and publish to Google Play =====
Write-Host "===== Release bundle =====" -ForegroundColor Cyan
Write-Host "Running bundleRelease..." -ForegroundColor Yellow
& $gradlew bundleRelease --no-daemon
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: bundleRelease failed - revert version in build.gradle manually" -ForegroundColor Red
    exit 1
}
Write-Host "OK: bundleRelease passed" -ForegroundColor Green

$publishScript = Join-Path $scriptDir "publish-google-play.ps1"
if (-not (Test-Path $publishScript)) {
    Write-Host "ERROR: publish-google-play.ps1 not found" -ForegroundColor Red
    exit 1
}
Write-Host "Publishing to Google Play..." -ForegroundColor Yellow
$pwsh = Get-Command pwsh -ErrorAction SilentlyContinue
if ($pwsh) {
    & pwsh -NoProfile -ExecutionPolicy Bypass -File $publishScript -ProjectRoot $projectRoot
} else {
    & powershell -NoProfile -ExecutionPolicy Bypass -File $publishScript -ProjectRoot $projectRoot
}
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Google Play publish failed - git push skipped" -ForegroundColor Red
    exit 1
}
Write-Host "OK: Google Play publish completed" -ForegroundColor Green
Write-Host ""

# ============================================================
# ===================== GIT BLOCK ============================
# ============================================================

Write-Host "===== Git operations =====" -ForegroundColor Yellow

# Проверяем, что мы в git репозитории
if (!(Test-Path (Join-Path $projectRoot ".git"))) {
    Write-Host "ERROR: Not a git repository!" -ForegroundColor Red
    exit 1
}

# Показываем изменения
Write-Host "`nChanged files:" -ForegroundColor Cyan
git status --short | Out-Host

Write-Host "`nFull list of changes:" -ForegroundColor Cyan
git diff --stat | Out-Host

#$confirm = Read-Host "`nDo you want to commit these changes? (y/n)"
$confirm = 'y'
if ($confirm -ne 'y' -and $confirm -ne 'Y') {
    Write-Host "Git operations cancelled." -ForegroundColor Yellow
    exit 0
}

Write-Host "`nAdding changes..." -ForegroundColor Yellow
git add .
foreach ($secret in @('keystore/service-account.json', 'app/keystore/service-account.json', 'keystore.properties')) {
    git reset HEAD -- $secret 2>$null | Out-Null
}

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: git add failed" -ForegroundColor Red
    exit 1
}

$status = git status --porcelain
if ([string]::IsNullOrEmpty($status)) {
    Write-Host "No changes to commit." -ForegroundColor Yellow
    exit 0
}

$commitMessage = "Bump version to $newVersionName ($newVersionCode)"
Write-Host "Committing: $commitMessage" -ForegroundColor Yellow
git commit -m $commitMessage

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: git commit failed" -ForegroundColor Red
    exit 1
}

$tagName = "v$newVersionName"
Write-Host "Creating tag: $tagName" -ForegroundColor Yellow
git tag $tagName

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: tag creation failed" -ForegroundColor Red
    exit 1
}

#$pushConfirm = Read-Host "`nDo you want to push changes and tag to remote? (y/n)"
$pushConfirm = 'y'
if ($pushConfirm -eq 'y' -or $pushConfirm -eq 'Y') {
    Write-Host "Pushing commit..." -ForegroundColor Yellow
    git push

    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: git push failed" -ForegroundColor Red
        exit 1
    }

    Write-Host "Pushing tag..." -ForegroundColor Yellow
    git push origin $tagName

    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: tag push failed" -ForegroundColor Red
        exit 1
    }

    Write-Host "OK: Git push completed, tag $tagName created and pushed" -ForegroundColor Green
    Write-Host "Release is local-only (Google Play already updated). GitHub Actions not triggered." -ForegroundColor Gray
} else {
    Write-Host "OK: Commit and tag created locally. Push skipped." -ForegroundColor Green
    Write-Host "To push later use: git push && git push origin $tagName/actions" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Done." -ForegroundColor Green