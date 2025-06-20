package com.store.store.modules.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.store.common.ApiResponse;
import com.store.store.common.ErrorHelper;
import com.store.store.model.User;
import com.store.store.modules.auth.dto.AuthResponse;
import com.store.store.modules.auth.dto.LoginRequest;
import com.store.store.modules.auth.dto.RegisterRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);
            return ResponseEntity.ok(ApiResponse.success(user, 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("User registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success(response, 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout/{userId}")
    public ResponseEntity<ApiResponse<Object>> logout(@PathVariable Long userId) {
        try {
            authService.logout(userId);
            return ResponseEntity.ok(ApiResponse.success("Logged out successfully", 200));
        } catch (Exception e) {
            return ErrorHelper.internalServerError("Logout failed: " + e.getMessage());
        }
    }
}
