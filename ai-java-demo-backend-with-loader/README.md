# AI Java Demo (Backend)

This Spring Boot demo includes:
- Spring AI (OpenAI) for chat/summarization
- LangChain4j based RAG service using PostgreSQL + pgvector
- Spring AI MCP tool example via @McpFunctionCallback

## Run Postgres with pgvector (recommended)
A docker-compose is provided to run Postgres with pgvector extension.

1. Start Postgres:
   ```
   docker-compose up -d
   ```

2. Initialize the `documents` table:
   ```
   psql -h localhost -U postgres -d ai_demo -f init_db.sql
   ```

3. Insert sample documents with actual embeddings (compute embeddings via OpenAI or Spring AI).
   Example SQL:
   ```
   INSERT INTO documents (content, embedding) VALUES ('Doc about Java', '[0.01, 0.02, ...]');
   ```

4. Add your OpenAI API key to `src/main/resources/application.yml` (ai.openai.api-key)

5. Run the app:
   ```
   mvn spring-boot:run
   ```

## Notes
- The RAG service contains placeholder code for embeddings; replace `getQueryEmbedding` with a real embeddings call (via Spring AI or OpenAI SDK) before using the vector search.

## Automatic Document Loader
On startup, the app loads `.txt` files from `src/main/resources/docs/`,
generates embeddings with OpenAI, and inserts them into PostgreSQL+pgvector.
