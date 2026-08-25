docker exec -it 4-configure_env-kafka-1 \
  /bin/kafka-configs --bootstrap-server kafka:29092 \
                     --group codely.test.share.group \
                     --alter \
                     --add-config share.auto.offset.reset=earliest
