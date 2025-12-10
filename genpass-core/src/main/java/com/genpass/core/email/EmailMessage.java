package com.genpass.core.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Immutable value object representing an email message.
 *
 * <p>This class intentionally keeps the model simple: recipients, subject,
 * plain text body, and optional HTML body. It is designed to be transport-agnostic
 * and used by email-sending implementations in other modules (which may adapt it
 * to platform-specific APIs).</p>
 */
public final class EmailMessage {

    private final String from;
    private final List<String> to;
    private final String subject;
    private final String textBody;
    private final String htmlBody;

    private EmailMessage(Builder b) {
        this.from = b.from;
        this.to = List.copyOf(b.to);
        this.subject = b.subject;
        this.textBody = b.textBody;
        this.htmlBody = b.htmlBody;
    }

    public String getFrom() {
        return from;
    }

    public List<String> getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getTextBody() {
        return textBody;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    @Override
    public String toString() {
        return "EmailMessage{from='" + from + "', to=" + to + ", subject='" + subject + "'}";
    }

    /**
     * Builder for EmailMessage.
     */
    public static class Builder {
        private String from;
        private final List<String> to = new ArrayList<>();
        private String subject;
        private String textBody;
        private String htmlBody;

        public Builder() { }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder addTo(String recipient) {
            Objects.requireNonNull(recipient, "recipient");
            this.to.add(recipient);
            return this;
        }

        public Builder to(List<String> recipients) {
            Objects.requireNonNull(recipients, "recipients");
            this.to.addAll(recipients);
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder textBody(String textBody) {
            this.textBody = textBody;
            return this;
        }

        public Builder htmlBody(String htmlBody) {
            this.htmlBody = htmlBody;
            return this;
        }

        public EmailMessage build() {
            if (to.isEmpty()) {
                throw new IllegalStateException("At least one recipient (to) is required");
            }
            return new EmailMessage(this);
        }
    }
}

