package com.store.store.modules.auth.dto;

import com.store.store.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private User user;
    private String accessToken;
    private String refreshToken;
}
