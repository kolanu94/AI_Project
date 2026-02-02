ALTER TABLE documents
  ADD COLUMN IF NOT EXISTS embedding vector(1536);

-- Optional but recommended for performance later:
-- CREATE INDEX IF NOT EXISTS idx_documents_embedding
-- ON documents USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);