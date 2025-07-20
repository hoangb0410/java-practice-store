package com.store.store.common;

import org.springframework.http.HttpStatus;

import com.store.store.common.exception.ApiException;

import java.util.List;

public class ErrorHelper {
        public static void badRequest(String message) {
                throw new ApiException(HttpStatus.BAD_REQUEST.value(), "Bad Request", List.of(message));
        }

        public static void badRequest(List<String> messages) {
                throw new ApiException(HttpStatus.BAD_REQUEST.value(), "Bad Request", messages);
        }

        public static void unauthorized(String message) {
                throw new ApiException(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", List.of(message));
        }

        public static void forbidden(String message) {
                throw new ApiException(HttpStatus.FORBIDDEN.value(), "Forbidden", List.of(message));
        }

        public static void notFound(String message) {
                throw new ApiException(HttpStatus.NOT_FOUND.value(), "Not Found", List.of(message));
        }

        public static void internalServerError(String message) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                                List.of(message));
        }
}
