#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [ ! -d out ]; then
  echo "out/ not found. Run ./compile.sh first." >&2
  exit 1
fi
java -cp out com.example.app.Main
