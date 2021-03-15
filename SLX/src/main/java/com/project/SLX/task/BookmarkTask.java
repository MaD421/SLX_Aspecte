package com.project.SLX.task;

import com.project.SLX.configuration.BookmarkConfiguration;
import com.project.SLX.model.User;
import com.project.SLX.service.CustomUserDetailsService;
import com.project.SLX.service.SMTPService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
@EnableScheduling
public class BookmarkTask {
    private final BookmarkConfiguration bookmarkConfiguration;
    private final SMTPService smtpService;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public BookmarkTask(SMTPService smtpService, BookmarkConfiguration bookmarkConfiguration, CustomUserDetailsService customUserDetailsService) {
        this.smtpService = smtpService;
        this.bookmarkConfiguration = bookmarkConfiguration;
        this.customUserDetailsService = customUserDetailsService;
    }

    // Executes every bookmark.task.notification.frequency amount of days
    @Scheduled(initialDelayString = "1", fixedDelayString = "#{${bookmark.task.notification.frequency} * 60000 * 60 * 24}")
    private void sendNotifications() {
        if(bookmarkConfiguration.isTaskEnabled()) {
            log.info("Send Notifications : Task started at {}", Calendar.getInstance().getTime());

            Iterable<User> users = customUserDetailsService.getAll();

            // Set current date
            String pattern = "dd.MM.yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String currentDate = simpleDateFormat.format(new Date());

            users.forEach(user -> {
                if(user.isEnableNotifications()) {
                    String content;
                    content = smtpService.createBookmarkEmailContent(user);

                    try {
                        smtpService.sendEmail(user.getEmail(),"SLX : Latest Updates for " + currentDate, content);
                    } catch (Exception e) {
                        log.warn("Error when sending email to {} : {}", user.getEmail(), e.getMessage());
                    }
                } else {
                    log.info("Skipping user with id : {}", user.getUserId());
                }
            });

            log.info("Send Notifications : Task finished at {}", Calendar.getInstance().getTime());
        }
    }
}
