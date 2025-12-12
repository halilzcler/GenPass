package com.genpass.email.console;

import com.genpass.core.email.EmailMessage;
import com.genpass.core.exception.GenPassCoreException;
import com.genpass.email.api.EmailClient;

import java.time.Instant;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Console implementation for {@link EmailClient}.
 *
 * Simply prints the email to stdout. Useful in development.
 */
public final class ConsoleEmailClient implements EmailClient {

    private static final Logger LOGGER = Logger.getLogger(ConsoleEmailClient.class.getName());

    @Override
    public void send(EmailMessage message) {
        Objects.requireNonNull(message, "message must not be null");

        try {
            String output = """
                    ================== GenPass Console Email ==================
                    Time     : %s
                    To       : %s
                    Subject  : %s
                    -----------------------------------------------------------
                    TEXT BODY:
                    %s
                    -----------------------------------------------------------
                    HTML BODY:
                    %s
                    ===========================================================
                    """.formatted(
                    Instant.now(),
                    message.getTo(),
                    message.getSubject(),
                    safe(message.getTextBody()),
                    safe(message.getHtmlBody())
            );

            System.out.println(output);
            LOGGER.info("Email printed to console for recipient: " + message.getTo());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while printing email to console", e);
            throw new GenPassCoreException("Unexpected error while printing email to console", e);
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}

