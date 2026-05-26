package com.photoalbum.service;

import com.photoalbum.model.User;
import com.photoalbum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(User user) {

        userRepository.save(user);

    }
}