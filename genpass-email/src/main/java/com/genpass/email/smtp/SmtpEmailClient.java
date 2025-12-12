package com.genpass.email.smtp;

import com.genpass.core.email.EmailMessage;
import com.genpass.core.exception.GenPassCoreException;
import com.genpass.email.api.EmailClient;
import com.genpass.email.config.EmailConfig;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * SMTP implementation of {@link EmailClient} using Jakarta Mail.
 *
 * Supports TLS and optional username/password authentication.
 */
public final class SmtpEmailClient implements EmailClient {

    private static final Logger LOGGER = Logger.getLogger(SmtpEmailClient.class.getName());

    private final EmailConfig config;
    private final Session session;

    public SmtpEmailClient(EmailConfig config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.session = createSession(config);
    }

    private Session createSession(EmailConfig cfg) {
        Properties props = new Properties();

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", cfg.host());
        props.put("mail.smtp.port", Integer.toString(cfg.port()));

        boolean hasAuth = cfg.username() != null && !cfg.username().isBlank();
        props.put("mail.smtp.auth", Boolean.toString(hasAuth));
        props.put("mail.smtp.starttls.enable", Boolean.toString(cfg.useTls()));

        int timeout = cfg.timeoutMillis();
        if (timeout > 0) {
            String t = Integer.toString(timeout);
            props.put("mail.smtp.connectiontimeout", t);
            props.put("mail.smtp.timeout", t);
            props.put("mail.smtp.writetimeout", t);
        }

        jakarta.mail.Authenticator authenticator = null;
        if (hasAuth) {
            authenticator = new jakarta.mail.Authenticator() {
                @Override
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication(cfg.username(), cfg.password());
                }
            };
        }

        LOGGER.info(() -> "Initializing SMTP Session host=%s port=%d tls=%s auth=%s"
                .formatted(cfg.host(), cfg.port(), cfg.useTls(), hasAuth));

        return Session.getInstance(props, authenticator);
    }

    @Override
    public void send(EmailMessage message) {
        Objects.requireNonNull(message, "message must not be null");

        List<String> toList = message.getTo();

        if (toList == null || toList.isEmpty()) {
            throw new GenPassCoreException("Email 'to' list must not be null or empty", null);
        }

        String subject = safeDefault(message.getSubject(), "(no subject)");
        String textBody = safeDefault(message.getTextBody(), "");
        String htmlBody = message.getHtmlBody(); // may be null

        try {
            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.setFrom(new InternetAddress(config.fromAddress(), false));

            InternetAddress[] recipients = toList.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(address -> {
                        try {
                            return new InternetAddress(address, false);
                        } catch (Exception e) {
                            throw new GenPassCoreException("Invalid email address: " + address, e);
                        }
                    })
                    .toArray(InternetAddress[]::new);

            if (recipients.length == 0) {
                throw new GenPassCoreException("No valid recipient addresses provided", null);
            }

            mimeMessage.setRecipients(Message.RecipientType.TO, recipients);
            mimeMessage.setSubject(subject, StandardCharsets.UTF_8.name());

            if (htmlBody != null && !htmlBody.isBlank()) {
                mimeMessage.setContent(htmlBody, "text/html; charset=UTF-8");
            } else {
                mimeMessage.setText(textBody, StandardCharsets.UTF_8.name());
            }

            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            throw new GenPassCoreException("Failed to send SMTP email", e);
        }
    }

    private static String safeDefault(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
