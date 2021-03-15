package com.project.SLX.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:application.properties")
public class ImageConfiguration {
    @Value("${img.listing.maxSize}")
    private int maxSize;

    @Value("${img.listing.maxNumber}")
    private int maxNumber;
}
