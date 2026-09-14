#!/usr/bin/env bash

source "$(dirname "$0")/lib/publish.sh"

publish codely.mooc.course.renamed "$(uuid)" '"name":"Kafka share consumers: renew the lock"'
