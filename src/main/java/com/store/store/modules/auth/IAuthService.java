package com.store.store.modules.auth;

import org.springframework.http.ResponseEntity;
import com.store.store.common.response.ApiResponse;
import com.store.store.modules.auth.dto.LoginRequest;
import com.store.store.modules.auth.dto.RefreshTokenRequest;
import com.store.store.modules.auth.dto.RegisterRequest;

public interface IAuthService {
    ResponseEntity<ApiResponse<Object>> register(RegisterRequest request);

    ResponseEntity<ApiResponse<Object>> login(LoginRequest request);

    ResponseEntity<ApiResponse<Object>> logout(Long userId);

    ResponseEntity<ApiResponse<Object>> refreshToken(RefreshTokenRequest request);
}
