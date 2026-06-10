DELETE FROM verification_tokens;
ALTER TABLE verification_tokens RENAME COLUMN token TO code;
ALTER TABLE verification_tokens ALTER COLUMN code TYPE VARCHAR(6);
ALTER TABLE verification_tokens ADD COLUMN IF NOT EXISTS attempts INTEGER DEFAULT 0 NOT NULL;