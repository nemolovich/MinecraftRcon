@echo off
setLOCAL

set RUN_FILE=run.bat

IF "%~2"=="" (
	call :ERROR "Invalid arguments"
	GOTO FINALLY
)

set OLD_JAR=%~1
set UPDATE_JAR=%~2

set ARGS=

:LOOP
IF "%~3"=="" GOTO CONTINUE
set ARGS=%ARGS% "%~3"
SHIFT
GOTO LOOP
:CONTINUE

IF not EXIST %OLD_JAR% (
	call :ERROR "Can not find previous JAR version"
	GOTO FINALLY
)
IF not EXIST %UPDATE_JAR% (
	call :ERROR "Can not find new JAR version"
	GOTO FINALLY
)
IF not EXIST %RUN_FILE% (
	call :ERROR "Can not find running file"
	GOTO FINALLY
)

set TRIES=0
:RETRY
set /A TRIES+=1
IF %TRIES% GTR 1 IF %TRIES% LEQ 30 (TIMEOUT /T 1 >nul)
IF %TRIES% GTR 30 (
	call :ERROR "The previous JAR version can not be moved"
	GOTO FINALLY
)
move %OLD_JAR% %OLD_JAR%_tmp
IF not EXIST %OLD_JAR%_tmp GOTO RETRY
move %UPDATE_JAR% %OLD_JAR%
echo Running %RUN_FILE% %ARGS%
START %RUN_FILE% %ARGS%
del %OLD_JAR%_tmp

GOTO EOF
:ERROR
set ERR_MSG=Error: %~1
echo %ERR_MSG%>UpdateProcessError.log
echo %ERR_MSG%
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