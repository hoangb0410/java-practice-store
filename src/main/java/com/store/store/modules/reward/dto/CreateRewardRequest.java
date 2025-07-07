package com.store.store.modules.reward.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRewardRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "pointsRequired is required")
    @Min(value = 0, message = "pointsRequired must be a non-negative number")
    private Integer pointsRequired;

    @NotNull(message = "expirationDate is required")
    @FutureOrPresent(message = "expirationDate must not be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    @NotNull(message = "quantity is required")
    @Min(value = 0, message = "quantity must be a non-negative number")
    private Integer quantity;

    private String description;

    private String imageUrl;
}
