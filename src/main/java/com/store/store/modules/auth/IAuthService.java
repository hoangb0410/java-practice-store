package com.store.store.modules.auth;

import org.springframework.http.ResponseEntity;
import com.store.store.common.response.ApiResponse;
import com.store.store.modules.auth.dto.LoginRequest;
import com.store.store.modules.auth.dto.RefreshTokenRequest;
import com.store.store.modules.auth.dto.RegisterRequest;
import com.store.store.modules.auth.dto.StoreRegisterRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface IAuthService {
    ResponseEntity<ApiResponse<Object>> register(RegisterRequest request);

    ResponseEntity<ApiResponse<Object>> login(LoginRequest request);

    ResponseEntity<ApiResponse<Object>> logout(HttpServletRequest userId);

    ResponseEntity<ApiResponse<Object>> refreshToken(RefreshTokenRequest request);

    ResponseEntity<ApiResponse<Object>> storeRegister(StoreRegisterRequest request);

    ResponseEntity<ApiResponse<Object>> storeLogin(LoginRequest request);

}
