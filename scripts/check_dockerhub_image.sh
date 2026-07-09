#!/usr/bin/env bash

set -euo pipefail

if [ "$#" -ne 2 ]; then
  echo "Usage: $0 <image-repository> <tag>" >&2
  exit 1
fi

image_repository="$1"
tag="$2"

docker manifest inspect "${image_repository}:${tag}" >/dev/null 2>&1
