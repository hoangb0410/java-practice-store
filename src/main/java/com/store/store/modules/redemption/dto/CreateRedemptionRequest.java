package com.store.store.modules.redemption.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRedemptionRequest {
    @Schema(description = "Reward ID", example = "1")
    @NotNull(message = "Reward ID is required")
    private Long rewardId;

    @Schema(description = "Quantity", example = "3")
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 3, message = "Quantity must be at most 3")
    private Integer quantity;
}
