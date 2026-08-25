docker exec -it 4-configure_env-kafka-1 \
  /bin/kafka-topics --create \
                    --topic codely.test.topic \
                    --bootstrap-server kafka:29092 \
                    --partitions 1 \
                    --replication-factor 1
