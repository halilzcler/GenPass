package com.genpass.core.exception;

/**
 * Base runtime exception for GenPass core module.
 *
 * <p>This exception is intentionally a RuntimeException to allow usage across
 * modules without forcing checked exception handling. Wrap lower-level errors
 * (IO, crypto, etc.) in this exception when surfacing them from the genpass-core
 * library.</p>
 */
public class GenPassCoreException extends RuntimeException {
    /**
     * Create a new GenPassCoreException with a message.
     *
     * @param message human-readable message
     */
    public GenPassCoreException(String message) {
        super(message);
    }

    /**
     * Create a new GenPassCoreException with a message and underlying cause.
     *
     * @param message human-readable message
     * @param cause   underlying cause
     */
    public GenPassCoreException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new GenPassCoreException with an underlying cause.
     *
     * @param cause underlying cause
     */
    public GenPassCoreException(Throwable cause) {
        super(cause);
    }
}

