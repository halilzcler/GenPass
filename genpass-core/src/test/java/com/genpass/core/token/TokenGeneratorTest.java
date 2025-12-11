package com.genpass.core.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenGeneratorTest {

    @Test
    void generatesUrlSafeBase64Token() {
        TokenGenerator generator = new TokenGenerator.Default();

        String token = generator.generateToken(32);

        assertNotNull(token);
        assertFalse(token.isBlank());

        // Base64 URL-safe regex: letters, digits, - and _
        assertTrue(token.matches("^[A-Za-z0-9_-]+$"));

        // Expected Base64 length = ceil(byteLength * 4/3)
        // Each 3 bytes equal to 4 bytes in Base64
        // Expected length should be 43 not 32
        int expectedLength = (int) Math.ceil(32 * 4.0 / 3.0);
        assertEquals(expectedLength, token.length());
    }

    @Test
    void generatesDifferentTokensEachCall() {
        TokenGenerator generator = new TokenGenerator.Default();

        String token1 = generator.generateToken(32);
        String token2 = generator.generateToken(32);

        assertNotEquals(token1, token2);
    }

    @Test
    void throwsForInvalidByteLength() {
        TokenGenerator generator = new TokenGenerator.Default();

        assertThrows(IllegalArgumentException.class, () ->
                generator.generateToken(0)
        );
        assertThrows(IllegalArgumentException.class, () ->
                generator.generateToken(-5)
        );
    }
}

