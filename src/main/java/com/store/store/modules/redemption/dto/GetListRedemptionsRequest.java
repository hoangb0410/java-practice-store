package com.store.store.modules.redemption.dto;

import org.springdoc.core.annotations.ParameterObject;

import com.store.store.common.pagination.PaginationRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ParameterObject
public class GetListRedemptionsRequest extends PaginationRequest {
    @Schema(description = "Filter by user id", example = "7", type = "number")
    @Min(value = 1, message = "User ID must be greater than 0")
    private Long userId;

    @Schema(description = "Filter by reward name", example = "Reward 1", type = "string")
    private String rewardName;
}
