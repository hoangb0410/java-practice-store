package com.store.store.modules.rank.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRankRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Points threshold is required")
    @Min(value = 0, message = "pointsThreshold must be a non-negative number")
    private Integer pointsThreshold;

    @NotNull(message = "Amount is required")
    @Min(value = 50000, message = "amount must be greater than 50000")
    private Integer amount;

    @NotNull(message = "Fixed point is required")
    @Min(value = 0, message = "fixedPoint must be a non-negative number")
    private Integer fixedPoint;

    @NotNull(message = "Percentage is required")
    @Min(value = 0, message = "percentage must be a non-negative number")
    @Max(value = 100, message = "maximum percentage is 100")
    private Float percentage;

    @NotNull(message = "Max percentage points is required")
    @Min(value = 0, message = "maxPercentagePoints must be a non-negative number")
    private Integer maxPercentagePoints;
}
