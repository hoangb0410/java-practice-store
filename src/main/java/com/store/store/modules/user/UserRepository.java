package com.store.store.modules.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.store.store.model.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    @Modifying
    @Query("UPDATE User u SET u.rank = NULL")
    void clearRankForAll();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.rank = NULL WHERE u.rank.id = :rankId")
    void clearRankByRankId(@Param("rankId") Long rankId);
}
