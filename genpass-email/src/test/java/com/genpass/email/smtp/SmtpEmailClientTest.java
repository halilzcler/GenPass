package com.genpass.email.smtp;

import com.genpass.core.email.EmailMessage;
import com.genpass.core.exception.GenPassCoreException;
import com.genpass.email.config.EmailConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SmtpEmailClientTest {

    private EmailConfig baseConfig() {
        return EmailConfig.builder()
                .host("localhost")
                .port(1)
                .fromAddress("no-reply@test.com")
                .timeoutMillis(500)
                .build();
    }

    @Test
    void shouldFailGracefullyWhenSmtpIsUnavailable() {
        SmtpEmailClient client = new SmtpEmailClient(baseConfig());

        EmailMessage message = new EmailMessage.Builder()
                .addTo("user@test.com")
                .subject("SMTP Down")
                .textBody("This should fail")
                .build();

        assertThrows(GenPassCoreException.class, () -> client.send(message));
    }

    @Test
    void shouldThrowWhenRecipientAddressIsInvalid() {
        SmtpEmailClient client = new SmtpEmailClient(baseConfig());

        EmailMessage message = new EmailMessage.Builder()
                .addTo("not-an-email")
                .subject("Invalid Address")
                .textBody("Body")
                .build();

        assertThrows(GenPassCoreException.class, () -> client.send(message));
    }
}
