package com.genpass.core.token;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Lightweight interface for generating secure tokens.
 *
 * <p>Implementations should use a cryptographically secure random source
 * (e.g. {@link SecureRandom}). Tokens returned by {@link #generateToken(int)}
 * MUST be safe to use in URLs (no newline characters); the default helper
 * implementation encodes bytes in URL-safe Base64 without padding.</p>
 */
public interface TokenGenerator {

    /**
     * Generate a token encoded as a String with approximately {@code byteLength} bytes
     * of entropy.
     *
     * @param byteLength number of random bytes to use for token entropy (e.g. 16, 32)
     * @return token string, URL-safe
     * @throws IllegalArgumentException if byteLength &lt;= 0
     */
    String generateToken(int byteLength);

    /**
     * A simple, secure default implementation that uses {@link SecureRandom}
     * and URL-safe Base64 (without padding).
     *
     * <p>It is provided as a static nested class here, so modules can use it
     * without requiring an additional file.</p>
     */
    final class Default implements TokenGenerator {
        private static final SecureRandom RANDOM = new SecureRandom();
        private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

        /**
         * Create a new Default token generator. No configuration required.
         */
        public Default() {
            // no-op
        }

        @Override
        public String generateToken(int byteLength) {
            if (byteLength <= 0) {
                throw new IllegalArgumentException("byteLength must be > 0");
            }
            byte[] buf = new byte[byteLength];
            RANDOM.nextBytes(buf);
            return URL_ENCODER.encodeToString(buf);
        }

        @Override
        public String toString() {
            return "TokenGenerator.Default";
        }
    }
}

