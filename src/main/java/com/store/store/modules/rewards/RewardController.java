package com.store.store.modules.rewards;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.store.common.response.ApiResponse;
import com.store.store.modules.rewards.dto.GetRewardsRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/rewards")
public class RewardController {
    private final IRewardService rewardService;

    public RewardController(IRewardService rewardService) {
        this.rewardService = rewardService;
    }

    @Operation(summary = "Get all rewards", description = "API to fetch all rewards", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getRewards(@ParameterObject GetRewardsRequest req) {
        return rewardService.getRewards(req);
    }
}
