package com.project.SLX.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:application.properties")
public class SMTPConfiguration {
    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.socketFactory.port}")
    private String socketPort;

    @Value("${mail.smtp.socketFactory.class}")
    private String socketClass;

    @Value("${mail.smtp.auth}")
    private String auth;

    @Value("${mail.smtp.port}")
    private String port;

    @Value("${mail.smtp.auth.username}")
    private String username;

    @Value("${mail.smtp.auth.password}")
    private String password;

    @Value("${mail.smtp.from}")
    private String from;
}
