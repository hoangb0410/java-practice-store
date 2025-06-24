package com.store.store.health;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.store.common.ErrorHelper;
import com.store.store.common.response.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<String>> checkHealth() {
        return ResponseEntity.ok(ApiResponse.success("OK", 200));
    }

    @GetMapping("/check-error")
    public ResponseEntity<ApiResponse<Object>> checkHealthError() {
        return ErrorHelper.badRequest("Invalid request");
    }
}
