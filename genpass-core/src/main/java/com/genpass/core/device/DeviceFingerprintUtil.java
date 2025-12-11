package com.genpass.core.device;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;

/**
 * Utility for generating device fingerprints.
 *
 * <p>This class provides deterministic (given the inputs) utility methods to
 * produce a fingerprint string from typical client attributes such as user-agent,
 * IP address and a timestamp. The fingerprint is produced by hashing the
 * concatenated input using SHA-256 and returning a hex string.</p>
 *
 * <p>Example usage:
 * <pre>
 * String fingerprint = DeviceFingerprintUtil.generateFingerprint(userAgent, ip, Instant.now().toEpochMilli());
 * </pre>
 * </p>
 *
 * <p>Note: including timestamp means fingerprints will vary with time. If you
 * want reproducible fingerprints for the same user, call the overload that accepts
 * a fixed timestamp (e.g. last login time).</p>
 */
public final class DeviceFingerprintUtil {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of();

    private DeviceFingerprintUtil() {
        // utility class
    }

    /**
     * Generate a fingerprint from user-agent, ip and timestamp.
     *
     * @param userAgent client user-agent string (may be null)
     * @param ip        client IP address (may be null)
     * @param timestamp epoch milliseconds (e.g. Instant.now().toEpochMilli())
     * @return hex-encoded SHA-256 hash representing the fingerprint
     * @throws IllegalStateException if SHA-256 is not available (very unlikely)
     */
    public static String generateFingerprint(String userAgent, String ip, long timestamp) {
        String ua = userAgent == null ? "" : userAgent;
        String address = ip == null ? "" : ip;
        String toHash = ua + "|" + address + "|" + timestamp;
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(toHash.getBytes(StandardCharsets.UTF_8));
            // add a small random salt to reduce accidental collisions across distributed logs
            byte[] salt = new byte[8];
            RANDOM.nextBytes(salt);
            md.update(salt);
            byte[] digest = md.digest();
            return HEX.formatHex(digest);
        } catch (Exception ex) {
            // In practice SHA-256 should be available. Wrap in a runtime exception for callers to handle.
            throw new IllegalStateException("Failed to compute fingerprint", ex);
        }
    }

    /**
     * Convenience method: generate a fingerprint using current time as a timestamp.
     *
     * @param userAgent client user-agent
     * @param ip        client ip address
     * @return fingerprint hex string
     */
    public static String generateFingerprint(String userAgent, String ip) {
        return generateFingerprint(userAgent, ip, Instant.now().toEpochMilli());
    }
}

