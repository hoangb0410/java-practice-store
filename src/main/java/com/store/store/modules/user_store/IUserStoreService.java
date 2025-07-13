package com.store.store.modules.user_store;

import org.springframework.http.ResponseEntity;

import com.store.store.common.response.ApiResponse;
import com.store.store.modules.user_store.dto.CreateTransactionRequest;
import com.store.store.modules.user_store.dto.GetListTransactionRequest;

public interface IUserStoreService {
    ResponseEntity<ApiResponse<Object>> addUserToStore(Long userId, Long storeId);

    ResponseEntity<ApiResponse<Object>> removeUser(Long userId, Long storeId);

    ResponseEntity<ApiResponse<Object>> getListUsersOfStore(Long storeId);

    ResponseEntity<ApiResponse<Object>> createTransaction(Long storeId, Long userId, CreateTransactionRequest request);

    ResponseEntity<ApiResponse<Object>> getListTransactions(Long storeId, GetListTransactionRequest request);
}
