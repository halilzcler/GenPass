package com.genpass.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenPassCoreExceptionTest {

    @Test
    void constructor_messageWorks() {
        GenPassCoreException ex = new GenPassCoreException("Error occurred");
        assertEquals("Error occurred", ex.getMessage());
    }

    @Test
    void constructor_messageAndCauseWork() {
        Exception cause = new RuntimeException("Cause");
        GenPassCoreException ex = new GenPassCoreException("Error", cause);

        assertEquals("Error", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
