package com.store.store.common.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Configuration
public class OTPConfig {

    @Value("${otp.secret-key}")
    private String secretKey;

    @Value("${otp.secret-salt}")
    private String salt;

    @Bean
    public TextEncryptor textEncryptor() {
        return Encryptors.text(secretKey, salt);
    }
}
