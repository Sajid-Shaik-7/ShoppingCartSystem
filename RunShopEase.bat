@echo off
title ShopEase Launcher
cd /d "%~dp0"
echo Starting ShopEase...
java ShoppingApp
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Could not start the app.
    echo Make sure Java is installed: https://adoptium.net
    pause
)
