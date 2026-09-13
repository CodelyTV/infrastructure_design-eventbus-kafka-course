docker compose exec shared_kafka \
  /bin/kafka-topics --describe \
                    --bootstrap-server shared_kafka:29092
