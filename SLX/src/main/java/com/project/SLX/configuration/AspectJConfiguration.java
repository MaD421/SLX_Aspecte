package com.project.SLX.configuration;

import com.project.SLX.aspect.*;
import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;

@Configuration
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
public class AspectJConfiguration {

    @Bean
    public ListingProfilingAspect listingProfilingAspect() {
        return Aspects.aspectOf(ListingProfilingAspect.class);
    }

    @Bean
    public ListingAspect listingAspect() {
        return Aspects.aspectOf(ListingAspect.class);
    }

    @Bean
    public SMTPServiceAspect smtpServiceAspect() {
        return Aspects.aspectOf(SMTPServiceAspect.class);
    }

    @Bean
    public LogRequestAspect logRequestAspect() {
        return Aspects.aspectOf(LogRequestAspect.class);
    }

    @Bean
    public MessageAspect messageAspect() {
        return Aspects.aspectOf(MessageAspect.class);
    }

    @Bean
    public UserAspect userAspect() {
        return Aspects.aspectOf(UserAspect.class);
    }
}
