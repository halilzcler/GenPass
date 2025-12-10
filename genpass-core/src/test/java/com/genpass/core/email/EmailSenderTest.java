package com.genpass.core.email;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailSenderTest {

    @Test
    void noOpSenderDoesNotThrow() {
        EmailSender sender = new EmailSender.NoOp();

        EmailMessage message = new EmailMessage.Builder()
                .from("no-reply@example.com")
                .addTo("user@example.com")
                .subject("Test email")
                .textBody("Hello user")
                .htmlBody("<p>Hello user</p>")
                .build();

        assertDoesNotThrow(() -> sender.send(message));
    }

    @Test
    void noOpSenderRequiresNonNullMessage() {
        EmailSender sender = new EmailSender.NoOp();

        assertThrows(NullPointerException.class, () -> sender.send(null));
    }
}
