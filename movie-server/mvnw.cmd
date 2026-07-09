@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM Begin all REM lines with '@' in case MAVEN_BATCH_ECHO is 'on'
@echo off
@REM set title of command window
title %0
@REM enable echoing by setting MAVEN_BATCH_ECHO to 'on'
@if "%MAVEN_BATCH_ECHO%" == "on"  echo %MAVEN_BATCH_ECHO%

@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set "HOME=%HOMEDRIVE%%HOMEPATH%")

@REM Execute a user defined script before this one
if not "%MAVEN_SKIP_RC%" == "" goto skipRcPre
@REM check for pre script, once with legacy .bat ending and once with .cmd ending
if exist "%USERPROFILE%\mavenrc_pre.bat" call "%USERPROFILE%\mavenrc_pre.bat" %*
if exist "%USERPROFILE%\mavenrc_pre.cmd" call "%USERPROFILE%\mavenrc_pre.cmd" %*
:skipRcPre

@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible post scripts, we use another setlocal
@setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

@REM ==== END VALIDATION ====

:init

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir

set EXEC_DIR=%CD%
set WDIR=%EXEC_DIR%
:findBaseDir
IF EXIST "%WDIR%"\.mvn goto baseDirFound
cd ..
IF "%WDIR%"=="%CD%" goto baseDirNotFound
set WDIR=%CD%
goto findBaseDir

:baseDirFound
set MAVEN_PROJECTBASEDIR=%WDIR%
cd "%EXEC_DIR%"
goto endDetectBaseDir

:baseDirNotFound
set MAVEN_PROJECTBASEDIR=%EXEC_DIR%
cd "%EXEC_DIR%"

:endDetectBaseDir

IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config" goto endReadAdditionalConfig

@setlocal EnableExtensions EnableDelayedExpansion
for /F "usebackq delims=" %%a in ("%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config") do set JVM_CONFIG_MAVEN_PROPS=!JVM_CONFIG_MAVEN_PROPS! %%a
@endlocal & set JVM_CONFIG_MAVEN_PROPS=%JVM_CONFIG_MAVEN_PROPS%

:endReadAdditionalConfig

SET MAVEN_JAVA_EXE="%JAVA_HOME%\bin\java.exe"
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

set DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO (
    IF "%%A"=="distributionUrl" SET DISTRIBUTION_URL=%%B
)

@REM Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@REM Setup the command line

set CLASSPATH=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar

set DISTRIBUTION_DIR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-distribution

if NOT EXIST "%DISTRIBUTION_DIR%" goto downloadAndExtract

@REM Maven distribution exists, but check if the version matches
set PROPERTIES_FILE="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties"
if NOT EXIST "%PROPERTIES_FILE%" goto downloadAndExtract

set TEMP_DIST_URL=
for /F "usebackq tokens=1,2 delims==" %%A IN ("%PROPERTIES_FILE%") DO (
    if "%%A"=="distributionUrl" set TEMP_DIST_URL=%%B
)

set MD5_SUMS_FILE=%DISTRIBUTION_DIR%\..\..\checksums\maven-distribution.md5
if EXIST "%MD5_SUMS_FILE%" (
    findstr /C:"%TEMP_DIST_URL%" "%MD5_SUMS_FILE%" >NUL
    if not errorlevel 1 goto runMaven
)

:downloadAndExtract
if NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\temp" mkdir "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\temp"
set TEMP_ZIP=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\temp\maven.zip
if EXIST "%TEMP_ZIP%" del "%TEMP_ZIP%"

echo Downloading Maven from %DISTRIBUTION_URL% ...
%JAVA_EXE% -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain download %DISTRIBUTION_URL% "%TEMP_ZIP%"
if %ERRORLEVEL% neq 0 goto fail

if EXIST "%DISTRIBUTION_DIR%" rmdir /S /Q "%DISTRIBUTION_DIR%"
mkdir "%DISTRIBUTION_DIR%"

echo Extracting Maven...
powershell -Command "Expand-Archive -Path '%TEMP_ZIP%' -DestinationPath '%DISTRIBUTION_DIR%' -Force" >NUL 2>&1
if %ERRORLEVEL% neq 0 goto fail

@REM Move extracted files up one level (maven wrapper extracts to subfolder)
for /D %%d in ("%DISTRIBUTION_DIR%\*") do (
    set EXTRACTED_DIR=%%d
    goto foundExtracted
)

:foundExtracted
if NOT EXIST "%EXTRACTED_DIR%\bin\mvn.cmd" goto fail

del "%TEMP_ZIP%"

:runMaven
set MAVEN_CMD=%EXTRACTED_DIR%\bin\mvn
if EXIST "%MAVEN_CMD%.cmd" set MAVEN_CMD=%MAVEN_CMD%.cmd
if EXIST "%MAVEN_CMD%.bat" set MAVEN_CMD=%MAVEN_CMD%.bat

@REM Workaround for https://issues.apache.org/jira/browse/MWRAPPER-105
set MVNW_VERBOSE=true
set MVNW_VERBOSE_ORIG=%MVNW_VERBOSE%

%MAVEN_CMD% %MAVEN_CONFIG% %*
if %ERRORLEVEL% equ 0 goto end

:fail
echo.
echo ERROR: Maven execution failed.
echo.
exit /b 1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

exit /b %ERROR_CODE%