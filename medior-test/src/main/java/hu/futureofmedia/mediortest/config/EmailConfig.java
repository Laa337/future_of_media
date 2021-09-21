package hu.futureofmedia.mediortest.config;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Getter
@Setter
public class EmailConfig {

    private Boolean enabled;
    private String sender;
    private String host;
    private Integer  port;
    private String username;
    private String password;
    private Map<String, String> properties;


    @Bean
    public JavaMailSender mailSender() {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost( host );
        mailSender.setPort( port );

        if ( StringUtils.isNotBlank( username ) ) {
            mailSender.setUsername( username );
        }
        if ( StringUtils.isNotBlank( password ) ) {
            mailSender.setPassword( password );
        }

        mailSender.setJavaMailProperties( MapUtils.toProperties(properties) );
        mailSender.getJavaMailProperties().put("sender", sender);
        mailSender.getJavaMailProperties().put("enabled", enabled);
        return mailSender;
    }
}
