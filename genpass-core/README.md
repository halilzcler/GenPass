# GenPass Core

`genpass-core` contains the foundational utilities used across the GenPass platform.  
It is a lightweight, dependency-free module built with pure Java, intended to be embedded by other modules such as `genpass-email`, `genpass-spring`, and `genpass-demo-api`.

The module provides:

- secure token and OTP generation  
- a compact magic-link token service  
- a simple email message abstraction  
- a device fingerprint utility  
- shared exceptions and helper interfaces  

The goal is to keep core logic isolated, portable, and free of framework dependencies.

---

## Token Utilities

### TokenGenerator  
Generates URL-safe Base64 tokens using `SecureRandom`.  
Used across modules where random identifiers or nonces are required.

### OtpGenerator  
Produces numeric OTP codes (6-digit by default).  
Stateless and suitable for SMS/email OTP flows in higher-level modules.

### MagicLinkTokenService  
Provides creation and verification of compact magic-link tokens.

Token format:

```
base64url(subject:expiry:nonce) . base64url(hmac)
```

Design notes:

- Signed using HMAC-SHA256 for a small, predictable, dependency-free implementation.  
- Stateless by design; all required data is embedded in the token.  
- Key rotation is handled externally—modules may maintain multiple active keys.  
- JWT was intentionally avoided to keep payloads minimal and remove structural overhead.

---

## Device Fingerprinting

### DeviceFingerprintUtil  
Generates a SHA-256 hash from basic client attributes:

```
user-agent | ip | timestamp
```

Includes a small random salt to reduce accidental collisions.  
This utility is optional and can be extended in higher modules based on project needs.

---

## Email Abstractions

### EmailMessage  
A simple immutable value object representing an email with:

- sender  
- recipients  
- subject  
- text body  
- optional HTML body  

Framework-agnostic so that implementations can be built on SMTP, SES, or other providers.

### EmailSender  
Small interface for sending email messages.  
A no-op implementation is included for testing and development.

---

## Exception

### GenPassCoreException  
Shared unchecked exception type for core-level failures such as failed HMAC operations during verification.

---

## Scope of This Module

`genpass-core` intentionally remains small.  
It provides only fundamental building blocks.  
Features such as HTTP endpoints, persistence, or framework wiring belong to other modules (e.g., Spring-based modules).

---

## Build & Test

```
mvn clean verify
```

Unit tests cover token creation/verification, message construction, OTP generation, and device fingerprint hashing.
