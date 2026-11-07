@echo off
rem ===========================================================================
rem Ripristina l'ultimo backup sofie-base-*.backup (vedi restore_sofie_base.ps1).
rem
rem   restore_sofie_base.bat                          su sofie, dopo conferma
rem   restore_sofie_base.bat -Database sofie_test     su una copia
rem   restore_sofie_base.bat -Backup ddl\sofie-base-20261107.backup -Force
rem
rem Su sofie i servizi devono essere spenti.
rem ===========================================================================
setlocal
set "SCRIPT=%~dp0restore_sofie_base.ps1"

if not "%~1"=="" (
    powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT%" %*
    exit /b %errorlevel%
)

echo The sofie database will be DROPPED and restored from the latest sofie-base backup.
choice /M "Continue"
if errorlevel 2 (
    echo Cancelled.
    pause
    exit /b 1
)
powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT%" -Force
set "RESULT=%errorlevel%"
pause
exit /b %RESULT%
