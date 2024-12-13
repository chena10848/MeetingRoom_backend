package com.example.demo.Service;

import com.example.demo.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConsumer {

    @Autowired
    private JavaMailSender mailSender;

    @KafkaListener(topics = "${spring.kafka.topic.email}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEmail(EmailMessage emailMessage) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailMessage.getEmail());
            message.setSubject(emailMessage.getSubject());
            message.setText(emailMessage.getText());
            message.setFrom("s981015a@gmail.com");

            mailSender.send(message);
            System.out.println("Email sent successfully to: " + emailMessage.getEmail());
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}
