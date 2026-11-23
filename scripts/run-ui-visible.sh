#!/usr/bin/env bash
set -euo pipefail

project_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
display_auth=${XAUTHORITY:-$HOME/.Xauthority}

if [[ -z ${DISPLAY:-} || ! -r "$display_auth" ]]; then
    echo "Run this script from a desktop terminal with DISPLAY and XAUTHORITY available." >&2
    exit 1
fi

docker build -t qa-automation-tests "$project_dir"

exec docker run --rm \
    --hostname "$(hostname)" \
    -e DISPLAY \
    -e XAUTHORITY=/tmp/qa-xauthority \
    -v /tmp/.X11-unix:/tmp/.X11-unix:ro \
    -v "$display_auth:/tmp/qa-xauthority:ro" \
    qa-automation-tests \
    mvn -B test -Dgroups=ui -Dselenide.headless=false "$@"
