package com.store.store.modules.user;

import org.springframework.http.ResponseEntity;

import com.store.store.common.pagination.PaginationRequest;
import com.store.store.common.response.ApiResponse;
import com.store.store.modules.user.dto.ChangePasswordRequest;
import com.store.store.modules.user.dto.UpdateUserRequest;

public interface IUserService {
    ResponseEntity<ApiResponse<Object>> getUsers(PaginationRequest req);

    ResponseEntity<ApiResponse<Object>> deleteUser(Long id);

    ResponseEntity<ApiResponse<Object>> findById(Long id);

    ResponseEntity<ApiResponse<Object>> updateUser(Long id, UpdateUserRequest request);

    ResponseEntity<ApiResponse<Object>> changePassword(Long id, ChangePasswordRequest request);
}
