#!/usr/bin/env bash

source "$(dirname "$0")/lib/publish.sh"

publish codely.mooc.course.created "$(uuid)" '"name":"Kafka share consumers","duration":"3 hours"'
publish codely.mooc.course.renamed "$(uuid)" '"name":"Kafka share consumers: dead letters"'
publish codely.mooc.new-courses-newsletter-email.sent "$(uuid)" '"student_id":"'"$(uuid)"'"'
