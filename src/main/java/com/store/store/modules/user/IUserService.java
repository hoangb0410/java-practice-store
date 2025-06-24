package com.store.store.modules.user;

import com.store.store.common.pagination.PaginationRequest;
import com.store.store.model.User;
import com.store.store.modules.user.dto.ChangePasswordRequest;
import com.store.store.modules.user.dto.UpdateUserRequest;

public interface IUserService {
    Object getUsers(PaginationRequest req);

    void deleteUser(Long id);

    User findById(Long id);

    User updateUser(Long id, UpdateUserRequest request);

    void changePassword(Long id, ChangePasswordRequest request);
}
