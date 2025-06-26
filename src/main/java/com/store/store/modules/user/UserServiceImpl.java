package com.store.store.modules.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.store.store.common.ErrorHelper;
import com.store.store.common.pagination.PaginateHelper;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.User;
import com.store.store.modules.user.dto.ChangePasswordRequest;
import com.store.store.modules.user.dto.GetUsersRequest;
import com.store.store.modules.user.dto.UpdateUserRequest;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getUsers(GetUsersRequest req) {
        try {
            Specification<User> spec = (root, query, cb) -> {
                if (req.getSearch() != null && !req.getSearch().isBlank()) {
                    return cb.like(cb.lower(root.get("name")), "%" + req.getSearch().toLowerCase() + "%");
                }
                return cb.conjunction();
            };

            if (Boolean.TRUE.equals(req.getAll())) {
                return ResponseEntity.ok(ApiResponse.success(userRepository.findAll(spec), 200));
            }

            return ResponseEntity.ok(ApiResponse.success(PaginateHelper.paginate(req, userRepository, spec), 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Get users failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> deleteUser(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                return ErrorHelper.notFound("User not found");
            }
            userRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Delete user failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> findById(Long id) {
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isEmpty()) {
                return ErrorHelper.notFound("User not found");
            }
            return ResponseEntity.ok(ApiResponse.success(user.get(), 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Find user failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> updateUser(Long id, UpdateUserRequest request) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isEmpty()) {
                return ErrorHelper.notFound("User not found with ID: " + id);
            }
            User user = optionalUser.get();

            if (request.getName() != null) {
                user.setName(request.getName());
            }
            if (request.getEmail() != null) {
                Optional<User> existingEmailUser = userRepository.findByEmail(request.getEmail());
                if (existingEmailUser.isPresent() && !existingEmailUser.get().getId().equals(id)) {
                    return ErrorHelper.badRequest("Email already exists");
                }
                user.setEmail(request.getEmail());
            }
            if (request.getPhone() != null) {
                Optional<User> existingPhoneUser = userRepository.findByPhone(request.getPhone());
                if (existingPhoneUser.isPresent() && !existingPhoneUser.get().getId().equals(id)) {
                    return ErrorHelper.badRequest("Phone number already exists");
                }
                user.setPhone(request.getPhone());
            }
            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.success(user, 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Update user failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> changePassword(Long id, ChangePasswordRequest request) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isEmpty()) {
                return ErrorHelper.notFound("User not found");
            }
            User user = optionalUser.get();

            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                return ErrorHelper.badRequest("Old password is incorrect");
            }

            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                return ErrorHelper.badRequest("New password and confirm password do not match");
            }

            if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                return ErrorHelper.badRequest("New password must be different from old password");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.success("Password changed successfully", 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Change password failed: " + e.getMessage());
        }
    }
}
