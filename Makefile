.PHONY: db db-down backend test test-db

db:
	docker compose up -d

db-down:
	docker compose down

backend:
	cd backend && mvn spring-boot:run

test-db:
	docker exec -i bookmanager-db psql -U postgres -c "CREATE DATABASE bookmanager_test;" || true

test:
	cd backend && mvn test
