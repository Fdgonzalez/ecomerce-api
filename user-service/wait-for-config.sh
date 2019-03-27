#!/bin/sh
# wait-for-config.sh

set -e

host="$1"
shift
cmd="$@"

until curl -f http://"$host":8088/discovery-service/default; do
  >&2 echo "config is unavailable - sleeping"
  sleep 5
done

>&2 echo "config is up - executing command"
exec $cmd
