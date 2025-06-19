package com.store.store.modules.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
}