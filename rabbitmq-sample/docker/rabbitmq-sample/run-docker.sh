#!/usr/bin/env bash

set -eu

docker-compose up -d

sleep 5s

docker ps
