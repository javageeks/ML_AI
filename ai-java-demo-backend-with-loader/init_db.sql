-- init_db.sql
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS documents (
  id serial PRIMARY KEY,
  content text NOT NULL,
  embedding vector(1536)
);

-- Example insertion (use actual embeddings values here)
-- INSERT INTO documents (content, embedding) VALUES ('Doc about Java', '[0.01, 0.02, ...]');
