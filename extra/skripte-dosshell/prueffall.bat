@echo off

call ..\..\..\skripte-dosshell\einstellungen.bat

set cp=..\..\de.bsvrz.sys.funclib.bitctrl\de.bsvrz.sys.funclib.bitctrl-runtime.jar
set cp=%cp%;..\de.bsvrz.dua.langfehlerlve-runtime.jar
set cp=%cp%;..\de.bsvrz.dua.langfehlerlve-test.jar
set cp=%cp%;..\..\junit-4.1.jar

title Pruefungen SE4 - DUA, SWE 4.DELzFh

echo ========================================================
echo # Pruefungen SE4 - DUA, SWE 4.DELzFh
echo #
echo # Automatischer JUnit-Test
echo ========================================================
echo.

%java% -cp %cp% org.junit.runner.JUnitCore de.bsvrz.dua.langfehlerlve.DELzFhTester
pause
