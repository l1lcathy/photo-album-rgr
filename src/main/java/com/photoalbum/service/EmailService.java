package com.photoalbum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String to, String token) {
        System.out.println("sendVerificationEmail");
        System.out.println("Кому: " + to);
        System.out.println("Токен: " + token);
        
        String subject = "Подтверждение регистрации в Фотоальбоме";
        String confirmationUrl = "http://localhost:8080/confirm?token=" + token;
        String message = "Для подтверждения регистрации перейдите по ссылке:\n" + confirmationUrl;
        
        sendEmail(to, subject, message);
    }
    
    public void sendNotification(String to, String message) {
        sendEmail(to, "Уведомление от Фотоальбома", message);
    }
    
    public void sendCommentNotification(String to, String photoTitle, String commentAuthor, String commentText) {
        String subject = "Новый комментарий к вашему фото";
        String message = "Пользователь " + commentAuthor + " оставил комментарий к вашему фото \"" + photoTitle + "\":\n\n" + commentText;
        sendEmail(to, subject, message);
    }
    
    public void sendFriendshipRequestNotification(String to, String fromUsername) {
        String subject = "Заявка в друзья";
        String message = "Пользователь " + fromUsername + " отправил вам заявку в друзья.\n\n" +
                         "Перейдите в раздел 'Друзья' чтобы принять или отклонить заявку.";
        sendEmail(to, subject, message);
    }
    
    public void sendCopyNotification(String to, String copiedBy, String photoTitle, String albumName) {
        String subject = "Ваше фото скопировали";
        String message = "Пользователь " + copiedBy + " скопировал ваше фото \"" + photoTitle + 
                         "\" в свой альбом \"" + albumName + "\"";
        sendEmail(to, subject, message);
    }

    private void sendEmail(String to, String subject, String text) {
        System.out.println("ОТПРАВКА EMAIL");
        System.out.println("От: " + fromEmail);
        System.out.println("Кому: " + to);
        System.out.println("Тема: " + subject);
        System.out.println("Текст: " + text);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("Email успешно отправлен!");
        } catch (Exception e) {
            System.err.println("ОШИБКА отправки: " + e.getMessage());
            e.printStackTrace();
        }
    }
}