package com.genpass.core.token;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MagicLinkTokenServiceTest {

    private MagicLinkTokenService service;

    @BeforeEach
    void setUp() {
        byte[] key = "super-secret-key-123456789".getBytes();
        service = new MagicLinkTokenService(key, new TokenGenerator.Default(), 16);
    }

    @Test
    void shouldCreateAndVerifyTokenSuccessfully() {
        String token = service.createToken("test-user", Duration.ofMinutes(10));

        assertNotNull(token);
        assertTrue(token.contains("."), "Token must contain a dot separating payload and signature");

        Optional<String> subject = service.verifyToken(token);

        assertTrue(subject.isPresent());
        assertEquals("test-user", subject.get());
    }

    @Test
    void shouldReturnEmptyOptionalWhenTokenIsModified() {
        String token = service.createToken("user1", Duration.ofMinutes(5));

        char lastChar = token.charAt(token.length() - 1);
        char tamperedChar = lastChar == 'A' ? 'B' : 'A';

        String tampered = token.substring(0, token.length() - 1) + tamperedChar;

        Optional<String> result = service.verifyToken(tampered);

        assertTrue(result.isEmpty(), "Tampered token must not validate");
    }

    @Test
    void shouldReturnEmptyOptionalForExpiredToken() throws InterruptedException {
        MagicLinkTokenService shortLived =
                new MagicLinkTokenService("secret".getBytes(), new TokenGenerator.Default(), 16);

        String token = shortLived.createToken("expired-user", Duration.ofMillis(1));

        Thread.sleep(5);

        Optional<String> result = shortLived.verifyToken(token);

        assertTrue(result.isEmpty(), "Expired token must return empty Optional");
    }

    @Test
    void shouldNotAllowSubjectContainingColon() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createToken("bad:user", Duration.ofMinutes(1))
        );
    }

    @Test
    void shouldRejectZeroTTL() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createToken("abc", Duration.ZERO)
        );
    }

    @Test
    void shouldRejectNegativeTTL() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createToken("abc", Duration.ofSeconds(-5))
        );
    }

    @Test
    void shouldReturnEmptyForNullToken() {
        assertTrue(service.verifyToken(null).isEmpty());
    }

    @Test
    void shouldReturnEmptyForInvalidBase64Token() {
        assertTrue(service.verifyToken("invalid!!..base64").isEmpty());
    }
}
