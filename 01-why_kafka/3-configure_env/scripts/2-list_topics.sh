docker exec -it 3-configure_env-kafka-1 \
  /bin/kafka-topics --list \
                    --bootstrap-server kafka:29092
