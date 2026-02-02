-- Enable pgvector
CREATE EXTENSION IF NOT EXISTS vector;

-- Store chunks + embeddings
CREATE TABLE IF NOT EXISTS document_chunks (
  id           BIGSERIAL PRIMARY KEY,
  document_id  BIGINT NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
  chunk_index  INT NOT NULL,
  content      TEXT NOT NULL,
  embedding    vector(1536) NOT NULL,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE(document_id, chunk_index)
);

-- Fast vector search (cosine)
-- If your pgvector supports HNSW:
CREATE INDEX IF NOT EXISTS idx_chunks_embedding_hnsw
ON document_chunks USING hnsw (embedding vector_cosine_ops);

-- Helpful filter index
CREATE INDEX IF NOT EXISTS idx_chunks_document_id
ON document_chunks(document_id);