package com.store.store.modules.store;

import org.springframework.http.ResponseEntity;

import com.store.store.common.response.ApiResponse;
import com.store.store.modules.store.dto.GetStoreRequest;

public interface IStoreService {
    ResponseEntity<ApiResponse<Object>> getStores(GetStoreRequest req);

    ResponseEntity<ApiResponse<Object>> approveStore(Long id);

    ResponseEntity<ApiResponse<Object>> findById(Long id);

    ResponseEntity<ApiResponse<Object>> delete(Long id);
}
