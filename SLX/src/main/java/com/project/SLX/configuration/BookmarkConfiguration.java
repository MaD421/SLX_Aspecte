package com.project.SLX.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:application.properties")
public class BookmarkConfiguration {
    @Value("${bookmark.task.notification.enabled}")
    private boolean TaskEnabled;

    @Value("${bookmark.task.notification.frequency}")
    private String notificationFrequency;
}
