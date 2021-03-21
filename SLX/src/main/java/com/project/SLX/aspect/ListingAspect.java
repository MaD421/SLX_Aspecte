package com.project.SLX.aspect;

import com.project.SLX.model.Listing;
import com.project.SLX.model.User;
import com.project.SLX.service.CustomUserDetailsService;
import com.project.SLX.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Aspect
public class ListingAspect {
    @Autowired
    private ListingService listingService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private String checkOwner(ProceedingJoinPoint joinPoint, Long listingId) throws Throwable {
        User user;

        try {
            user = customUserDetailsService.getCurrentUser();
        } catch (Exception e) {
            log.warn("Unauthenticated request");
            return "redirect:/";
        }

        if (!listingService.isOwner(user, listingId)) {
            log.warn("Unauthorized request by user with id " + user.getUserId());
            return "redirect:/";
        }

        return (String) joinPoint.proceed();
    }

    @Around(value = "checkOwnerWithIdPointcut(id)", argNames = "joinPoint,id")
    public String checkOwnerWithId(ProceedingJoinPoint joinPoint, Long id) throws Throwable {
        return checkOwner(joinPoint, id);
    }

    @Pointcut(
            value = "execution(public String com.project.SLX.controller.ListingController.editListing(.., Long, ..)) && args(*, id, ..) || " +
                    "execution(public String com.project.SLX.controller.ListingController.deleteListingProcess(.., Long, ..)) && args(*, id, ..)",
            argNames = "id"
    )
    public void checkOwnerWithIdPointcut(Long id) {
    }

    @Around(value = "checkOwnerWithModelPointcut(listingDetails)", argNames = "joinPoint,listingDetails")
    public String checkOwnerWithModel(ProceedingJoinPoint joinPoint, Listing listingDetails) throws Throwable {
        return checkOwner(joinPoint, listingDetails.getListingId());
    }

    @Pointcut(
            value = "execution(public String com.project.SLX.controller.ListingController.editListingProcess(com.project.SLX.model.Listing, ..)) && args(listingDetails, ..)",
            argNames = "listingDetails"
    )
    public void checkOwnerWithModelPointcut(Listing listingDetails) {
    }
}
