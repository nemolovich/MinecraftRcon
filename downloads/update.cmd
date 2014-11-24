@echo off
setLOCAL

set RUN_FILE=run.bat

IF "%2"=="" GOTO ERROR

set OLD_JAR=%1
set UPDATE_JAR=%2

set ARGS=

:LOOP
IF "%3"=="" GOTO CONTINUE
set ARGS=%ARGS% %3
SHIFT
GOTO LOOP
:CONTINUE

IF not EXIST %OLD_JAR% GOTO ERROR
IF not EXIST %UPDATE_JAR% GOTO ERROR
IF not EXIST %RUN_FILE% GOTO ERROR

set TRIES=0
:RETRY
set /A TRIES+=1
IF %TRIES% GTR 1 IF %TRIES% LEQ 30 (TIMEOUT /T 1 >nul)
IF %TRIES% GTR 30 GOTO ERROR
move %OLD_JAR% %OLD_JAR%_tmp
IF not EXIST %OLD_JAR%_tmp GOTO RETRY
move %UPDATE_JAR% %OLD_JAR%
echo Running %RUN_FILE% %ARGS%
START %RUN_FILE% %ARGS%
del %OLD_JAR%_tmp

GOTO EOF
:ERROR
echo =============================================
echo === Ended with some errors                ===
echo =============================================
GOTO FINALLY

:EOF
echo =============================================
echo === Update terminated                     ===
echo =============================================
:FINALLY
ENDLOCAL
exit