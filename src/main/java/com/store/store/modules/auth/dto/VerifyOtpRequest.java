package com.store.store.modules.auth.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String otp;
    private String hash;
}
