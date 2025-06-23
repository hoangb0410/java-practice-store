package com.store.store.modules.user;

import java.util.List;

import com.store.store.model.User;
import com.store.store.modules.user.dto.ChangePasswordRequest;
import com.store.store.modules.user.dto.UpdateUserRequest;

public interface IUserService {
    List<User> getAllUsers();

    void deleteUser(Long id);

    User findById(Long id);

    User updateUser(Long id, UpdateUserRequest request);

    void changePassword(Long id, ChangePasswordRequest request);
}
