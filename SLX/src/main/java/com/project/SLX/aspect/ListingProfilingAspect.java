package com.project.SLX.aspect;

import com.project.SLX.controller.ListingController;
import com.project.SLX.service.ListingService;
import org.aspectj.lang.JoinPoint;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class ListingProfilingAspect {

    private final ListingService listingService;

    public ListingProfilingAspect(ListingService listingService) {
        this.listingService = listingService;
    }

    private Object profileCall(ProceedingJoinPoint joinPoint, String message) throws Throwable {
        String methodSignature = joinPoint.getSignature().getName();
        StopWatch stopWatch = new StopWatch(getClass().getSimpleName());

        try {
            stopWatch.start(joinPoint.getSignature().getName());
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            log.info(methodSignature + " duration in seconds: " + stopWatch.getTotalTimeSeconds() + message);
        }
    }

    @Around(value = "allListings(sort,search,currency)", argNames = "joinPoint,sort,search,currency")
    public Object profileAllListings(ProceedingJoinPoint joinPoint, String sort, String search, String currency) throws Throwable {
        return profileCall(
                joinPoint, " sort: " + sort + ", search: " + search + ", currency: " + currency
        );
    }

    @Pointcut(
            value = "execution(public * com.project.SLX.controller.ListingController.getAllListings(.., String, String, String, ..)) && args(*, sort, search, currency, ..) || " +
                    "execution(public * com.project.SLX.controller.ListingController.getAllListingsPaged(.., String, String, String, ..)) && args(.., sort, search, currency)",
            argNames = "sort,search,currency"
    )
    public void allListings(String sort, String search, String currency) {
    }

    @Around(value = "userListings(userId)", argNames = "joinPoint,userId")
    public Object profileUserListings(ProceedingJoinPoint joinPoint, Long userId) throws Throwable {
        return profileCall(joinPoint, " userId: " + userId);
    }

    @Pointcut(
            value = "execution(public * com.project.SLX.controller.ListingController.getUsersListings(.., Long, ..)) && args(*, userId, ..)",
            argNames = "userId"
    )
    public void userListings(Long userId) { }

    @Around(value = "myListings(controller)", argNames = "joinPoint,controller")
    public Object profileMyListings(ProceedingJoinPoint joinPoint, ListingController controller) throws Throwable {
        Long userId = controller.getCustomUserDetailsService().getCurrentUser().getUserId();

        return profileCall(joinPoint, " current userId: " + userId);
    }

    @Pointcut(
            value = "execution(public * com.project.SLX.controller.ListingController.getMyListings(..)) && target(controller) || " +
                    "execution(public * com.project.SLX.controller.ListingController.getMyListingsPaged(..)) && target(controller)",
            argNames = "controller"
    )
    public void myListings(ListingController controller) { }

    @Pointcut(
            value = "execution(* com.project.SLX.controller.ListingController.viewListing(Long, ..)) && args(id, ..) || " +
                    "execution(* com.project.SLX.controller.ListingController.viewListingPaged(Long, ..)) && args(id, ..)",
            argNames = "id"
    )
    public void addViewsCall(Long id) {}

    @After(value = "addViewsCall(id)", argNames = "joinPoint,id")
    public void profileAddViews(JoinPoint joinPoint, Long id) {
        try {
            listingService.incrementViews(id);
        } catch (Exception ignored) { }
    }
}
