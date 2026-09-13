#!/usr/bin/env bash

docker compose exec shared_kafka \
  /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server shared_kafka:29092 \
                                           --topic dlq.codely.mooc \
                                           --from-beginning \
                                           --formatter-property print.headers=true \
                                           --formatter-property print.key=true \
                                           --formatter-property key.separator=$'\t'
