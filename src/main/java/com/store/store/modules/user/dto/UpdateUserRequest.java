package com.store.store.modules.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;
}
