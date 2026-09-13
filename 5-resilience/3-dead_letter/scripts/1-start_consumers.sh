#!/usr/bin/env bash
# Starts one share consumer per subscriber of the mooc context.
# On start, every share group gets its dead letter topic configured: dlq.codely.mooc

docker compose exec test_server_java \
  ./gradlew :bootRun --args='mooc_backend domain-events:kafka:consume'
