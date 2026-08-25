docker exec -it 4-configure_env-kafka-1 \
  /bin/kafka-console-consumer --bootstrap-server kafka:29092 \
                              --include 'codely.test.topic' \
                              --from-beginning
