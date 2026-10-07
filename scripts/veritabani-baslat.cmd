@echo off
REM PostgreSQL sunucusunu baslatir (Windows servisi kayitli olmadigi icin elle baslatilir).
set PGBIN=C:\Program Files\PostgreSQL\18\bin
set PGDATA=C:\Users\ranae\pgdata

"%PGBIN%\pg_ctl.exe" -D "%PGDATA%" -l "%PGDATA%\server.log" start
echo.
echo PostgreSQL baslatildi. Durum:
"%PGBIN%\pg_ctl.exe" -D "%PGDATA%" status
pause
