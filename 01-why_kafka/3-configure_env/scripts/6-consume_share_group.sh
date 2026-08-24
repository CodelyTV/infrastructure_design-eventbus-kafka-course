docker exec -it 3-configure_env-kafka-1 \
  /bin/kafka-console-share-consumer --bootstrap-server kafka:29092 \
                                    --topic codely.test.topic \
                                    --group codely.test.share.group
