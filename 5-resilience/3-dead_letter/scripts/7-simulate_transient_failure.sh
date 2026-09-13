#!/usr/bin/env bash
# Simulates a transient failure that the dead letter consumer can recover from.
# Run it with the consumers of the step 1 running.
#
# 1. Stops the database. The subscriber IncrementCoursesCounterOnCourseCreated can not write.
# 2. Publishes a valid course.created event. The subscriber fails 3 times and the event goes to dlq.codely.mooc.
# 3. Starts the database again.
#
# Then run the step 5. The dead letter consumer replays the event to the subscriber that failed,
# the subscriber consumes it and the dead letter is resolved as REPLAYED.

source "$(dirname "$0")/lib/publish.sh"

echo "Stopping the database"
docker compose stop shared_postgres

publish codely.mooc.course.created "$(uuid)" '"name":"Course published while the database was down","duration":"2 hours"'

echo "Waiting for the 3 deliveries to fail (backoff 4s + 6s)"
sleep 30

echo "Starting the database"
docker compose start shared_postgres
until docker compose exec shared_postgres pg_isready --username=root --host=127.0.0.1 &>/dev/null; do sleep 2; done

echo "Now run ./scripts/5-start_dead_letter_consumer.sh and watch the REPLAYED resolution"
