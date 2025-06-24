package com.store.store.common.pagination;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public class PaginateHelper {
    public static <T> PaginationResponse<T> paginate(
            PaginationRequest req,
            JpaSpecificationExecutor<T> repo,
            Specification<T> spec) {
        int page = req.getPage() != null ? req.getPage() : 1;
        int limit = req.getLimit() != null ? req.getLimit() : 10;

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<T> result = repo.findAll(spec, pageable);

        return new PaginationResponse<>(
                result.getContent(),
                result.getTotalElements(),
                page,
                limit,
                result.getTotalPages());
    }
}
