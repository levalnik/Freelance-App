CREATE TABLE IF NOT EXISTS user_outbox_events (
                              id UUID PRIMARY KEY,
                              aggregate_type VARCHAR(100) NOT NULL,
                              aggregate_id VARCHAR(100) NOT NULL,
                              event_type VARCHAR(100) NOT NULL,
                              payload TEXT NOT NULL,
                              created_at TIMESTAMP NOT NULL,
                              processed BOOLEAN DEFAULT FALSE
);