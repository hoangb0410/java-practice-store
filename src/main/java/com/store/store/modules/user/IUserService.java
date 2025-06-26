package com.store.store.modules.user;

import org.springframework.http.ResponseEntity;

import com.store.store.common.response.ApiResponse;
import com.store.store.modules.user.dto.ChangePasswordRequest;
import com.store.store.modules.user.dto.GetUsersRequest;
import com.store.store.modules.user.dto.UpdateUserRequest;

public interface IUserService {
    ResponseEntity<ApiResponse<Object>> getUsers(GetUsersRequest req);

    ResponseEntity<ApiResponse<Object>> deleteUser(Long id);

    ResponseEntity<ApiResponse<Object>> findById(Long id);

    ResponseEntity<ApiResponse<Object>> updateUser(Long id, UpdateUserRequest request);

    ResponseEntity<ApiResponse<Object>> changePassword(Long id, ChangePasswordRequest request);
}
