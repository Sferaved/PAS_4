@echo off
chcp 65001 >nul
cd /d "%~dp0.."
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup_git_accounts.ps1" -Auto
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

for %%R in (MDDocImport MDDocImport.Access MDDocImport.Admin) do (
  if exist "%USERPROFILE%\%%R\setup-git-auth.bat" (
    echo === %%R setup-git-auth ===
    pushd "%USERPROFILE%\%%R"
    call setup-git-auth.bat
    popd
  )
)

echo.
echo All GitHub credentials repaired.
pause
