package com.store.store.modules.store;

import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.store.store.common.ErrorHelper;
import com.store.store.common.pagination.PaginateHelper;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.Store;
import com.store.store.modules.store.dto.GetStoreRequest;

@Service
public class StoreServiceImpl implements IStoreService {
    private final StoreRepository storeRepository;

    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getStores(GetStoreRequest req) {
        try {
            Specification<Store> spec = (root, query, cb) -> {
                if (req.getSearch() != null && !req.getSearch().isBlank()) {
                    return cb.like(cb.lower(root.get("name")), "%" + req.getSearch().toLowerCase() + "%");
                }
                return cb.conjunction();
            };

            if (Boolean.TRUE.equals(req.getAll())) {
                return ResponseEntity.ok(ApiResponse.success(storeRepository.findAll(spec), 200));
            }
            return ResponseEntity.ok(ApiResponse.success(PaginateHelper.paginate(req, storeRepository, spec), 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Error fetching stores: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> approveStore(Long id) {
        try {
            Optional<Store> storeOpt = storeRepository.findById(id);
            if (storeOpt.isEmpty()) {
                return ErrorHelper.notFound("Store not found");
            }
            Store store = storeOpt.get();
            if (store.getIsApproved()) {
                return ErrorHelper.badRequest("Store is already approved");
            }

            store.setIsApproved(true);
            storeRepository.save(store);

            return ResponseEntity.ok(ApiResponse.success("Store approved successfully", 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Error approving store: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> findById(Long id) {
        try {
            Optional<Store> storeOpt = storeRepository.findById(id);
            if (storeOpt.isEmpty()) {
                return ErrorHelper.notFound("Store not found");
            }
            return ResponseEntity.ok(ApiResponse.success(storeOpt.get(), 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Error fetching store by ID: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> delete(Long id) {
        try {
            if (!storeRepository.existsById(id)) {
                return ErrorHelper.notFound("Store not found");
            }
            storeRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Store deleted successfully", 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Delete store failed: " + e.getMessage());
        }
    }
}
