package com.store.store.modules.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.store.store.modules.user.entity.User;
import com.store.store.modules.user.repository.UserRepository;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
