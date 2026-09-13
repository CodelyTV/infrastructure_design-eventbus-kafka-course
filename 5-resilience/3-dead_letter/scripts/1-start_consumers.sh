docker compose exec test_server_java \
  ./gradlew :bootRun --args='mooc_backend domain-events:kafka:consume'
