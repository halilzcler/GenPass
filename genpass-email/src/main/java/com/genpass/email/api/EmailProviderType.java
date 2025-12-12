package com.genpass.email.api;

/**
 * Identifies the type of email provider.
 *
 * This is useful if you want to choose the implementation
 * from configuration (e.g. application.properties / env).
 */
public enum EmailProviderType {
    SMTP,
    CONSOLE,
    MOCK
}

