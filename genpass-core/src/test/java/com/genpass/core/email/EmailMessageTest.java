package com.genpass.core.email;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailMessageTest {

    @Test
    void builderCreatesValidEmailMessage() {
        EmailMessage msg = new EmailMessage.Builder()
                .from("no-reply@example.com")
                .addTo("user@example.com")
                .subject("Welcome!")
                .textBody("Hello user")
                .htmlBody("<p>Hello user</p>")
                .build();

        assertEquals("no-reply@example.com", msg.getFrom());
        assertEquals(List.of("user@example.com"), msg.getTo());
        assertEquals("Welcome!", msg.getSubject());
        assertEquals("Hello user", msg.getTextBody());
        assertEquals("<p>Hello user</p>", msg.getHtmlBody());
    }

    @Test
    void builderRequiresAtLeastOneRecipient() {
        EmailMessage.Builder builder = new EmailMessage.Builder()
                .from("no-reply@example.com")
                .subject("Test");

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void addMultipleRecipientsWorks() {
        EmailMessage msg = new EmailMessage.Builder()
                .from("sender@example.com")
                .to(List.of("a@example.com", "b@example.com"))
                .build();

        assertEquals(List.of("a@example.com", "b@example.com"), msg.getTo());
    }
}
