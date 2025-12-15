#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p out
# Compile all sources into the out/ directory
javac -d out $(find src -name "*.java")
echo "Compiled to out/"
