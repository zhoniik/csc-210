#!/usr/bin/env bash
set -euo pipefail

JAR="lib/junit-platform-console-standalone-1.11.3.jar"

# If you want auto-download, uncomment one of the curl lines and paste the URL you trust.
# mkdir -p lib
# [ -f "$JAR" ] || curl -L -o "$JAR" "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.3/junit-platform-console-standalone-1.11.3.jar"

mkdir -p out/main out/test out/myprog
mkdir -p tmp

# Compile main
find src/main -name "*.java" > ./tmp/main_sources.txt
javac -d out/main @./tmp/main_sources.txt

# Compile myprog
find src/myprog -name "*.java" > ./tmp/myprog_sources.txt
javac -cp out/main -d out/myprog @./tmp/myprog_sources.txt

# Compile tests (need junit api on classpath)
find src/test -name "*.java" > ./tmp/test_sources.txt
javac -cp "out/main:$JAR" -d out/test @./tmp/test_sources.txt

echo "Compiled main -> out/main, tests -> out/test"

