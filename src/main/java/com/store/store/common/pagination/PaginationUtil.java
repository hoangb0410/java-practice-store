package com.store.store.common.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationUtil {
    public static Pageable getPageable(Integer page, Integer limit) {
        int safePage = (page != null ? page : 1) - 1;
        int safeLimit = (limit != null ? limit : 20);
        return PageRequest.of(safePage, safeLimit);
    }
}
