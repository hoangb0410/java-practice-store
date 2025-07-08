package com.store.store.modules.user_store;

import org.springframework.http.ResponseEntity;

import com.store.store.common.response.ApiResponse;

public interface IUserStoreService {
    ResponseEntity<ApiResponse<Object>> addUserToStore(Long userId, Long storeId);
    ResponseEntity<ApiResponse<Object>> removeUser(Long userId, Long storeId);
    ResponseEntity<ApiResponse<Object>> getListUsersOfStore(Long storeId);
}
