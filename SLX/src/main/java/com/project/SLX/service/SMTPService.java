package com.project.SLX.service;

import com.project.SLX.model.Email;
import com.project.SLX.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class SMTPService {

    private final static String emailHost = "http://localhost:8020";

    public void sendEmail(String to, String subject, String body) throws RestClientException {
        Email email = new Email();

        email.setBody(body);
        email.setSubject(subject);
        email.setTo(to);
        email.setFrom("no-reply@slx.com");

        RestTemplate restTemplate = new RestTemplate();
        String reqUrl = emailHost + "api/send";
        restTemplate.postForObject(reqUrl, email, Email.class);
    }

    public String createBookmarkEmailContent(User user) {
        StringBuilder content = new StringBuilder();

        if(user.getBookmarks().isEmpty()) {
            content.append("You have no bookmarks!");
        }

        user.getBookmarks().forEach(bookmark -> {
            content.append("Title : ").append(bookmark.getTitle()).append("<br/>");
            content.append("Description : ").append(bookmark.getDescription()).append("<br/>");
            content.append("Views : ").append(bookmark.getViews()).append("<br/>");
            content.append("Latest Update : ").append(bookmark.getUpdatedAt()).append("<br/><br/><br/>");
        });

        return content.toString();

    }
}
