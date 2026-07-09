#!/usr/bin/env bash

set -euo pipefail

if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <branch>" >&2
  exit 1
fi

branch="$1"

if [ "$branch" = "main" ]; then
  echo "main"
  exit 0
fi

git fetch --no-tags origin "refs/heads/${branch}:refs/remotes/origin/${branch}" >/dev/null 2>&1
git rev-parse --short "refs/remotes/origin/${branch}"
