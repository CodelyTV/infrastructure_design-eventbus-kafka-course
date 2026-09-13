#!/usr/bin/env bash
# Shows how the dead letter queue is configured in the broker:
# 1. The dead letter topic and its errors.deadletterqueue.group.enable config.
# 2. The dead letter configs of every share group of the mooc context.
# 3. The state of the share groups.

kafka() {
  docker compose exec shared_kafka /opt/kafka/bin/"$1" --bootstrap-server shared_kafka:29092 "${@:2}"
}

echo "== Dead letter topic"
kafka kafka-configs.sh --describe --entity-type topics --entity-name dlq.codely.mooc

echo
echo "== Share groups"
for group in $(kafka kafka-share-groups.sh --list); do
  echo "-- $group"
  kafka kafka-configs.sh --describe --entity-type groups --entity-name "$group" | grep -v "^Dynamic configs for group"
done

echo
echo "== Share groups offsets"
kafka kafka-share-groups.sh --describe --all-groups --offsets
