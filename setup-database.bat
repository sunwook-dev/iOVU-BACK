@echo off
echo ========================================
echo Setting up iOVU Database
echo ========================================
echo.

echo This script will set up the MySQL database for iOVU
echo Make sure MySQL is running and accessible
echo.

set /p MYSQL_USER=Enter MySQL username (default: root): 
if "%MYSQL_USER%"=="" set MYSQL_USER=root

set /p MYSQL_PASS=Enter MySQL password (default: iovu1234): 
if "%MYSQL_PASS%"=="" set MYSQL_PASS=iovu1234

echo.
echo Creating database and tables...
mysql -u %MYSQL_USER% -p%MYSQL_PASS% < setup-database.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo Database setup completed successfully!
    echo ========================================
) else (
    echo.
    echo ========================================
    echo Database setup failed!
    echo Please check your MySQL connection
    echo ========================================
)

echo.
pause 