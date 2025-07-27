@echo off
echo Generating Javadoc for NutriSci Application...

REM Create docs directory if it doesn't exist
if not exist "docs" mkdir docs

REM Set classpath for external libraries
set CLASSPATH=lib\*;target\classes

REM Generate Javadoc
"D:\Program Files\Java\jdk-24\bin\javadoc.exe" -d docs ^
    -sourcepath src\main\java ^
    -subpackages com.nutrisci ^
    -classpath "%CLASSPATH%" ^
    -author ^
    -version ^
    -use ^
    -windowtitle "NutriSci API Documentation" ^
    -doctitle "NutriSci Nutrition Tracking Application" ^
    -header "NutriSci v1.0" ^
    -footer "Copyright © 2025 NutriSci Development Team" ^
    -overview src\main\java\overview.html ^
    -charset UTF-8 ^
    -docencoding UTF-8 ^
    -link https://docs.oracle.com/en/java/javase/11/docs/api/ ^
    -linkoffline https://junit.org/junit5/docs/current/api/ lib\

echo.
echo Javadoc generation complete!
echo Open docs\index.html in your browser to view the documentation.
pause
