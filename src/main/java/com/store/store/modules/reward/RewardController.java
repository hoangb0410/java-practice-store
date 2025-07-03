package com.store.store.modules.reward;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.store.common.response.ApiResponse;
import com.store.store.modules.reward.dto.CreateRewardRequest;
import com.store.store.modules.reward.dto.GetRewardsRequest;
import com.store.store.modules.reward.dto.UpdateRewardRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

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

    @Operation(summary = "Create reward", description = "API to create reward", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createReward(@Valid @RequestBody CreateRewardRequest req) {
        return rewardService.createReward(req);
    }

    @Operation(summary = "Get reward by ID", description = "API to get reward by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> findById(@PathVariable Long id) {
        return rewardService.findById(id);
    }

    @Operation(summary = "Update reward", description = "API to update reward", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateReward(@PathVariable Long id,
            @Valid @RequestBody UpdateRewardRequest req) {
        return rewardService.updateReward(id, req);
    }

    @Operation(summary = "Delete reward", description = "API to delete reward", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteReward(@PathVariable Long id) {
        return rewardService.deleteReward(id);
    }
}
