DROP INDEX IF EXISTS idx_verification_token;
ALTER TABLE verification_tokens DROP CONSTRAINT IF EXISTS verification_tokens_token_key;