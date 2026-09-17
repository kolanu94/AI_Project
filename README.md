RAG API — Document Q&A with Semantic Search

A production-ready Retrieval-Augmented Generation (RAG) backend built with Java 21 and Spring Boot. Upload documents, then ask questions in plain English — the API finds the most relevant context using vector similarity search and returns a grounded answer from GPT-4o-mini.

"ChatGPT for your own documents" — reduces hallucinations by anchoring every answer in your actual data.

Architecture
User question
      │
      ▼
┌─────────────────────┐
│   POST /documents   │
│        /ask         │  Spring Boot REST API (Java 21)
└────────┬────────────┘
         │
         ▼
  OpenAI Embeddings
  text-embedding-3-small
  (1 536-dim float vector)
         │
         ▼
┌────────────────────────────────────┐
│  PostgreSQL 15 + pgvector          │
│  cosine similarity  (<=>)          │
│  top-k nearest neighbor search     │
└────────┬───────────────────────────┘
         │  top-k matching documents
         ▼
   Build context string
         │
         ▼
  OpenAI Chat Completion
  gpt-4o-mini  (context + question)
         │
         ▼
  Answer + source documents
  (id, title, similarity score)
Tech Stack
Layer	Technology
Language	Java 21
Framework	Spring Boot 3.4
Database	PostgreSQL 15 + pgvector
Migrations	Flyway 11
AI	OpenAI API (embeddings + chat)
Containerization	Docker Compose
Quick Start

Prerequisites: Docker, an OpenAI API key

bash
# 1. Clone
git clone https://github.com/kolanu94/AI_Project.git
cd AI_Project

# 2. Set your OpenAI key
export OPENAI_API_KEY=sk-...

# 3. Start everything (API + Postgres)
docker compose -f infra/docker-compose.yml up -d --build

# 4. Verify health
curl http://localhost:8080/actuator/health
# → {"status":"UP"}
API Reference
Ingest a document
bash
curl -X POST http://localhost:8080/documents \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Spring Boot Caching",
    "content": "Spring Boot supports caching via @EnableCaching and @Cacheable annotations..."
  }'
List all documents
bash
curl http://localhost:8080/documents
Get a document by ID
bash
curl http://localhost:8080/documents/1
Ask a question (RAG)
bash
curl -X POST http://localhost:8080/documents/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "How does Spring Boot handle caching?", "topK": 3}'

Response:

json
{
  "answer": "Spring Boot handles caching through the @EnableCaching annotation...",
  "sources": [
    { "id": 1, "title": "Spring Boot Caching", "score": 0.94 },
    { "id": 4, "title": "Cache Eviction Strategies", "score": 0.87 }
  ]
}

topK controls how many documents are retrieved (1–10, default 3). Higher values give broader context; lower values are faster and more focused.

Project Structure
AI_Project/
├── backend/
│   ├── src/main/java/com/kolanu94/ragapi/
│   │   ├── document/        # CRUD endpoints + pgvector store
│   │   │   ├── DocumentController.java
│   │   │   ├── DocumentVectorStore.java   ← cosine similarity search
│   │   │   └── DocumentRepository.java
│   │   ├── rag/             # RAG pipeline (embed → search → answer)
│   │   │   ├── RagController.java
│   │   │   └── RagService.java
│   │   └── openai/          # OpenAI client (embed + chat)
│   │       └── OpenAiClient.java
│   ├── src/main/resources/
│   │   └── db/migration/    # Flyway SQL migrations
│   │       └── V1__init.sql
│   ├── Dockerfile
│   └── pom.xml
└── infra/
    └── docker-compose.yml
Key Design Decisions

Why pgvector instead of a dedicated vector DB (Pinecone, Weaviate)? Keeping vectors in Postgres eliminates a network hop, avoids managing a second data store, and lets ACID transactions cover both document metadata and embeddings. For document counts in the thousands, pgvector's HNSW index easily handles production load.

Why cosine similarity? OpenAI embedding vectors are normalized, so cosine distance (<=>) and dot-product produce equivalent rankings — but cosine similarity (1 − distance) maps naturally to a [0, 1] confidence score that's easy to threshold and explain.

Why text-embedding-3-small? 1 536 dimensions at a fraction of the cost of text-embedding-3-large. Benchmarks show minimal quality difference for English document retrieval tasks, which keeps embedding cost near zero at small scale.

Why Flyway? Schema changes are version-controlled alongside the code. The pgvector extension and the embedding vector(1536) column are created deterministically on every fresh container, making local setup and CI reproducible.

Roadmap
 Streaming responses via SSE (/documents/ask/stream)
 PDF / plain-text ingestion with automatic chunking
 HNSW index on the embedding column for sub-millisecond search at scale
 JWT authentication
 RAGAS-based evaluation harness (faithfulness, answer relevance metrics)
 React frontend — document upload UI + chat interface
Local Development
bash
# Run Postgres only (for iterating on the API outside Docker)
docker compose -f infra/docker-compose.yml up -d db

# Run the API with Maven (requires Java 21)
cd backend
OPENAI_API_KEY=sk-... ./mvnw spring-boot:run

# View logs
docker compose -f infra/docker-compose.yml logs -f api
Environment Variables
Variable	Description	Required
OPENAI_API_KEY	OpenAI secret key	✅
OPENAI_CHAT_MODEL	Chat model ID (e.g. gpt-4o-mini)	optional (default: gpt-4o-mini)
OPENAI_EMBED_MODEL	Embedding model ID	optional (default: text-embedding-3-small)
SPRING_DATASOURCE_URL	JDBC URL for Postgres	set by Docker Compose
