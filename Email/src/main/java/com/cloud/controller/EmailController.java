package com.cloud.controller;

import com.cloud.model.Email;
import com.cloud.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/api")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public void sendEmail(@RequestBody Email email) throws MessagingException {
        emailService.sendEmail(email);
    }

}
