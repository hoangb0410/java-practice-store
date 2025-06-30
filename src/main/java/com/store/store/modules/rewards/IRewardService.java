package com.store.store.modules.rewards;

import org.springframework.http.ResponseEntity;

import com.store.store.common.response.ApiResponse;
import com.store.store.modules.rewards.dto.GetRewardsRequest;

public interface IRewardService {
    ResponseEntity<ApiResponse<Object>> getRewards(GetRewardsRequest req);
}
