@echo off
REM Projeyi derleyip calistirir. Tarayicidan http://localhost:8080 adresini acin.
cd /d "%~dp0.."
call mvnw.cmd spring-boot:run
pause
