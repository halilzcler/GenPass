package com.genpass.email.config;

import java.util.Objects;

/**
 * Immutable configuration used by the SMTP email client.
 *
 * This is intentionally simple but enough for most use-cases.
 */
public record EmailConfig(
        String host,
        int port,
        String username,
        String password,
        boolean useTls,
        String fromAddress,
        int timeoutMillis
) {

    public EmailConfig {
        Objects.requireNonNull(host, "host must not be null");
        Objects.requireNonNull(fromAddress, "fromAddress must not be null");

        if (host.isBlank()) {
            throw new IllegalArgumentException("host must not be blank");
        }
        if (fromAddress.isBlank()) {
            throw new IllegalArgumentException("fromAddress must not be blank");
        }
        if (port <= 0 || port > 65_535) {
            throw new IllegalArgumentException("port must be between 1 and 65535");
        }
        if (timeoutMillis < 0) {
            throw new IllegalArgumentException("timeoutMillis must be >= 0");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for EmailConfig.
     */
    public static final class Builder {

        private String host;
        private int port = 587;
        private String username;
        private String password;
        private boolean useTls = true;
        private String fromAddress;
        private int timeoutMillis = 10_000;

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder useTls(boolean useTls) {
            this.useTls = useTls;
            return this;
        }

        public Builder fromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
            return this;
        }

        public Builder timeoutMillis(int timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
            return this;
        }

        public EmailConfig build() {
            return new EmailConfig(
                    host,
                    port,
                    username,
                    password,
                    useTls,
                    fromAddress,
                    timeoutMillis
            );
        }
    }
}

