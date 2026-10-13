@echo off
SETLOCAL
SET MAVEN_PROJECTBASEDIR=%~dp0
REM remove trailing backslash
IF "%MAVEN_PROJECTBASEDIR:~-1%"=="\" SET MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%
SET WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
IF NOT EXIST "%WRAPPER_JAR%" (
  echo maven-wrapper.jar missing
  EXIT /B 1
)
java -cp "%WRAPPER_JAR%" -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*

