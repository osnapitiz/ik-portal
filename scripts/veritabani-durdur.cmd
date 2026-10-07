@echo off
set PGBIN=C:\Program Files\PostgreSQL\18\bin
set PGDATA=C:\Users\ranae\pgdata

"%PGBIN%\pg_ctl.exe" -D "%PGDATA%" -m fast stop
pause
