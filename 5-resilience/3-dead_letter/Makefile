all: build

start:
	@docker compose up -d

build:
	@./gradlew build --warning-mode all

lint:
	@docker compose exec test_server_java ./gradlew spotlessCheck

run-tests:
	@./gradlew test --warning-mode all

test:
	@docker compose exec test_server_java ./gradlew test --warning-mode all

run:
	@./gradlew :run

ping-postgres:
	@docker compose exec shared_postgres pg_isready --username=root --host=127.0.0.1

# Start the app
start-mooc_backend:
	@./gradlew :bootRun --args='mooc_backend server'

start-backoffice_frontend:
	@./gradlew :bootRun --args='backoffice_frontend server'

generate-kafka-terraform:
	@docker compose exec test_server_java ./gradlew :bootRun --args='mooc_backend domain-events:kafka:terraform:generate'

consume-kafka-domain-events-mooc:
	@docker compose exec test_server_java ./gradlew :bootRun --args='mooc_backend domain-events:kafka:consume'

consume-kafka-domain-events-backoffice:
	@docker compose exec test_server_java ./gradlew :bootRun --args='backoffice_backend domain-events:kafka:consume'

consume-kafka-dead-letters-mooc:
	@docker compose exec test_server_java ./gradlew :bootRun --args='mooc_backend domain-events:kafka:dead-letter:consume'

consume-kafka-dead-letters-backoffice:
	@docker compose exec test_server_java ./gradlew :bootRun --args='backoffice_backend domain-events:kafka:dead-letter:consume'
