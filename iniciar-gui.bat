@echo off
chcp 65001 > nul
echo ========================================================
echo   Iniciando Interfaz Grafica Swing - Demo de Patrones
echo ========================================================
echo.
java -cp target\classes pe.utp.tienda.gui.DemoGUI
if %ERRORLEVEL% NEQ 0 (
    echo Compilando proyecto...
    call mvn compile
    java -cp target\classes pe.utp.tienda.gui.DemoGUI
)
pause
