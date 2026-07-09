#!/usr/bin/env bash

set -euo pipefail

actor="${1:-}"
commit_message="${2:-}"

if [[ "$commit_message" == *"[skip gitops ci]"* ]]; then
  echo "true"
  exit 0
fi

if [[ "$actor" == *"[bot]"* ]]; then
  echo "true"
  exit 0
fi

echo "false"
