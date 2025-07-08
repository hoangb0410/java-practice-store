package com.store.store.modules.user_store;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.store.store.common.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/user-store")
public class UserStoreController {
    private final IUserStoreService userStoreService;

    public UserStoreController(IUserStoreService userStoreService) {
        this.userStoreService = userStoreService;
    }

    @Operation(summary = "Add user to store", description = "API to add user to store", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('STORE')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Object>> addUserToStore(
            @RequestParam Long userId,
            @RequestParam Long storeId) {
        return userStoreService.addUserToStore(userId, storeId);
    }

    @Operation(summary = "Remove user from store", description = "API to remove user from store", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('STORE')")
    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<Object>> removeUser(
            @RequestParam Long userId,
            @RequestParam Long storeId) {
        return userStoreService.removeUser(userId, storeId);
    }

    @Operation(summary = "Get list users of store", description = "API to get list users of store", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('STORE')")
    @GetMapping("/{storeId}/list-users")
    public ResponseEntity<ApiResponse<Object>> getListUsersOfStore(
            @PathVariable Long storeId) {
        return userStoreService.getListUsersOfStore(storeId);
    }
}
