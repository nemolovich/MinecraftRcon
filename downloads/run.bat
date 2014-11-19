@echo off
SETLOCAL

set APPLI_VERSION=0.1
set APPLI_NAME=MinecraftRcon-%APPLI_VERSION%

echo =============================================
echo === Nemolovich Minecraft RCON Application ===
echo === Version: %APPLI_VERSION%                ===
echo =============================================
echo .

set APPLI_HOME=%~dp0
set PARMS=-Xms64M -Xmx512M

if "%1"=="--debug" if "%2" NEQ "" (
	set PARMS=%PARMS% -Xdebug -Xrunjdwp:transport=dt_socket,server=n,address=%2
)

if not defined JAVA_HOME goto CHECKJAVA
IF EXIST "%JAVA_HOME%\bin\java.exe" goto JAVA_OK
echo === JAVA_HOME specified but not useable %JAVA_HOME%
echo === looking for Java in standard places
echo .

:CHECKJAVA
set PROGRAMS=%ProgramFiles%
if defined ProgramFiles(x86) set PROGRAMS32=%ProgramFiles(x86)%

IF not EXIST "%PROGRAMS%\Java\jdk7" goto JAVA6
set JAVA_HOME=%PROGRAMS%\Java\jdk7
goto JAVA_OK

:JAVA6
IF not EXIST "%PROGRAMS%\Java\jre6" goto JAVANO
set JAVA_HOME=%PROGRAMS%\Java\jre6
goto JAVA_OK

:JAVANO
if not defined %PROGRAMS32% goto JAVANO32

IF not EXIST "%PROGRAMS32%\Java\jre7" goto JAVA6
set JAVA_HOME=%PROGRAMS32%\Java\jre7
goto JAVA_OK

:JAVA6
IF not EXIST "%PROGRAMS32%\Java\jre6" goto JAVANO32
set JAVA_HOME=%PROGRAMS32%\Java\jre6
goto JAVA_OK

:JAVANO32
echo === Java not found in standard places %PROGRAMS% or %PROGRAMS32%
echo === JAVA_HOME not specified
goto STOPIT

:JAVA_OK 
echo === Running this Java
set APPLI_COMMAND=%*
"%JAVA_HOME%\bin\java.exe" -version
PATH=%APPLI_HOME%libs;%PATH%
echo === Trying to start Nemolovich Minecraft RCON Application
echo === Using: %PARMS% -jar %APPLI_HOME%%APPLI_NAME%.jar %APPLI_COMMAND%
"%JAVA_HOME%\bin\java.exe" %PARMS% -jar "%APPLI_HOME%%APPLI_NAME%.jar" %APPLI_COMMAND%

GOTO FINALLY
:STOPIT
echo =============================================
echo.=== Ended with some errors                ===
echo =============================================

:FINALLY
echo =============================================
echo.=== Application terminated                ===
echo =============================================
ENDLOCAL