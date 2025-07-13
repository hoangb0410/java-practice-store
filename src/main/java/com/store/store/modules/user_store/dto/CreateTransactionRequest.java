package com.store.store.modules.user_store.dto;

import com.store.store.constants.PointType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTransactionRequest {

    @Schema(description = "user amount", example = "2500000", type = "number")
    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be greater than or equal to 0")
    private Integer amount;

    @Schema(description = "point type fixed or percentage", example = "FIXED", type = "string", allowableValues = {
            "FIXED", "PERCENTAGE" })
    @NotNull(message = "Point type is required")
    private PointType pointType;
}
