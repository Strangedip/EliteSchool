#!/bin/sh
# Pushes every YAML file in ./config into Consul KV at config/<name>/data,
# which is where spring-cloud-consul-config (format=yaml) expects it.
set -e

CONSUL_ADDR="${CONSUL_HTTP_ADDR:-http://localhost:8500}"
CONFIG_DIR="$(dirname "$0")/config"

for file in "$CONFIG_DIR"/*.yml; do
  name=$(basename "$file" .yml)
  echo "Seeding config/$name/data from $file"
  consul kv put -http-addr="$CONSUL_ADDR" "config/$name/data" @"$file"
done

echo "Consul KV seed complete."
