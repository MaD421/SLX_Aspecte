package com.project.SLX.aspect;

import com.project.SLX.model.Comment;
import com.project.SLX.model.User;
import com.project.SLX.service.CommentService;
import com.project.SLX.service.CustomUserDetailsService;
import com.project.SLX.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CommentAspect {
    private final ListingService listingService;

    private final CustomUserDetailsService customUserDetailsService;

    public CommentAspect(ListingService listingService, CustomUserDetailsService customUserDetailsService) {
        this.listingService = listingService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Around(value = "checkOwnerAddPointcut(listingId)", argNames = "joinPoint,listingId")
    public boolean checkOwnerAddAdvice(ProceedingJoinPoint joinPoint, Long listingId) throws Throwable {
        User user;

        try {
            user = customUserDetailsService.getCurrentUser();
        } catch (Exception e) {
            log.warn("Unauthenticated comment request");
            return false;
        }

        if (listingService.isOwner(user, listingId)) {
            log.warn("Unauthorized comment request by user with id " + user.getUserId());
            return false;
        }

        try {
            listingService.getById(listingId);
        } catch (Exception e) {
            log.warn("Comment by user " + user.getUserId() + " on not found image with id " + listingId);
            return false;
        }

        return (boolean) joinPoint.proceed();
    }

    @Pointcut(
            value = "execution(public boolean com.project.SLX.service.CommentService.add(Long, ..)) && args(listingId, ..)",
            argNames = "listingId"
    )
    public void checkOwnerAddPointcut(Long listingId) {
    }

    @Around(value = "validateActionPointcut(id, commentService)", argNames = "joinPoint,id,commentService")
    public boolean validateActionAdvice(ProceedingJoinPoint joinPoint, Long id, CommentService commentService) throws Throwable {
        User user;
        Comment comment;

        try {
            user = customUserDetailsService.getCurrentUser();
        } catch (Exception e) {
            log.warn("Unauthenticated request");
            return false;
        }

        try {
            comment = commentService.findById(id);
        } catch (Exception e) {
            log.warn("Comment with id " + id + " not found");
            return false;
        }

        if (!comment.getUser().equals(user)) {
            log.warn("Unauthorized request by user " + user.getUserId());
            return false;
        }

        return (boolean) joinPoint.proceed();
    }

    @Pointcut(
            value = "execution(public boolean com.project.SLX.service.CommentService.update(Long, ..)) && args(id, ..)  && target(commentService) || " +
                    "execution(public boolean com.project.SLX.service.CommentService.delete(Long, ..)) && args(id, ..)  && target(commentService)",
            argNames = "id,commentService"
    )
    public void validateActionPointcut(Long id, CommentService commentService) {
    }
}
