package com.store.store.modules.store;

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

import com.store.store.common.response.ApiResponse;
import com.store.store.modules.store.dto.GetStoreRequest;
import com.store.store.modules.store.dto.UpdateStoreRequest;
import com.store.store.modules.user.dto.ChangePasswordRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/stores")
public class StoreController {
    private final IStoreService storeService;

    public StoreController(IStoreService storeService) {
        this.storeService = storeService;
    }

    @Operation(summary = "Get all stores", description = "API for admin to fetch all registered stores", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getStores(@ParameterObject GetStoreRequest req) {
        return storeService.getStores(req);
    }

    @Operation(summary = "Approve store", description = "API for admin to approve store", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Object>> approveStore(@PathVariable Long id) {
        return storeService.approveStore(id);
    }

    @Operation(summary = "Get store by ID", description = "API to fetch store info by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> findById(@PathVariable Long id) {
        return storeService.findById(id);
    }

    @Operation(summary = "Delete store by ID", description = "API to delete a specific store by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) {
        return storeService.delete(id);
    }

    @Operation(summary = "Change password for store", description = "API to change password for store", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE')")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(@AuthenticationPrincipal(expression = "id") Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        return storeService.changePassword(id, request);
    }

    @Operation(summary = "Update store by ID", description = "API to update a specific store by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateStore(@PathVariable Long id,
            @Valid @RequestBody UpdateStoreRequest request) {
        return storeService.updateStore(id, request);
    }
}
