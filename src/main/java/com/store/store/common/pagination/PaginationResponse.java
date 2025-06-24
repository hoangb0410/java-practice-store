package com.store.store.common.pagination;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaginationResponse<T> {
    private List<T> items;
    private long total;
    private int page;
    private int limit;
    private int totalPage;
}
