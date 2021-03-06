package com.project.SLX.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Slf4j
public class SMTPServiceAspect {

    private final static int MAX_RETRIES = 4;
    private final static long WAIT_MILLIS_BETWEEN_RETRIES = 2000;

    @Pointcut("execution(* com.project.SLX.service.SMTPService.sendEmail(String,..)) && args(to,..)")
    public void emailPointcut(String to) { }

    @Around(value = "emailPointcut(to)", argNames = "proceedingJoinPoint,to")
    public void emailRetry(ProceedingJoinPoint proceedingJoinPoint, String to) throws Throwable {
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                proceedingJoinPoint.proceed();
                return;
            } catch (Exception e) {
                log.info("Failed to send email to {}. Retrying {} ...", to, i + 1);
                Thread.sleep(WAIT_MILLIS_BETWEEN_RETRIES);
            }
        }
    }

}
