package com.genpass.core.token;

import com.genpass.core.exception.GenPassCoreException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

/**
 * Simple stateless magic-link token service.
 *
 * <p>This service creates compact, URL-safe magic link tokens that contain a payload
 * and an HMAC-SHA256 signature. The payload is a UTF-8 string in the format:
 * {@code subject:expiryEpochMilli:nonce}, encoded using URL-safe Base64 (no padding),
 * then appended with '.' and the signature (URL-safe Base64 no padding).</p>
 *
 * <p>Construction requires a secret key (byte array) used for HMAC-SHA256 signing.
 * The service exposes {@link #createToken(String, Duration)} and
 * {@link #verifyToken(String)} which returns the subject if verification succeeds
 * and the token has not expired.</p>
 *
 * <p>Notes:
 * - This class is intentionally tiny and stateless: it does not store issued tokens.
 * - If you need revocation or single-use tokens, pair this with persistent storage
 *   in another module.</p>
 */
public final class MagicLinkTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    private final SecretKeySpec hmacKey;
    private final TokenGenerator tokenGenerator;
    private final int nonceByteLength;

    /**
     * Create a new MagicLinkTokenService.
     *
     * @param hmacSecret     secret bytes used for HMAC-SHA256 signing (must not be null/empty)
     * @param tokenGenerator token generator used to produce the nonce (if null a sensible default is used)
     * @param nonceByteLength number of random bytes to include as nonce (recommended 16 or 32)
     */
    public MagicLinkTokenService(byte[] hmacSecret, TokenGenerator tokenGenerator, int nonceByteLength) {
        if (hmacSecret == null || hmacSecret.length == 0) {
            throw new IllegalArgumentException("hmacSecret must not be null or empty");
        }
        if (nonceByteLength <= 0) {
            throw new IllegalArgumentException("nonceByteLength must be > 0");
        }
        this.hmacKey = new SecretKeySpec(hmacSecret, HMAC_ALGORITHM);
        this.tokenGenerator = tokenGenerator != null ? tokenGenerator : new TokenGenerator.Default();
        this.nonceByteLength = nonceByteLength;
    }

    /**
     * Convenience constructor using a default TokenGenerator and a 32-byte nonce length.
     *
     * @param hmacSecret secret bytes for signing
     */
    public MagicLinkTokenService(byte[] hmacSecret) {
        this(hmacSecret, new TokenGenerator.Default(), 32);
    }

    /**
     * Create a token for a subject with a time-to-live.
     *
     * @param subject the subject (e.g., user id or email) — must not contain ':' character
     * @param ttl     time-to-live (duration). Must be positive.
     * @return a compact URL-safe token string
     */
    public String createToken(String subject, Duration ttl) {
        Objects.requireNonNull(subject, "subject");
        Objects.requireNonNull(ttl, "ttl");
        if (subject.indexOf(':') >= 0) {
            throw new IllegalArgumentException("subject must not contain ':'");
        }
        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("ttl must be positive");
        }

        long expiry = Instant.now().plus(ttl).toEpochMilli();
        // nonce: produce a URL-safe Base64 string with the desired entropy
        String nonce = tokenGenerator.generateToken(nonceByteLength);

        String payloadPlain = subject + ":" + expiry + ":" + nonce;
        byte[] payloadBytes = payloadPlain.getBytes(StandardCharsets.UTF_8);
        String payloadEncoded = URL_ENCODER.encodeToString(payloadBytes);

        byte[] signature = hmac(payloadBytes);
        String signatureEncoded = URL_ENCODER.encodeToString(signature);

        return payloadEncoded + "." + signatureEncoded;
    }

    /**
     * Verify a token created by {@link #createToken(String, Duration)}.
     *
     * @param token token string
     * @return Optional containing subject if verification and expiry checks pass; empty Optional otherwise.
     * @throws GenPassCoreException when a token is malformed or HMAC computation fails unexpectedly
     */
    public Optional<String> verifyToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        int dot = token.indexOf('.');
        if (dot < 0) {
            return Optional.empty();
        }
        String payloadEncoded = token.substring(0, dot);
        String signatureEncoded = token.substring(dot + 1);

        byte[] payloadBytes;
        byte[] signatureBytes;
        try {
            payloadBytes = URL_DECODER.decode(payloadEncoded);
            signatureBytes = URL_DECODER.decode(signatureEncoded);
        } catch (IllegalArgumentException e) {
            // invalid Base64 input
            return Optional.empty();
        }

        // Verify signature using constant-time comparison
        byte[] expectedSig = hmac(payloadBytes);
        if (!MessageDigest.isEqual(expectedSig, signatureBytes)) {
            return Optional.empty();
        }

        // Parse payload: subject:expiry:nonce
        String payload = new String(payloadBytes, StandardCharsets.UTF_8);
        String[] parts = payload.split(":", 3);
        if (parts.length < 2) { // must have at least subject:expiry
            return Optional.empty();
        }
        String subject = parts[0];
        String expiryStr = parts[1];
        long expiryMillis;
        try {
            expiryMillis = Long.parseLong(expiryStr);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        long now = Instant.now().toEpochMilli();
        if (now > expiryMillis) {
            return Optional.empty(); // expired
        }
        return Optional.of(subject);
    }

    private byte[] hmac(byte[] payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(hmacKey);
            return mac.doFinal(payload);
        } catch (Exception ex) {
            throw new GenPassCoreException("Failed to compute HMAC-SHA256", ex);
        }
    }

    @Override
    public String toString() {
        return "MagicLinkTokenService{nonceBytes=" + nonceByteLength + "}";
    }
}

