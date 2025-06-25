package com.store.store.modules.auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.store.store.common.ErrorHelper;
import com.store.store.common.jwt.JwtService;
import com.store.store.common.redis.RedisService;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.User;
import com.store.store.modules.auth.dto.AuthResponse;
import com.store.store.modules.auth.dto.LoginRequest;
import com.store.store.modules.auth.dto.RefreshTokenRequest;
import com.store.store.modules.auth.dto.RegisterRequest;
import com.store.store.modules.user.UserRepository;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RedisService redisService;

    @Override
    public ResponseEntity<ApiResponse<Object>> register(RegisterRequest request) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ErrorHelper.badRequest("Email already exists");
            }
            if (userRepository.findByPhone(request.getPhone()).isPresent()) {
                return ErrorHelper.badRequest("Phone number already exists");
            }

            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();
            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.success(user, 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("User registration failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> login(LoginRequest request) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
            if (optionalUser.isEmpty()) {
                return ErrorHelper.badRequest("Invalid credentials");
            }
            User user = optionalUser.get();

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ErrorHelper.badRequest("Invalid credentials");
            }

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            String key = "refresh_token:" + user.getId();
            redisService.set(key, refreshToken, 5L * 30 * 24 * 60 * 60); // 5 months in seconds

            AuthResponse response = new AuthResponse(user, accessToken, refreshToken);

            return ResponseEntity.ok(ApiResponse.success(response, 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Login failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> logout(Long userId) {
        try {
            String key = "refresh_token:" + userId;
            redisService.delete(key);
            return ResponseEntity.ok(ApiResponse.success("Logout successful", 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Logout failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> refreshToken(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            if (!jwtService.isTokenValid(refreshToken)) {
                return ErrorHelper.badRequest("Invalid refresh token");
            }

            String userId = jwtService.extractUserId(refreshToken);
            String key = "refresh_token:" + userId;

            String storedToken = redisService.get(key);
            if (storedToken == null || !storedToken.equals(refreshToken)) {
                return ErrorHelper.badRequest("Refresh token not found or mismatched");
            }

            Optional<User> user = userRepository.findById(Long.parseLong(userId));
            if (user.isEmpty()) {
                return ErrorHelper.notFound("User not found");
            }

            String newAccessToken = jwtService.generateAccessToken(user.get());

            AuthResponse response = new AuthResponse(user.get(), newAccessToken, refreshToken);
            return ResponseEntity.ok(ApiResponse.success(response, 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Token refresh failed: " + e.getMessage());
        }
    }
}
