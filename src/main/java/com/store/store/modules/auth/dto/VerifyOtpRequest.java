package com.store.store.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @NotBlank(message = "OTP is required")
    private String otp;

    @NotBlank(message = "Hash is required")
    private String hash;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(user|store)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Type must be either 'user' or 'store'")
    private String type;
}
