package com.sam.BankingWebApplication1.Services.ServicesImpl;

import com.sam.BankingWebApplication1.Services.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendMail(String to, String subject, String message) {

        System.out.println(">>> EMAIL SERVICE HIT");
        System.out.println("Sending to: " + to);
        System.out.println("FROM EMAIL: " + from);
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(from);
            email.setTo(to);
            email.setSubject(subject);
            email.setText(message);

            mailSender.send(email);
            System.out.println(">>> EMAIL SENT SUCCESSFULLY");

        } catch (Exception e) {
            System.out.println(">>> EMAIL ERROR OCCURRED");
            throw new RuntimeException("SMTP email sending failed", e);
        }
    }
}