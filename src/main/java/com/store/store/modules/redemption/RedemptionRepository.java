package com.store.store.modules.redemption;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.store.store.model.Redemption;

public interface RedemptionRepository extends JpaRepository<Redemption, Long>, JpaSpecificationExecutor<Redemption> {
}
