#! /bin/sh
mkdir -p input output platforms
docker build --pull -t sootkeeper/experiments .
docker run -ti -v $(pwd)/input:/input/ -v $(pwd)/output:/output/ -v $(pwd)/platforms:/platforms/:ro sootkeeper/experiments "$@"
