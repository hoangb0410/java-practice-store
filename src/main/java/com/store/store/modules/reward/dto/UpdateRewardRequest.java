package com.store.store.modules.reward.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateRewardRequest {
    private String name;

    @Min(value = 0, message = "pointsRequired must be a non-negative number")
    private Integer pointsRequired;

    @FutureOrPresent(message = "expirationDate must not be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expirationDate;

    @Min(value = 0, message = "quantity must be a non-negative number")
    private Integer quantity;

    private String description;

    private String imageUrl;
}
