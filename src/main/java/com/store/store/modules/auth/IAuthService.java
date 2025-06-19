package com.store.store.modules.auth;

import com.store.store.model.User;
import com.store.store.modules.auth.dto.AuthResponse;
import com.store.store.modules.auth.dto.LoginRequest;
import com.store.store.modules.auth.dto.RegisterRequest;

public interface IAuthService {
    User register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void logout(Long userId);
}
