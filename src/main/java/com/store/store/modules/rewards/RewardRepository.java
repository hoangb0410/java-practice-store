package com.store.store.modules.rewards;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.store.store.model.Reward;

public interface RewardRepository extends JpaRepository<Reward, Long>, JpaSpecificationExecutor<Reward> {
    Optional<Reward> findByName(String name);
}
