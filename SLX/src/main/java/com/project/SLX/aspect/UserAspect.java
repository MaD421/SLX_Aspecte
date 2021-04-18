package com.project.SLX.aspect;

import com.project.SLX.model.User;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class UserAspect {
    @AfterReturning(value = "userRegistrationPointcut(user)", argNames = "joinPoint,user")
    public void logUserRegister(JoinPoint joinPoint, User user) {
         log.info("New user registration \n Username: " + user.getUsername() + "\n Email: " + user.getEmail());
    }

    @Pointcut(
            value = "execution(public * com.project.SLX.service.CustomUserDetailsService.create(com.project.SLX.model.User)) && args(user)",
            argNames = "user"
    )
    public void userRegistrationPointcut(User user) { }
}
