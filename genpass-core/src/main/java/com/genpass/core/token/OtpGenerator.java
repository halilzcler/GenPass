package com.genpass.core.token;

import java.security.SecureRandom;

/**
 * Interface for generating one-time passwords (OTPs).
 *
 * <p>Implementations should return numeric-only OTP strings suitable for sending
 * over SMS or email. The default generator included here returns 6-digit numeric
 * OTPs with leading zeros when necessary.</p>
 */
public interface OtpGenerator {

    /**
     * Generate a one-time password (OTP) as a string.
     *
     * @return OTP string (numeric). Implementations in this module produce 6-digit codes.
     */
    String generateOtp();

    /**
     * Default implementation producing numeric 6-digit OTPs using {@link SecureRandom}.
     */
    final class Default implements OtpGenerator {
        private static final SecureRandom RANDOM = new SecureRandom();
        private static final int DIGITS = 6;
        private static final int UPPER_BOUND = (int) Math.pow(10, DIGITS); // 1_000_000

        /**
         * Construct a default OTP generator.
         */
        public Default() {
            // no-op
        }

        @Override
        public String generateOtp() {
            int value = RANDOM.nextInt(UPPER_BOUND); // 0 .. 999_999
            return String.format("%0" + DIGITS + "d", value);
        }

        @Override
        public String toString() {
            return "OtpGenerator.Default(6-digit)";
        }
    }
}

