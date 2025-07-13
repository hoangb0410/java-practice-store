package com.store.store.modules.rank;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.store.store.model.Rank;

public interface RankRepository extends JpaRepository<Rank, Long>, JpaSpecificationExecutor<Rank> {
    Optional<Rank> findByName(String name);

    Optional<Rank> findTopByOrderByPointsThresholdAsc();

    List<Rank> findAllByOrderByPointsThresholdDesc();
}