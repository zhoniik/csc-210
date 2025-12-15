#!/usr/bin/env bash
set -euo pipefail

JAR="lib/junit-platform-console-standalone-1.11.3.jar"

bash ./compile.sh

# Run all tests discovered on the classpath
java -jar "$JAR" \
  -cp "out/main:out/test" \
  --scan-class-path \
  --fail-if-no-tests

