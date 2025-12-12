package com.genpass.email.api;

import com.genpass.core.email.EmailMessage;
import com.genpass.core.exception.GenPassCoreException;

/**
 * Public email sending interface of the genpass-email module.
 *
 * Implementations:
 * <ul>
 *   <li>SMTP: {@link com.genpass.email.smtp.SmtpEmailClient}</li>
 *   <li>Console: {@link com.genpass.email.console.ConsoleEmailClient}</li>
 *   <li>Mock: {@link com.genpass.email.mock.MockEmailClient}</li>
 * </ul>
 */
public interface EmailClient {

    /**
     * Sends the given email message.
     *
     * @param message email payload (immutable)
     * @throws GenPassCoreException in case of failure
     */
    void send(EmailMessage message) throws GenPassCoreException;
}

