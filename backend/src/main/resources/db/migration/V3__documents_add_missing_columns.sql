ALTER TABLE documents
  ADD COLUMN IF NOT EXISTS title VARCHAR(200);

ALTER TABLE documents
  ADD COLUMN IF NOT EXISTS content TEXT;

-- If old rows exist, backfill so NOT NULL can be applied safely
UPDATE documents
SET title = COALESCE(title, 'Untitled')
WHERE title IS NULL;

UPDATE documents
SET content = COALESCE(content, '')
WHERE content IS NULL;

ALTER TABLE documents
  ALTER COLUMN title SET NOT NULL;

ALTER TABLE documents
  ALTER COLUMN content SET NOT NULL;