package com.store.store.modules.auth.dto;

import lombok.Data;

@Data
public class SendOtpRequest {
    private String email;
    private String hash;
}
