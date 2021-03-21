package com.project.SLX.service;

import com.project.SLX.model.Email;
import com.project.SLX.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class SMTPService {

    private final static String EMAIL_HOST = "http://localhost:8020";

    private final static String DEFAULT_FROM = "no-reply@slx.com";

    public void sendEmail(String to, String subject, String body) throws RestClientException {
        Email email = new Email(to, DEFAULT_FROM);

        email.setBody(body);
        email.setSubject(subject);

        RestTemplate restTemplate = new RestTemplate();
        String reqUrl = EMAIL_HOST + "api/send";
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
