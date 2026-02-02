# AI_Project


Purpose: Build a RAG (Retrieval-Augmented Generation) app that can store documents and answer questions using those documents.

What it does:
	•	Stores documents (title + content) in a database
	•	Later it will convert documents into embeddings (vectors)
	•	When you ask a question, it will search the most relevant documents and use them as context to generate an answer

Why it’s helpful:
	•	Gives more accurate answers than a normal chatbot because it uses your own data
	•	Reduces hallucinations (random answers) by grounding responses in stored documents
	•	Useful for teams who want “ChatGPT for their internal docs”

What it can be used for:
	•	Personal knowledge base (notes, PDFs, learning material)
	•	Company FAQ / internal documentation assistant
	•	Support assistant that answers based on product docs
	•	Search engine for documents with “meaning-based search” (semantic search)

⸻


## ✅ What I’ve built so far (Phase 0)

This project is the foundation for a **RAG (Retrieval-Augmented Generation)** application.

So far I have:
- Built a **Java Spring Boot REST API**
- Connected it to a **PostgreSQL database**
- Containerized everything using **Docker Compose**, so the full system can run with one command
- Added basic endpoints to:
  - Create a document
  - List all documents
  - Get a document by ID
- Verified the API works using `curl`

---

## 🧱 Tech used (simple explanation)

### Docker (why it’s used)
Docker lets me run the app and database inside **containers**.
That means:
- No manual installation headaches
- Works the same on any machine
- Easy setup: one command starts everything

In this project:
- One container runs the **API**
- One container runs the **database**

### PostgreSQL (why it’s used)
PostgreSQL is the database where documents are stored.
It stores fields like:
- `title`
- `content`
- `created_at`

### pgvector (why it’s included)
pgvector is an extension for PostgreSQL that allows storing and searching **AI embeddings (vectors)**.

This will be used later for semantic search, like:
- searching "car" can also match "vehicle"

---

## ▶️ How to run locally

```bash
docker compose -f infra/docker-compose.yml up -d --build

## Check API health:
curl http://localhost:8080/actuator/health

##Create a document:
curl -X POST http://localhost:8080/documents \
  -H "Content-Type: application/json" \
  -d '{"title":"First doc","content":"Hello world","source":"manual"}'

##List documents:
curl http://localhost:8080/documents

##Get a document by ID:
curl http://localhost:8080/documents/1


