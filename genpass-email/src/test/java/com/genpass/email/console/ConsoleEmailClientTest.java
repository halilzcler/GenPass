package com.genpass.email.console;

import com.genpass.core.email.EmailMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ConsoleEmailClientTest {

    @Test
    void shouldPrintEmailWithoutThrowing() {
        ConsoleEmailClient client = new ConsoleEmailClient();

        EmailMessage message = new EmailMessage.Builder()
                .addTo("console@test.com")
                .subject("Console Test")
                .textBody("Hello console")
                .build();

        assertDoesNotThrow(() -> client.send(message));
    }

    @Test
    void shouldHandleHtmlBody() {
        ConsoleEmailClient client = new ConsoleEmailClient();

        EmailMessage message = new EmailMessage.Builder()
                .addTo("html@test.com")
                .subject("HTML Test")
                .textBody("Plain text")
                .htmlBody("<p>Hello <b>HTML</b></p>")
                .build();

        assertDoesNotThrow(() -> client.send(message));
    }
}
