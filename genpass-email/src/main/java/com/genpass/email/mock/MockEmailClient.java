package com.genpass.email.mock;

import com.genpass.core.email.EmailMessage;
import com.genpass.core.exception.GenPassCoreException;
import com.genpass.email.api.EmailClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Mock implementation of {@link EmailClient}.
 *
 * Stores sent emails in-memory for testing.
 */
public final class MockEmailClient implements EmailClient {

    private static final Logger LOGGER = Logger.getLogger(MockEmailClient.class.getName());

    private final List<EmailMessage> sentEmails =
            Collections.synchronizedList(new ArrayList<>());

    @Override
    public void send(EmailMessage message) {
        Objects.requireNonNull(message, "message must not be null");
        try {
            sentEmails.add(message);
            LOGGER.info("Mock email stored. Total stored emails: " + sentEmails.size());
        } catch (RuntimeException e) {
            throw new GenPassCoreException("Failed to store mock email", e);
        }
    }

    /**
     * Returns an immutable snapshot of sent emails.
     */
    public List<EmailMessage> getSentEmails() {
        synchronized (sentEmails) {
            return List.copyOf(sentEmails);
        }
    }

    /**
     * Clears all stored emails.
     */
    public void clear() {
        synchronized (sentEmails) {
            sentEmails.clear();
        }
    }
}

