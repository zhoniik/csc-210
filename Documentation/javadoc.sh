#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
rm -rf docs
mkdir -p docs
# Generate Javadoc for the com.example packages
javadoc \
  -d docs \
  -sourcepath src \
  -subpackages com.example \
  -author -version -use -linksource \
  -encoding UTF-8 -charset UTF-8
echo "Javadoc generated in docs/"
