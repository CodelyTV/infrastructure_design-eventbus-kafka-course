#!/usr/bin/env bash
# Publishes two events that can not be consumed. Both end in dlq.codely.mooc:
#
# 1. A malformed body. The consumer can not deserialize it and rejects it.
#    The broker moves it to the dead letter topic on the first delivery.
#    Cause header: "Offset rejected by client."
#
# 2. A course.created event with an aggregate id that is not a UUID.
#    The subscriber IncrementCoursesCounterOnCourseCreated fails and the consumer releases it.
#    After KAFKA_GROUP_SHARE_DELIVERY_COUNT_LIMIT deliveries (3), the broker moves it to the dead letter topic.
#    Cause header: "Offset delivery count exceeded the threshold."
#    Watch the consumer logs: the backoff grows on every retry.

source "$(dirname "$0")/lib/publish.sh"

publish_raw codely.mooc.course.created "$(uuid)" '{"this is not":"a domain event"'

publish codely.mooc.course.created "not-a-valid-uuid" '"name":"Poison course","duration":"1 hour"'
