package com.store.store.modules.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.store.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
