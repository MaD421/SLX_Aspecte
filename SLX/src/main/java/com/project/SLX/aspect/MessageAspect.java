package com.project.SLX.aspect;

import com.project.SLX.model.exception.MessageException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MessageAspect {
    @Around(value = "beforeSendMessagePointcut(body)", argNames = "joinPoint,body")
    public Object beforeSendMessage(ProceedingJoinPoint joinPoint, String body) throws Throwable {
        if (body == null || body.isEmpty()) {
            throw new MessageException("Invalid body");
        }

        return joinPoint.proceed();
    }

    @Pointcut(
            value = "execution(public * com.project.SLX.controller.MessageController.sendMessage(String, ..)) && args(body, ..)",
            argNames = "body"
    )
    public void beforeSendMessagePointcut(String body) { }
}
