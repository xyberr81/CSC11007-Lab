
set -x

#Read configuration value from cluster-config.yaml file
read -rd '' REDIS_PASSWORD \
< <(yq -r '.redis.password' ./cluster-config.yaml)

helm install redis \
  --set auth.password="$REDIS_PASSWORD" \
  --set architecture=standalone \
  --set master.persistence.enabled=false \
  oci://registry-1.docker.io/bitnamicharts/redis -n redis --create-namespace
