package com.photoalbum.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendVerification(String email, String token) {
        System.out.println("VERIFY EMAIL TO " + email + " TOKEN: " + token);
    }

    public void sendNotification(String email, String text) {
        System.out.println("NOTIFICATION TO " + email + " TEXT: " + text);
    }
}