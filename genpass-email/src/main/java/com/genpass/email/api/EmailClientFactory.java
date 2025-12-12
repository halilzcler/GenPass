package com.genpass.email.api;

import com.genpass.email.config.EmailConfig;
import com.genpass.email.console.ConsoleEmailClient;
import com.genpass.email.mock.MockEmailClient;
import com.genpass.email.smtp.SmtpEmailClient;

import java.util.Objects;

/**
 * Factory for creating {@link EmailClient} instances.
 *
 * This is the main entry point for consumers of this library.
 */
public final class EmailClientFactory {

    private EmailClientFactory() {
        // utility class
    }

    /**
     * Creates an SMTP email client.
     */
    public static EmailClient smtp(EmailConfig config) {
        Objects.requireNonNull(config, "config must not be null");
        return new SmtpEmailClient(config);
    }

    /**
     * Creates a console email client that prints emails to stdout.
     */
    public static EmailClient console() {
        return new ConsoleEmailClient();
    }

    /**
     * Creates a mock email client that stores emails in memory.
     */
    public static EmailClient mock() {
        return new MockEmailClient();
    }

    /**
     * Convenience method for creating a client from an enum.
     *
     * For SMTP, a non-null config is required.
     * For CONSOLE and MOCK, the config is ignored and may be null.
     */
    public static EmailClient fromType(EmailProviderType type, EmailConfig config) {
        Objects.requireNonNull(type, "type must not be null");

        return switch (type) {
            case SMTP -> smtp(Objects.requireNonNull(config, "config must not be null for SMTP"));
            case CONSOLE -> console();
            case MOCK -> mock();
        };
    }
}

