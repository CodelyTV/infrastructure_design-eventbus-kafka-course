#!/usr/bin/env bash
# Helpers to publish domain events with the Kafka console producer.

uuid() {
  uuidgen | tr '[:upper:]' '[:lower:]'
}

publish_raw() {
  topic=$1
  key=$2
  body=$3

  echo "Publishing <$topic> with key <$key>"

  printf '%s\t%s\n' "$key" "$body" |
    docker compose exec -T shared_kafka \
      /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server shared_kafka:29092 \
                                               --topic "$topic" \
                                               --reader-property parse.key=true \
                                               --reader-property key.separator=$'\t' 2>/dev/null
}

publish() {
  topic=$1
  aggregate_id=$2
  attributes=$3
  event_id=$(uuid)
  occurred_on=$(date -u '+%Y-%m-%d %H:%M:%S')

  publish_raw "$topic" "$aggregate_id" \
    "$(printf '{"data":{"id":"%s","type":"%s","occurred_on":"%s","attributes":{"id":"%s",%s}},"meta":{}}' \
      "$event_id" "$topic" "$occurred_on" "$aggregate_id" "$attributes")"
}
