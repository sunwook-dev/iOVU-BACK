@echo off
echo ===============================================
echo         iOVU Backend Server Startup
echo ===============================================
echo.

REM 환경 변수 파일 확인
if not exist ".env" (
    echo [WARNING] .env file not found!
    echo Please copy .env.example to .env and configure your settings.
    echo.
    if exist ".env.example" (
        echo Do you want to copy .env.example to .env? (y/n)
        set /p choice=
        if /i "%choice%"=="y" (
            copy ".env.example" ".env"
            echo .env file created. Please edit it with your actual values.
            echo Opening .env file for editing...
            notepad .env
            pause
        ) else (
            echo Please create .env file manually and restart.
            pause
            exit /b 1
        )
    ) else (
        echo .env.example file not found. Please check your project structure.
        pause
        exit /b 1
    )
)

echo [INFO] Checking Java version...
java -version 2>nul
if errorlevel 1 (
    echo [ERROR] Java is not installed or not in PATH.
    echo Please install Java 17 or higher.
    pause
    exit /b 1
)
echo.

echo [INFO] Checking MySQL connection...
echo Please ensure MySQL is running on localhost:3306
echo.

echo [INFO] Building project...
call gradlew.bat clean build --no-daemon
if errorlevel 1 (
    echo [ERROR] Build failed. Please check the error messages above.
    pause
    exit /b 1
)
echo.

echo [INFO] Starting iOVU Backend Server...
echo Server will be available at: http://localhost:8081
echo.
echo OAuth2 Test URLs:
echo - Google: http://localhost:8081/oauth2/authorization/google
echo - Kakao:  http://localhost:8081/oauth2/authorization/kakao
echo - Naver:  http://localhost:8081/oauth2/authorization/naver
echo.
echo Press Ctrl+C to stop the server
echo ===============================================
echo.

call gradlew.bat bootRun --no-daemon 