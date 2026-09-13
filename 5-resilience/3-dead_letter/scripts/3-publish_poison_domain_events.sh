#!/usr/bin/env bash

source "$(dirname "$0")/lib/publish.sh"

publish_raw codely.mooc.course.created "$(uuid)" '{"this is not":"a domain event"'

publish codely.mooc.course.created "not-a-valid-uuid" '"name":"Poison course","duration":"1 hour"'
