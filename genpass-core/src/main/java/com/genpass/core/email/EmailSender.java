package com.genpass.core.email;

import com.genpass.core.exception.GenPassCoreException;

import java.util.Objects;

/**
 * Lightweight interface for sending email messages.
 *
 * <p>Implementations may be synchronous or asynchronous. This interface is intentionally tiny,
 * so it can be implemented in other modules using platform-specific transports (SMTP, AWS SES,
 * third-party SDKs, etc.). Implementations should convert transport-specific exceptions into
 * {@link GenPassCoreException} if they want to propagate an unchecked error.</p>
 */
public interface EmailSender {

    /**
     * Send the provided email message.
     *
     * @param message email message
     * @throws GenPassCoreException when sending fails
     */
    void send(EmailMessage message) throws GenPassCoreException;

    /**
     * A no-op implementation that writes the message to stdout (for tests/dev).
     */
    final class NoOp implements EmailSender {
        @Override
        public void send(EmailMessage message) {
            Objects.requireNonNull(message, "message");
            // Silently simulate sending by printing. Do not use in production.
            System.out.println("=== EmailSender.NoOp ===");
            System.out.println("From: " + message.getFrom());
            System.out.println("To  : " + message.getTo());
            System.out.println("Subj: " + message.getSubject());
            System.out.println("Text: " + message.getTextBody());
            System.out.println("HTML: " + message.getHtmlBody());
            System.out.println("========================");
        }

        @Override
        public String toString() {
            return "EmailSender.NoOp";
        }
    }
}

