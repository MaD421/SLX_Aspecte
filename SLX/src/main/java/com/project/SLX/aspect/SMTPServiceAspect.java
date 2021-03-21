package com.project.SLX.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Slf4j
public class SMTPServiceAspect {

    private final static int MAX_RETRIES = 4;
    private final static long WAIT_MILLIS_BETWEEN_RETRIES = 2000;

    @Pointcut("execution(* com.project.SLX.service.SMTPService.sendEmail(..))")
    public void emailPointcut() { }

    @Pointcut("execution(com.project.SLX.model.Email.new(..))")
    public void emailConstructorPointcut() { }

    @Before("emailConstructorPointcut()")
    public void emailConstructor(JoinPoint joinPoint) {
        String to = (String) joinPoint.getArgs()[0];
        String from = (String) joinPoint.getArgs()[1];
        log.info("--Creating email : to {} from {}", to, from);
    }

    @Around("emailPointcut()")
    public void emailRetry(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String to = (String) proceedingJoinPoint.getArgs()[0];
        Exception ex = null;
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                proceedingJoinPoint.proceed();
            } catch (Exception e) {
                log.info("Failed to send email to {}. Retrying {} ...", to, i + 1);
                ex = e;
                Thread.sleep(WAIT_MILLIS_BETWEEN_RETRIES);
            }
        }
    }


}
