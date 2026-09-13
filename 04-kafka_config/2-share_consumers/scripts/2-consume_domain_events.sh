docker compose exec shared_kafka \
  /bin/kafka-console-consumer --bootstrap-server shared_kafka:29092 \
                              --include 'codely\.mooc\..*' \
                              --formatter-property print.key=true \
                              --formatter-property print.topic=true \
                              --from-beginning
