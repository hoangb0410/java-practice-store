package com.store.store.modules.user;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.store.common.ErrorHelper;
import com.store.store.common.pagination.PaginationRequest;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.User;
import com.store.store.modules.user.dto.ChangePasswordRequest;
import com.store.store.modules.user.dto.UpdateUserRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users", description = "API for admin to fetch all registered users", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getUsers(@ParameterObject PaginationRequest req) {
        try {
            Object users = userService.getUsers(req);
            return ResponseEntity.ok(ApiResponse.success(users, 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Login failed: " + e.getMessage());
        }
    }

    @Operation(summary = "Delete user by ID", description = "API to delete a specific user by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", 200));
        } catch (Exception e) {
            return ErrorHelper.internalServerError("Failed to delete user: " + e.getMessage());
        }
    }

    @Operation(summary = "Get user by ID", description = "API to fetch user info by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> findById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(ApiResponse.success(user, 200));
        } catch (Exception e) {
            return ErrorHelper.internalServerError("Failed to get user: " + e.getMessage());
        }
    }

    @Operation(summary = "Update user by ID", description = "API to update user by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateUser(@PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            User user = userService.updateUser(id, request);
            return ResponseEntity.ok(ApiResponse.success(user, 200));
        } catch (Exception e) {
            return ErrorHelper.internalServerError("Failed to update user: " + e.getMessage());
        }
    }

    @Operation(summary = "Change password for user", description = "API to change password for user", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(@AuthenticationPrincipal(expression = "id") Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(id, request);
            return ResponseEntity.ok(ApiResponse.success("Password changed successfully", 200));
        } catch (Exception e) {
            return ErrorHelper.internalServerError("Failed to change password: " + e.getMessage());
        }
    }
}
