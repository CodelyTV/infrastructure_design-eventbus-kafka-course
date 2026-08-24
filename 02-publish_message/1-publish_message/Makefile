all: build

start:
	@docker compose up -d

build:
	@./gradlew build --warning-mode all

lint:
	@docker exec codely-java_ddd_example-test_server ./gradlew spotlessCheck

run-tests:
	@./gradlew test --warning-mode all

test:
	@docker exec codely-java_ddd_example-test_server ./gradlew test --warning-mode all

run:
	@./gradlew :run

ping-postgres:
	@docker exec codely-java_ddd_example-postgres pg_isready --username=root --host=127.0.0.1

# Start the app
start-mooc_backend:
	@./gradlew bootRun --args='mooc_backend server'

start-backoffice_frontend:
	@./gradlew bootRun --args='backoffice_frontend server'
