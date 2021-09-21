package hu.futureofmedia.mediortest.services;

import java.util.Properties;

import javax.mail.internet.InternetAddress;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import lombok.SneakyThrows;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService( JavaMailSender mailSender ) {
        this.mailSender = mailSender;
    }

    public void sendSimpleMessage( String to, String subject, String text) {
        Properties properties = ((JavaMailSenderImpl)mailSender).getJavaMailProperties();
        if(!(Boolean)properties.get("enabled"))
            return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.getProperty("sender"));
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
