package com.cloud.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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

    public void setHost(String host) {
        this.host = host;
    }

    public void setSocketPort(String socketPort) {
        this.socketPort = socketPort;
    }

    public void setSocketClass(String socketClass) {
        this.socketClass = socketClass;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public String getSocketPort() {
        return socketPort;
    }

    public String getSocketClass() {
        return socketClass;
    }

    public String getAuth() {
        return auth;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
