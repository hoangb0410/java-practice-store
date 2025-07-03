package com.store.store.modules.reward;

import org.springframework.http.ResponseEntity;

import com.store.store.common.response.ApiResponse;
import com.store.store.modules.reward.dto.CreateRewardRequest;
import com.store.store.modules.reward.dto.GetRewardsRequest;
import com.store.store.modules.reward.dto.UpdateRewardRequest;

public interface IRewardService {
    ResponseEntity<ApiResponse<Object>> getRewards(GetRewardsRequest req);

    ResponseEntity<ApiResponse<Object>> createReward(CreateRewardRequest req);

    ResponseEntity<ApiResponse<Object>> findById(Long id);

    ResponseEntity<ApiResponse<Object>> updateReward(Long id, UpdateRewardRequest req);

    ResponseEntity<ApiResponse<Object>> deleteReward(Long id);
}
