# Kafka Queues viewer

A small dashboard for Kafka Queues (share groups) and their dead letter topics.

It runs the Kafka CLI tools inside the `shared_kafka` container, so it needs no dependencies and no extra ports on the broker.

## Run

```bash
make start                      # brings up shared_kafka
make start-kafka-queues-viewer  # http://localhost:8050
```

## What it shows

- Every topic, with its total messages, partitions and retention.
- Every share group, with the topics it consumes: processed, pending and dead messages per topic, the connected consumers, and the dead letter configuration of the group.
- Every dead letter topic, with the records it contains and the `__dlq.errors.*` headers Kafka adds to them.

## Settings

| Variable          | Default          |
|-------------------|------------------|
| `PORT`            | `8050`           |
| `KAFKA_SERVICE`   | `shared_kafka`   |
| `KAFKA_BOOTSTRAP` | `localhost:9092` |
| `REFRESH_MS`      | `4000`           |
