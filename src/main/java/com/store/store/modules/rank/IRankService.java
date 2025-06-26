package com.store.store.modules.rank;

import org.springframework.http.ResponseEntity;

import com.store.store.common.response.ApiResponse;
import com.store.store.modules.rank.dto.CreateRankRequest;
import com.store.store.modules.rank.dto.GetRanksRequest;
import com.store.store.modules.rank.dto.UpdateRankRequest;

public interface IRankService {
    ResponseEntity<ApiResponse<Object>> getRanks(GetRanksRequest req);

    ResponseEntity<ApiResponse<Object>> createRank(CreateRankRequest req);

    ResponseEntity<ApiResponse<Object>> findById(Long id);

    ResponseEntity<ApiResponse<Object>> updateRank(Long id, UpdateRankRequest req);

    ResponseEntity<ApiResponse<Object>> deleteRank(Long id);

}
