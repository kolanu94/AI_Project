-- Create table if it doesn't exist
CREATE TABLE IF NOT EXISTS documents (
  id         BIGSERIAL PRIMARY KEY,
  title      VARCHAR(200) NOT NULL,
  content    TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- If table already existed (without created_at), add the column
ALTER TABLE documents
  ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ;

-- Backfill any old rows (if column was added and existing rows exist)
UPDATE documents
SET created_at = NOW()
WHERE created_at IS NULL;

-- Enforce desired constraints/default
ALTER TABLE documents
  ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE documents
  ALTER COLUMN created_at SET NOT NULL;

-- Index (now safe)
CREATE INDEX IF NOT EXISTS idx_documents_created_at
  ON documents (created_at DESC);