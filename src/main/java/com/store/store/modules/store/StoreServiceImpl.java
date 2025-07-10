package com.store.store.modules.store;

import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.store.store.common.ErrorHelper;
import com.store.store.common.pagination.PaginateHelper;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.Store;
import com.store.store.modules.store.dto.GetStoreRequest;
import com.store.store.modules.store.dto.UpdateStoreRequest;
import com.store.store.modules.user.dto.ChangePasswordRequest;

@Service
public class StoreServiceImpl implements IStoreService {
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    public StoreServiceImpl(StoreRepository storeRepository, PasswordEncoder passwordEncoder) {
        this.storeRepository = storeRepository;
        this.passwordEncoder = passwordEncoder;
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

    @Override
    public ResponseEntity<ApiResponse<Object>> changePassword(Long id, ChangePasswordRequest request) {
        try {
            Optional<Store> optionalStore = storeRepository.findById(id);
            if (optionalStore.isEmpty()) {
                return ErrorHelper.notFound("Store not found");
            }
            Store store = optionalStore.get();

            if (!passwordEncoder.matches(request.getOldPassword(), store.getPassword())) {
                return ErrorHelper.badRequest("Old password is incorrect");
            }

            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                return ErrorHelper.badRequest("New password and confirm password do not match");
            }

            if (passwordEncoder.matches(request.getNewPassword(), store.getPassword())) {
                return ErrorHelper.badRequest("New password must be different from old password");
            }

            store.setPassword(passwordEncoder.encode(request.getNewPassword()));
            storeRepository.save(store);
            return ResponseEntity.ok(ApiResponse.success("Password changed successfully", 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Change password failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> updateStore(Long id, UpdateStoreRequest request) {
        try {
            Optional<Store> optionalStore = storeRepository.findById(id);
            if (optionalStore.isEmpty()) {
                return ErrorHelper.notFound("Store not found with ID: " + id);
            }
            Store store = optionalStore.get();

            if (request.getName() != null) {
                store.setName(request.getName());
            }
            if (request.getEmail() != null) {
                Optional<Store> existingEmailStore = storeRepository.findByEmail(request.getEmail());
                if (existingEmailStore.isPresent() && !existingEmailStore.get().getId().equals(id)) {
                    return ErrorHelper.badRequest("Email already exists");
                }
                store.setEmail(request.getEmail());
            }

            storeRepository.save(store);
            return ResponseEntity.ok(ApiResponse.success(store, 200));

        } catch (Exception e) {
            return ErrorHelper.badRequest("Update store failed: " + e.getMessage());
        }
    }
}
