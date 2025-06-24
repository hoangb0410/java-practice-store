package com.store.store.common.pagination;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ParameterObject
public class PaginationRequest {
    @Schema(description = "Page number", example = "1")
    @Min(1)
    private Integer page = 1;

    @Schema(description = "Limit", example = "10")
    @Min(1)
    @Max(1000)
    private Integer limit = 10;

    @Schema(description = "Search keyword", example = "keyword")
    private String search;

    @Schema(description = "All records (skip paging)", defaultValue = "false")
    private Boolean all = false;
}
