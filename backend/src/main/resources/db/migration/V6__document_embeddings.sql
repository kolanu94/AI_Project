-- Ensure pgvector extension exists (safe if already installed)
CREATE EXTENSION IF NOT EXISTS vector;

-- 1536 dims matches text-embedding-3-small
ALTER TABLE documents
  ADD COLUMN IF NOT EXISTS embedding vector(1536);

-- Helpful for filtering/search, optional
CREATE INDEX IF NOT EXISTS idx_documents_embedding
  ON documents USING ivfflat (embedding vector_cosine_ops)
  WITH (lists = 100);