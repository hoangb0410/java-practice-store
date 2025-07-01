package com.store.store.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[0-9]{8,15}$", message = "Phone number must contain only digits and be between 8 to 15 characters")
    private String phone;
}
