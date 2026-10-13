#!/usr/bin/env sh
MAVEN_PROJECTBASEDIR=$(cd "$(dirname "$0")" && pwd)
WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
if [ ! -f "$WRAPPER_JAR" ]; then
  echo "maven-wrapper.jar missing"
  exit 1
fi
exec java -cp "$WRAPPER_JAR" -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" org.apache.maven.wrapper.MavenWrapperMain "$@"

