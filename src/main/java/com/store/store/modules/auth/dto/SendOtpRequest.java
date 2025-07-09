package com.store.store.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SendOtpRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(user|store)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Type must be either 'user' or 'store'")
    private String type; // "user" or "store"
}
