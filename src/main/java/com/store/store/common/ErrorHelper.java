package com.store.store.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.store.store.common.response.ApiResponse;

import java.util.List;

public class ErrorHelper {

    public static ResponseEntity<ApiResponse<Object>> badRequest(String message) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request Exception",
                        List.of(message)));
    }

    public static ResponseEntity<ApiResponse<Object>> badRequest(List<String> messages) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request Exception",
                        messages));
    }

    public static ResponseEntity<ApiResponse<Object>> unauthorized(String message) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized",
                        List.of(message)));
    }

    public static ResponseEntity<ApiResponse<Object>> forbidden(String message) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(
                        HttpStatus.FORBIDDEN.value(),
                        "Forbidden",
                        List.of(message)));
    }

    public static ResponseEntity<ApiResponse<Object>> notFound(String message) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        List.of(message)));
    }

    public static ResponseEntity<ApiResponse<Object>> internalServerError(String message) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        List.of(message)));
    }
}
