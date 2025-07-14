package com.store.store.modules.rank;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.store.common.response.ApiResponse;
import com.store.store.modules.rank.dto.CreateRankRequest;
import com.store.store.modules.rank.dto.GetRanksRequest;
import com.store.store.modules.rank.dto.UpdateRankRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/ranks")
public class RankController {
    private final IRankService rankService;

    public RankController(IRankService rankService) {
        this.rankService = rankService;
    }

    @Operation(summary = "Get all ranks", description = "API to fetch all ranks", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getRanks(@ParameterObject GetRanksRequest req) {
        return rankService.getRanks(req);
    }

    @Operation(summary = "Create rank", description = "API to create rank", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createRank(@Valid @RequestBody CreateRankRequest req) {
        return rankService.createRank(req);
    }

    @Operation(summary = "Get rank by ID", description = "API to fetch rank by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> findById(@PathVariable Long id) {
        return rankService.findById(id);
    }

    @Operation(summary = "Update rank", description = "API to update rank", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateRank(@PathVariable Long id,
            @Valid @RequestBody UpdateRankRequest req) {
        return rankService.updateRank(id, req);
    }

    @Operation(summary = "Delete rank", description = "API to delete rank", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteRank(@PathVariable Long id) {
        return rankService.deleteRank(id);
    }
}
