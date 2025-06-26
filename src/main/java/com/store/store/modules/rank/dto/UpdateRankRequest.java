package com.store.store.modules.rank.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateRankRequest {
    private String name;

    @Min(value = 0, message = "pointsThreshold must be a non-negative number")
    private Integer pointsThreshold;

    @Min(value = 50000, message = "amount must be greater than 50000")
    private Integer amount;

    @Min(value = 0, message = "fixedPoint must be a non-negative number")
    private Integer fixedPoint;

    @Min(value = 0, message = "percentage must be a non-negative number")
    @Max(value = 100, message = "maximum percentage is 100")
    private Float percentage;

    @Min(value = 0, message = "maxPercentagePoints must be a non-negative number")
    private Integer maxPercentagePoints;
}
