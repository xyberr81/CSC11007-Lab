#!/usr/bin/env bash

set -euo pipefail

if [ "$#" -ne 3 ]; then
  echo "Usage: $0 <service> <dockerhub-username> <tag>" >&2
  exit 1
fi

service="$1"
dockerhub_username="$2"
tag="$3"

case "$service" in
  backoffice)
    manifest_name="backoffice-ui"
    image_name="yas-backoffice"
    section_name="ui"
    ;;
  backoffice-bff)
    manifest_name="backoffice-bff"
    image_name="yas-backoffice-bff"
    section_name="backend"
    ;;
  cart)
    manifest_name="cart"
    image_name="yas-cart"
    section_name="backend"
    ;;
  customer)
    manifest_name="customer"
    image_name="yas-customer"
    section_name="backend"
    ;;
  inventory)
    manifest_name="inventory"
    image_name="yas-inventory"
    section_name="backend"
    ;;
  media)
    manifest_name="media"
    image_name="yas-media"
    section_name="backend"
    ;;
  order)
    manifest_name="order"
    image_name="yas-order"
    section_name="backend"
    ;;
  product)
    manifest_name="product"
    image_name="yas-product"
    section_name="backend"
    ;;
  rating)
    manifest_name="rating"
    image_name="yas-rating"
    section_name="backend"
    ;;
  sampledata)
    manifest_name="sampledata"
    image_name="yas-sampledata"
    section_name="backend"
    ;;
  search)
    manifest_name="search"
    image_name="yas-search"
    section_name="backend"
    ;;
  storefront)
    manifest_name="storefront-ui"
    image_name="yas-storefront"
    section_name="ui"
    ;;
  storefront-bff)
    manifest_name="storefront-bff"
    image_name="yas-storefront-bff"
    section_name="backend"
    ;;
  tax)
    manifest_name="tax"
    image_name="yas-tax"
    section_name="backend"
    ;;
  *)
    echo "Unsupported GitOps deploy service: ${service}" >&2
    exit 1
    ;;
esac

manifest_path="k8s/deploy/dev/services/${manifest_name}.yaml"

if [ ! -f "$manifest_path" ]; then
  echo "Manifest not found: ${manifest_path}" >&2
  exit 1
fi

tmp_file="$(mktemp)"

awk \
  -v target_section="${section_name}:" \
  -v desired_repository="    repository: docker.io/${dockerhub_username}/${image_name}" \
  -v desired_tag="    tag: ${tag}" \
  '
  BEGIN {
    in_section = 0
    in_image = 0
    repository_updated = 0
    tag_updated = 0
  }

  $0 == target_section {
    in_section = 1
    in_image = 0
    print
    next
  }

  in_section && $0 ~ /^[^[:space:]]/ && $0 != target_section {
    in_section = 0
    in_image = 0
  }

  in_section && $0 == "  image:" {
    in_image = 1
    print
    next
  }

  in_image && $0 ~ /^    repository:/ {
    print desired_repository
    repository_updated = 1
    next
  }

  in_image && $0 ~ /^    tag:/ {
    print desired_tag
    tag_updated = 1
    next
  }

  in_image && $0 ~ /^  [^[:space:]]/ && $0 != "  image:" {
    in_image = 0
  }

  {
    print
  }

  END {
    if (!repository_updated || !tag_updated) {
      exit 1
    }
  }
  ' "$manifest_path" >"$tmp_file"

mv "$tmp_file" "$manifest_path"
