docker exec -it codely-java_ddd_example-kafka \
  /bin/kafka-topics --create \
                    --topic codely.mooc.domain_events \
                    --bootstrap-server shared_kafka:29092 \
                    --partitions 1 \
                    --replication-factor 1
