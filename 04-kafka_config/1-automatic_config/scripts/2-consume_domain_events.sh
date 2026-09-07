docker exec -it codely-java_ddd_example-kafka \
  /bin/kafka-console-consumer --bootstrap-server shared_kafka:29092 \
                              --topic codely.mooc.domain_events \
                              --formatter-property print.key=true \
                              --from-beginning
