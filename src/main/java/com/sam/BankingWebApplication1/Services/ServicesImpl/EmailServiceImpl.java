////package com.sam.BankingWebApplication1.Services.ServicesImpl;
////
////
////import com.sam.BankingWebApplication1.Services.EmailService;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.mail.SimpleMailMessage;
////import org.springframework.mail.javamail.JavaMailSender;
////import org.springframework.stereotype.Service;
////
////@Service
////public class EmailServiceImpl implements EmailService {
////
////    @Autowired
////    private JavaMailSender mailSender;
////
////    @Override
////    public void sendMail(String to, String subject, String message) {
////        SimpleMailMessage email = new SimpleMailMessage();
////        email.setTo(to);
////        email.setSubject(subject);
////        email.setText(message);
////        mailSender.send(email);
////    }
////}
//package com.sam.BankingWebApplication1.Services.ServicesImpl;
//
//import com.sam.BankingWebApplication1.Services.EmailService;
//import com.sendgrid.*;
//import com.sendgrid.helpers.mail.Mail;
//import com.sendgrid.helpers.mail.objects.Content;
//import com.sendgrid.helpers.mail.objects.Email;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Service
//public class EmailServiceImpl implements EmailService {
//
//    @Value("${sendgrid.api.key}")
//    private String sendGridApiKey;
//
//    @Autowired
//    private JavaMailSender javaMailSender;
//
//    @Override
//    public void sendMail(String to, String subject, String message) {
//        Email from = new Email("smartbankofficial@gmail.com");   // must match SendGrid verified sender
//        Email recipient = new Email(to);
//
//        Content content = new Content("text/plain", message);
//        Mail mail = new Mail(from, subject, recipient, content);
//
//        SendGrid sg = new SendGrid(sendGridApiKey);
//        Request request = new Request();
//
//        try {
//            request.setMethod(Method.POST);
//            request.setEndpoint("mail/send");
//            request.setBody(mail.build());
//            Response response = sg.api(request);
//
//            System.out.println("Email Status Code: " + response.getStatusCode());
//            System.out.println("Email Response Body: " + response.getBody());
//        } catch (IOException ex) {
//            System.out.println("SendGrid Error: " + ex.getMessage());
//        }
//    }
//}


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

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(from);
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }
}
