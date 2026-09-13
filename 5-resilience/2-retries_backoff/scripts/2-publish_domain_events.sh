publish() {
  topic=$1
  aggregate_id=$(uuidgen | tr '[:upper:]' '[:lower:]')
  event_id=$(uuidgen | tr '[:upper:]' '[:lower:]')
  occurred_on=$(date -u '+%Y-%m-%d %H:%M:%S')
  attributes=$2

  echo "Publishing <$topic> with id <$event_id>"

  printf '%s\t{"data":{"id":"%s","type":"%s","occurred_on":"%s","attributes":{"id":"%s",%s}},"meta":{}}\n' \
    "$aggregate_id" "$event_id" "$topic" "$occurred_on" "$aggregate_id" "$attributes" |
    docker compose exec -T shared_kafka \
      /bin/kafka-console-producer --bootstrap-server shared_kafka:29092 \
                                  --topic "$topic" \
                                  --reader-property parse.key=true \
                                  --reader-property key.separator=$'\t' 2>/dev/null
}

publish codely.mooc.course.created '"name":"Kafka share consumers","duration":"3 hours"'
publish codely.mooc.course.renamed '"name":"Kafka share consumers: same aggregate"'
publish codely.mooc.new-courses-newsletter-email.sent '"student_id":"'$(uuidgen | tr '[:upper:]' '[:lower:]')'"'
