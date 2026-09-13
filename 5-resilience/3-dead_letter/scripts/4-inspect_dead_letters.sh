#!/usr/bin/env bash
# Reads the dead letter topic of the mooc context from the beginning with a plain consumer.
# Prints the __dlq.errors.* headers written by the broker, then the key and the body.
# Press Ctrl+C to stop.

docker compose exec shared_kafka \
  /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server shared_kafka:29092 \
                                           --topic dlq.codely.mooc \
                                           --from-beginning \
                                           --formatter-property print.headers=true \
                                           --formatter-property print.key=true \
                                           --formatter-property key.separator=$'\t'
