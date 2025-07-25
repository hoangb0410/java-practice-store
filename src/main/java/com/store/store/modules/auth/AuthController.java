package com.store.store.modules.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.store.common.response.ApiResponse;
import com.store.store.modules.auth.dto.LoginRequest;
import com.store.store.modules.auth.dto.RefreshTokenRequest;
import com.store.store.modules.auth.dto.RegisterRequest;
import com.store.store.modules.auth.dto.SendOtpRequest;
import com.store.store.modules.auth.dto.StoreRegisterRequest;
import com.store.store.modules.auth.dto.VerifyOtpRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Logout", description = "API to logout", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> logout(
            @AuthenticationPrincipal(expression = "id") Long id,
            @AuthenticationPrincipal(expression = "type") String type) {
        return authService.logout(id, type);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Object>> refreshAccessToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/store/register")
    public ResponseEntity<ApiResponse<Object>> storeRegister(@Valid @RequestBody StoreRegisterRequest request) {
        return authService.storeRegister(request);
    }

    @PostMapping("/store/login")
    public ResponseEntity<ApiResponse<Object>> storeLogin(@Valid @RequestBody LoginRequest request) {
        return authService.storeLogin(request);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Object>> sendOTP(@Valid @RequestBody SendOtpRequest request) {
        return authService.sendOTP(request);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Object>> verifyOTP(@Valid @RequestBody VerifyOtpRequest request) {
        return authService.verifyOTP(request);
    }
}
