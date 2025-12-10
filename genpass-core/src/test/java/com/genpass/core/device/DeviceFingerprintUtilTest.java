package com.genpass.core.device;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceFingerprintUtilTest {

    @Test
    void fingerprintShouldBeHexAndNonEmpty() {
        String fp = DeviceFingerprintUtil.generateFingerprint(
                "Mozilla/5.0",
                "192.168.1.10",
                123456789L
        );

        assertNotNull(fp);
        assertFalse(fp.isEmpty());

        // SHA-256 → 32 bytes → 64 hex characters
        assertEquals(64, fp.length());
        assertTrue(fp.matches("[0-9a-f]+"));
    }

    @Test
    void nullValuesShouldNotFail() {
        String fp = DeviceFingerprintUtil.generateFingerprint(null, null, 1000L);

        assertNotNull(fp);
        assertEquals(64, fp.length());
    }

    @Test
    void convenienceMethodShouldWork() {
        String fp = DeviceFingerprintUtil.generateFingerprint("UA", "1.2.3.4");

        assertNotNull(fp);
        assertEquals(64, fp.length());
    }

    @Test
    void consecutiveCallsShouldUsuallyDifferBecauseOfSalt() {
        String fp1 = DeviceFingerprintUtil.generateFingerprint("UA", "1.2.3.4", 1000L);
        String fp2 = DeviceFingerprintUtil.generateFingerprint("UA", "1.2.3.4", 1000L);

        // Because random salt is included in the hash
        assertNotEquals(fp1, fp2);
    }
}
