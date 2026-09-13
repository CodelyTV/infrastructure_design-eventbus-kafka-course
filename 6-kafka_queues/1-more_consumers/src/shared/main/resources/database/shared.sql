CREATE TABLE IF NOT EXISTS failover_domain_events (
	id SERIAL PRIMARY KEY,
	event_id UUID NOT NULL,
	event_name VARCHAR(255) NOT NULL,
	body TEXT NOT NULL
);
