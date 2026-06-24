$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $projectRoot

$gradleFile = Join-Path $projectRoot 'app/build.gradle'
$content = Get-Content $gradleFile -Raw -Encoding UTF8
$currentVersionCode = [int]([regex]::Match($content, 'versionCode\s*=\s*(\d+)')).Groups[1].Value
$newVersionCode = $currentVersionCode + 1
$visibleSuffix = $newVersionCode - 4000
$newVersionName = "4.$visibleSuffix"

Write-Host "Bumping to $newVersionName ($newVersionCode)"

$content = $content -replace 'versionCode\s*=\s*\d+', "versionCode = $newVersionCode"
$content = $content -replace "versionName\s*=\s*'[^']+'", "versionName = '$newVersionName'"
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllText($gradleFile, $content, $utf8NoBom)

$xmlFiles = @(
    'app/src/main/res/values/strings_app.xml',
    'app/src/main/res/values-ru/strings_app.xml',
    'app/src/main/res/values-uk/strings_app.xml',
    'app/src/main/res/values-en/strings_app.xml'
)

foreach ($file in $xmlFiles) {
    $fullPath = Join-Path $projectRoot $file
    if (!(Test-Path $fullPath)) { continue }
    $xml = Get-Content $fullPath -Raw -Encoding UTF8
    $xml = $xml -replace '(?<=<string name="version_code">).*?(?=</string>)', $newVersionName
    $xml = $xml -replace '(?<=<string name="version">).*?(?=</string>)', "# $newVersionName"
    [System.IO.File]::WriteAllText($fullPath, $xml, $utf8NoBom)
}

git add .
$commitMessage = "Bump version to $newVersionName ($newVersionCode)"
git commit -m $commitMessage
$tagName = "v$newVersionName"
git tag $tagName
git push
git push origin $tagName
Write-Host "Done: $tagName"
