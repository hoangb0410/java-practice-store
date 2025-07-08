package com.store.store.common.email;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class OTPUtil {

    private final TextEncryptor encryptor;

    public OTPUtil(TextEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    public String generateOTP() {
        return String.valueOf(new Random().nextInt(9000) + 1000);
    }

    public String hashData(String data) {
        String encrypted = encryptor.encrypt(data);
        return Base64.getEncoder().encodeToString(encrypted.getBytes(StandardCharsets.UTF_8));
    }

    public String decryptData(String encrypted) {
        String decoded = new String(Base64.getDecoder().decode(encrypted), StandardCharsets.UTF_8);
        return encryptor.decrypt(decoded);
    }
}
