package com.store.store.modules.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.store.store.common.jwt.JwtService;
import com.store.store.common.redis.RedisService;
import com.store.store.model.User;
import com.store.store.modules.auth.dto.AuthResponse;
import com.store.store.modules.auth.dto.LoginRequest;
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
    public User register(RegisterRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        return userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        String key = "refresh_token:" + user.getId();
        redisService.set(key, refreshToken, 5L * 30 * 24 * 60 * 60); // 5 months in seconds

        return new AuthResponse(user, accessToken, refreshToken);
    }

    @Override
    public void logout(Long userId) {
        String key = "refresh_token:" + userId;
        redisService.delete(key);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String userId = jwtService.extractUserId(refreshToken);
        String key = "refresh_token:" + userId;

        String storedToken = redisService.get(key);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new RuntimeException("Refresh token not found or mismatched");
        }

        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtService.generateAccessToken(user);

        return new AuthResponse(user, newAccessToken, refreshToken);
    }
}
