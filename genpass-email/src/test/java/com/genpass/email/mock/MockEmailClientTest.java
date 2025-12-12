package com.genpass.email.mock;

import com.genpass.core.email.EmailMessage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockEmailClientTest {

    @Test
    void shouldStoreSentEmails() {
        MockEmailClient client = new MockEmailClient();

        EmailMessage message = new EmailMessage.Builder()
                .addTo("test@example.com")
                .subject("Test")
                .textBody("Hello")
                .build();

        client.send(message);

        List<EmailMessage> sent = client.getSentEmails();

        assertEquals(1, sent.size());
        assertSame(message, sent.getFirst());
    }

    @Test
    void clearShouldRemoveAllEmails() {
        MockEmailClient client = new MockEmailClient();

        client.send(
                new EmailMessage.Builder()
                        .addTo("a@test.com")
                        .textBody("A")
                        .build()
        );

        client.clear();

        assertTrue(client.getSentEmails().isEmpty());
    }

    @Test
    void sendNullMessageShouldThrow() {
        MockEmailClient client = new MockEmailClient();

        assertThrows(NullPointerException.class, () -> client.send(null));
    }
}
