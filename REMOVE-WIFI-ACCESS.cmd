@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\configure-wifi-access.ps1" -Remove
if errorlevel 1 pause
