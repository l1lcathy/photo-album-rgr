package com.photoalbum.service;

import com.photoalbum.model.User;
import com.photoalbum.repository.jdbc.UserRepositoryJdbc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepositoryJdbc userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(User user) {
        // Хешируем пароль перед сохранением
        user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        
        // Устанавливаем значения по умолчанию
        user.setRole("USER");
        user.setEnabled(true);
        
        // Сохраняем через JDBC репозиторий
        userRepository.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    // ИСПРАВЛЕНО: int → Long
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}