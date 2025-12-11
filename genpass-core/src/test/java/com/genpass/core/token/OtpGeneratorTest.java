package com.genpass.core.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OtpGeneratorTest {

    @Test
    void generateOtp_returnsSixDigits() {
        OtpGenerator generator = new OtpGenerator.Default();
        String code = generator.generateOtp();

        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"));
    }

    @Test
    void generateOtp_isRandom() {
        OtpGenerator generator = new OtpGenerator.Default();
        String first = generator.generateOtp();
        String second = generator.generateOtp();

        assertNotEquals(first, second);
    }
}
