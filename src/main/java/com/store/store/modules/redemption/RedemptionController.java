package com.store.store.modules.redemption;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.store.common.response.ApiResponse;
import com.store.store.modules.redemption.dto.CreateRedemptionRequest;
import com.store.store.modules.redemption.dto.GetListRedemptionsRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/redemptions")
public class RedemptionController {

    private final IRedemptionService redemptionService;

    public RedemptionController(IRedemptionService redemptionService) {
        this.redemptionService = redemptionService;
    }

    @Operation(summary = "Create redemption", description = "API to create redemption", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createRedemption(@AuthenticationPrincipal(expression = "id") Long id,
            @Valid @RequestBody CreateRedemptionRequest req) {
        return redemptionService.createRedemption(id, req);
    }

    @Operation(summary = "Get list of redemptions", description = "API to get list of redemptions", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getListRedemptions(
            @ParameterObject GetListRedemptionsRequest request) {
        return redemptionService.getListRedemptions(request);
    }

    @Operation(summary = "Get redemption details", description = "API to get redemption details by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getRedemptionDetails(@PathVariable Long id) {
        return redemptionService.getRedemptionDetails(id);
    }
}
