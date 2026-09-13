#!/usr/bin/env bash
# Starts the dead letter consumer of the mooc context.
# It reads dlq.codely.mooc with the share group codely.mooc.dead-letter and logs every dead letter.

docker compose exec test_server_java \
  ./gradlew :bootRun --args='mooc_backend domain-events:kafka:dead-letter:consume'
