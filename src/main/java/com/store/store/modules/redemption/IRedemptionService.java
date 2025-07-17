package com.store.store.modules.redemption;

import org.springframework.http.ResponseEntity;

import com.store.store.common.response.ApiResponse;
import com.store.store.modules.redemption.dto.CreateRedemptionRequest;
import com.store.store.modules.redemption.dto.GetListRedemptionsRequest;

public interface IRedemptionService {
    ResponseEntity<ApiResponse<Object>> createRedemption(Long userId, CreateRedemptionRequest request);

    ResponseEntity<ApiResponse<Object>> getListRedemptions(GetListRedemptionsRequest request);

    ResponseEntity<ApiResponse<Object>> getRedemptionDetails(Long id);
}
