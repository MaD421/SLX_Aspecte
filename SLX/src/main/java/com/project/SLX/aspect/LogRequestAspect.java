package com.project.SLX.aspect;

import com.project.SLX.service.WebRequestService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Aspect
public class LogRequestAspect {
    @Autowired
    private WebRequestService webRequestService;

    @Before("@annotation(LogRequest)")
    public void logRequestCall(JoinPoint joinPoint) throws IOException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = webRequestService.getClientIpFromRequest(request);
        log.info("New signup request from : {}", ip);
    }

    /*
    @AfterReturning(pointcut="execution(public * *(..))", returning="result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) throws IOException{
        log.info("logAfterReturning() is running!");
        log.info("Method Name : " + joinPoint.getSignature().getName());
        log.info("******");
        if(joinPoint.getSignature().getName() != "getCSS" && result!= null){
            log.info(result.toString());
        }
    }
     */
}
