package com.store.store.common.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private int code;
    private T data;
    private String message;
    private List<String> errors;

    public static <T> ApiResponse<T> success(T data, int code) {
        return new ApiResponse<>(true, code, data, null, null);
    }

    public static ApiResponse<Object> error(int code, String message, List<String> errors) {
        return new ApiResponse<>(false, code, null, message, errors);
    }
}
