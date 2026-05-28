package com.photoalbum.service;

import com.photoalbum.model.User;
import com.photoalbum.model.VerificationToken;
import com.photoalbum.repository.jdbc.UserRepositoryJdbc;
import com.photoalbum.repository.jdbc.VerificationTokenRepositoryJdbc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepositoryJdbc userRepository;
    
    @Autowired
    private VerificationTokenRepositoryJdbc tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;

    public void registerUser(User user) {
        
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        
        user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        
        
        user.setRole("USER");
        user.setEnabled(false);  
        User savedUser = userRepository.save(user);
        
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
            savedUser.getId(),
            token,
            LocalDateTime.now().plusHours(24)
        );
        tokenRepository.save(verificationToken);
        
        System.out.println("Отправка email на: " + user.getEmail());
        emailService.sendVerificationEmail(user.getEmail(), token);
    }
    
    public boolean confirmRegistration(String token) {
        System.out.println("=== confirmRegistration ===");
        System.out.println("Токен: " + token);
        
        Optional<VerificationToken> vtOpt = tokenRepository.findByToken(token);
        System.out.println("Токен найден в БД: " + vtOpt.isPresent());
        
        if (vtOpt.isEmpty()) {
            return false;
        }
        
        VerificationToken vt = vtOpt.get();
        System.out.println("userId: " + vt.getUserId());
        System.out.println("ExpiryDate: " + vt.getExpiryDate());
        System.out.println("Текущее время: " + LocalDateTime.now());
        System.out.println("isExpired: " + vt.isExpired());
        
        if (vt.isExpired()) {
            System.out.println("Токен просрочен, удаляем...");
            tokenRepository.deleteByUserId(vt.getUserId());
            return false;
        }
        
        Optional<User> userOpt = userRepository.findById(vt.getUserId());
        if (userOpt.isEmpty()) {
            System.out.println("Пользователь не найден!");
            return false;
        }
        
        User user = userOpt.get();
        System.out.println("Пользователь найден: " + user.getUsername() + ", enabled: " + user.isEnabled());
        
        user.setEnabled(true);
        userRepository.update(user);
        System.out.println("Пользователь активирован!");
        
        tokenRepository.deleteByUserId(user.getId());
        System.out.println("Токен удалён");
        
        return true;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public void updateUser(User user) {
        userRepository.update(user);
    }
}