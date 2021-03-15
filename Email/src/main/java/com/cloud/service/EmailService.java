package com.cloud.service;

import com.cloud.configuration.SMTPConfiguration;
import com.cloud.model.Email;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {

    private final SMTPConfiguration smtpConfiguration;

    public EmailService(SMTPConfiguration smtpConfiguration) {
        this.smtpConfiguration = smtpConfiguration;
    }

    public void sendEmail(Email email) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpConfiguration.getHost());
        props.put("mail.smtp.socketFactory.port", smtpConfiguration.getSocketPort());
        props.put("mail.smtp.socketFactory.class", smtpConfiguration.getSocketClass());
        props.put("mail.smtp.auth", smtpConfiguration.getAuth());
        props.put("mail.smtp.port", smtpConfiguration.getPort());

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpConfiguration.getUsername(),smtpConfiguration.getPassword());
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(email.getFrom()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getTo()));
        message.setSubject(email.getSubject());
        message.setContent(email.getBody(), "text/html; charset=utf-8");

        Transport.send(message);
    }

}
