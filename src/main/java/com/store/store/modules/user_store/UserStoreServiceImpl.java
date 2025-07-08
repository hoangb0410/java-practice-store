package com.store.store.modules.user_store;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.store.store.common.ErrorHelper;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.Store;
import com.store.store.model.User;
import com.store.store.model.UserStore;
import com.store.store.modules.store.StoreRepository;
import com.store.store.modules.user.UserRepository;

@Service
public class UserStoreServiceImpl implements IUserStoreService {
    private final UserStoreRepository userStoreRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public UserStoreServiceImpl(UserStoreRepository userStoreRepository, UserRepository userRepository,
            StoreRepository storeRepository) {
        this.userStoreRepository = userStoreRepository;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> addUserToStore(Long userId, Long storeId) {
        try {
            if (!userRepository.existsById(userId)) {
                return ErrorHelper.notFound("User not found with id: " + userId);
            }
            if (!storeRepository.existsById(storeId)) {
                return ErrorHelper.notFound("Store not found with id: " + storeId);
            }

            if (userStoreRepository.existsByUserIdAndStoreId(userId, storeId)) {
                return ErrorHelper.badRequest("User already added to this store");
            }

            UserStore userStore = UserStore.builder()
                    .userId(userId)
                    .storeId(storeId)
                    .build();
            userStoreRepository.save(userStore);

            return ResponseEntity.ok(ApiResponse.success("Add user to store successfully", 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("An error occurred while adding user to store: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> removeUser(Long userId, Long storeId) {
        try {
            Optional<UserStore> optional = userStoreRepository.findByUserIdAndStoreId(userId, storeId);
            if (optional.isEmpty()) {
                return ErrorHelper.notFound("User is not added to this store");
            }

            userStoreRepository.delete(optional.get());

            return ResponseEntity.ok(ApiResponse.success("Removed user from store successfully", 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Error removing user from store: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getListUsersOfStore(Long storeId) {
        try {
            Optional<Store> optionalStore = storeRepository.findById(storeId);
            if (optionalStore.isEmpty()) {
                return ErrorHelper.notFound("Store not found with id: " + storeId);
            }
            List<User> users = optionalStore.get().getUsers();
            return ResponseEntity.ok(ApiResponse.success(users, 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Error retrieving users of store: " + e.getMessage());
        }
    }
}
