package com.store.store.modules.user;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.store.store.common.response.ApiResponse;
import com.store.store.model.User;
import com.store.store.modules.user.dto.ChangePasswordRequest;
import com.store.store.modules.user.dto.GetUsersRequest;
import com.store.store.modules.user.dto.UpdateUserRequest;

public interface IUserService {
    ResponseEntity<ApiResponse<Object>> getUsers(GetUsersRequest req);

    ResponseEntity<ApiResponse<Object>> deleteUser(Long id);

    ResponseEntity<ApiResponse<Object>> findById(Long id);

    ResponseEntity<ApiResponse<Object>> updateUser(Long id, UpdateUserRequest request);

    ResponseEntity<ApiResponse<Object>> changePassword(Long id, ChangePasswordRequest request);

    List<User> findAllUsers();

    void saveAllUsers(List<User> users);

    void clearRankByRankId(Long rankId);

    void clearRankForAll();

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findOne(Long id);

    User save(User user);
}
