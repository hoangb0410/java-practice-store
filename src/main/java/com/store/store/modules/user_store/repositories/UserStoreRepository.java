package com.store.store.modules.user_store.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.store.store.model.UserStore;

public interface UserStoreRepository extends JpaRepository<UserStore, Long>, JpaSpecificationExecutor<UserStore> {
    boolean existsByUserIdAndStoreId(Long userId, Long storeId);

    void deleteByUserIdAndStoreId(Long userId, Long storeId);

    Optional<UserStore> findByUserIdAndStoreId(Long userId, Long storeId);
}
