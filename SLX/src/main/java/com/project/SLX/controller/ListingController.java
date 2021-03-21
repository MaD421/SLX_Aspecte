package com.project.SLX.controller;

import com.project.SLX.model.Comment;
import com.project.SLX.model.Listing;
import com.project.SLX.model.User;
import com.project.SLX.model.dto.ListingUpdateDTO;
import com.project.SLX.model.enums.LogTypeEnum;
import com.project.SLX.model.exception.ListingNotFoundException;
import com.project.SLX.service.CommentService;
import com.project.SLX.service.CustomUserDetailsService;
import com.project.SLX.service.ListingService;
import com.project.SLX.service.LogService;
import com.project.SLX.utils.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/")
public class ListingController {
    private final ListingService listingService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CommentService commentService;
    private final LogService logService;

    @Autowired
    public ListingController(ListingService listingService, CustomUserDetailsService customUserDetailsService, CommentService commentService, LogService logService) {
        this.listingService = listingService;
        this.commentService = commentService;
        this.customUserDetailsService = customUserDetailsService;
        this.logService = logService;
    }

    @GetMapping("/user/{userId}/listings/pg/{page}")
    public String getUsersListings(Model model, @PathVariable Long userId, @PathVariable int page, @RequestParam(value = "sort", required = false, defaultValue = "") String sort, @RequestParam(value = "search", required = false, defaultValue = "") String search, @RequestParam(value = "currency", required = false, defaultValue = "all") String currency, HttpServletResponse response) {
        // See the listings of a specific user
        User user;
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            Optional<User> tmpUser = customUserDetailsService.getUserById(userId);
            tmpUser.orElseThrow();
            user = tmpUser.get();
        } catch (Exception e) {
            model.addAttribute("error", "Invalid user id!");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            return "listing/error";
        }

        handleListingsUser(model, user, currency, sort, search, page, user.getUsername() + "'s listings", "");
        model.addAttribute("cssConfig", "listings");
        model.addAttribute("userName", user.getUsername());

        return "listing/usersListings";
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public String getAllListings(Model model, @RequestParam(value = "sort", required = false, defaultValue = "") String sort, @RequestParam(value = "search", required = false, defaultValue = "") String search, @RequestParam(value = "currency", required = false, defaultValue = "all") String currency) {
        int page = 1;
        handleListingsAll(model, currency, sort, search, page, "Home", "pg/");
        model.addAttribute("cssConfig", "home");

        return "listing/home";
    }

    @GetMapping("/pg/{page}")
    @ResponseStatus(HttpStatus.OK)
    public String getAllListingsPaged(Model model, @PathVariable int page, @RequestParam(value = "sort", required = false, defaultValue = "") String sort, @RequestParam(value = "search", required = false, defaultValue = "") String search, @RequestParam(value = "currency", required = false, defaultValue = "all") String currency) {
        handleListingsAll(model, currency, sort, search, page, "Home", "");
        model.addAttribute("cssConfig", "home");

        return "listing/home";
    }

    @GetMapping("/myListings")
    @ResponseStatus(HttpStatus.OK)
    public String getMyListings(Model model, @RequestParam(value = "sort", required = false, defaultValue = "") String sort, @RequestParam(value = "search", required = false, defaultValue = "") String search, @RequestParam(value = "currency", required = false, defaultValue = "all") String currency) {
        int page = 1;
        User user = customUserDetailsService.getCurrentUser();
        handleListingsUser(model, user, currency, sort, search, page, "My listings", "myListings/pg/");
        model.addAttribute("cssConfig", "listings");

        return "listing/myListings";
    }

    @GetMapping("/myListings/pg/{page}")
    @ResponseStatus(HttpStatus.OK)
    public String getMyListingsPaged(Model model, @PathVariable int page, @RequestParam(value = "sort", required = false, defaultValue = "") String sort, @RequestParam(value = "search", required = false, defaultValue = "") String search, @RequestParam(value = "currency", required = false, defaultValue = "all") String currency) {
        User user = customUserDetailsService.getCurrentUser();
        handleListingsUser(model, user, currency, sort, search, page, "My listings", "");
        model.addAttribute("cssConfig", "listings");

        return "listing/myListings";
    }

    @GetMapping("/viewListing/{id}")
    public String viewListing(@PathVariable Long id, Model model, HttpServletResponse response) {
        int page = 1;

        return handleViewListing(id, page, "/viewListing/" + id + "/pg/", model, response);
    }

    @GetMapping("/viewListing/{id}/pg/{page}")
    public String viewListingPaged(@PathVariable Long id, @PathVariable int page, Model model, HttpServletResponse response) {
        return handleViewListing(id, page, "", model, response);
    }

    public String handleViewListing(Long id, int page, String pagePrefix, Model model, HttpServletResponse response) {
        int commentsPerPage = 1;
        response.setStatus(HttpServletResponse.SC_OK);
        Listing listing;
        model.addAttribute("comments", new ArrayList<>());
        model.addAttribute("totalPages", 0);
        model.addAttribute("currentPage", 0);
        model.addAttribute("pagePrefix", pagePrefix);
        model.addAttribute("totalComments", 0);

        try {
            listing = listingService.getById(id);
            model.addAttribute("listing", listing);
        } catch (ListingNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            log.warn("Could not find listing with id " + id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            return "info";
        }

        Page<Comment> commentsPaged = commentService.getAllPaged(listing, page, commentsPerPage);

        if (commentsPaged == null) {
            return "listing/viewListing";
        }

        int totalCommentPages = commentsPaged.getTotalPages();

        if (totalCommentPages < page && totalCommentPages > 0) {
            page = totalCommentPages;
            commentsPaged = commentService.getAllPaged(listing, page, commentsPerPage);
        }

        model.addAttribute("comments", commentsPaged.getContent());
        model.addAttribute("totalPages", totalCommentPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("pagePrefix", pagePrefix);
        model.addAttribute("totalComments", commentsPaged.getTotalElements());

        User user = null;

        // Enable bookmark / delete features
        try {
            user = customUserDetailsService.getCurrentUser();
            model.addAttribute("userId", listing.getOwner().getUserId());

            if (customUserDetailsService.isBookmarked(user, listing)) {
                model.addAttribute("isBookmarked", true);
            } else {
                model.addAttribute("isBookmarked", false);
            }

            if (customUserDetailsService.isOwner(user, listing)) {
                model.addAttribute("isOwner", true);
            } else {
                model.addAttribute("isOwner", false);
            }
        } catch (Exception e) {
            log.info("User not authenticated. Bookmark and delete features are disabled.");
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("pageTitle", "View listing");
        model.addAttribute("cssConfig", "viewListing");

        return "listing/viewListing";
    }

    @GetMapping("/addListing")
    @ResponseStatus(HttpStatus.OK)
    public String addListing(Model model) {
        model.addAttribute("listing", new Listing());
        model.addAttribute("pageTitle", "Add listing");

        return "listing/addListing";
    }

    @PostMapping("/addListing/process")
    public String addListingProcess(@Valid @ModelAttribute("listing") Listing listing, BindingResult bindingResult, @RequestParam(value = "imagesListing", required = false) MultipartFile[] images, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CREATED);

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("listing", listing);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "listing/addListing";
        }

        User user = null;

        try {
            user = customUserDetailsService.getCurrentUser();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        Long listingId = listingService.add(user, listing, images);
        logService.addLog(listingId, LogTypeEnum.CREATE);

        if (listingId.equals(0L)) {
            return "listing/addListing";
        }

        model.addAttribute("listingId", listingId);

        return "listing/addSuccess";
    }

    @GetMapping("/editListing")
    public String editListing(Model model, @RequestParam("id") Long id, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        Listing listing;

        try {
            listing = listingService.getById(id);
            listingService.incrementViews(id);
            model.addAttribute("listing", listing);
        } catch (ListingNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            log.warn("Could not find listing with id " + id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            return "info";
        }

        ListingUpdateDTO listingDetails = listingService.getUpdateDTO(listing);
        model.addAttribute("listingDetails", listingDetails);
        model.addAttribute("pageTitle", "Edit listing");

        return "listing/editListing";
    }

    @PutMapping("editListing/process")
    public String editListingProcess(@Valid @ModelAttribute("listingDetails") Listing listingDetails, BindingResult bindingResult, @RequestParam(value = "imagesListing", required = false) MultipartFile[] images, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("listingDetails", listingDetails);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return "listing/editListing";
        }

        Listing listing;

        try {
            listing = listingService.getById(listingDetails.getListingId());
            listingService.incrementViews(listingDetails.getListingId());
        } catch (ListingNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            log.warn("Could not find listing with id " + listingDetails.getListingId());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            return "info";
        }

        Long listingId = listingService.update(listing, listingDetails, images);
        logService.addLog(listing.getListingId(), LogTypeEnum.UPDATE);

        return ("redirect:/editListing?id=" + listingId);
    }

    @DeleteMapping("deleteListing/process")
    public String deleteListingProcess(Model model, @RequestParam("id") Long id, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);

        if (!listingService.delete(id)) {
            model.addAttribute("error", "This listing can't be deleted!");
            log.warn("Could not find listing with id " + id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            return "info";
        }

        log.info("Listing with id {} was deleted!", id);
        model.addAttribute("success", "Listing deleted!");

        return "info";
    }

    private void handleListingsUser(Model model, User user, String currency, String sort, String search, int page, String pageTitle, String pagePrefix) {
        if (page < 1) {
            page = 1;
        }

        Pagination pagination = new Pagination();
        Map.Entry<String, Pageable> paginationDetails = pagination.getPaginationDetails(currency, sort, search, page);
        Page<Listing> listingsPage = listingService.getPagedUser(user, paginationDetails, currency, search);
        int totalPages = listingsPage.getTotalPages();

        if (totalPages < page && totalPages > 0) {
            page = totalPages;
            paginationDetails = pagination.getPaginationDetails(currency, sort, search, page);
            listingsPage = listingService.getPagedUser(user, paginationDetails, currency, search);
        }

        setListingsPaged(model, listingsPage, pagination, paginationDetails, pageTitle, page, pagePrefix, sort, search, currency);
    }

    private void handleListingsAll(Model model, String currency, String sort, String search, int page, String pageTitle, String pagePrefix) {
        if (page < 1) {
            page = 1;
        }

        Pagination pagination = new Pagination();
        Map.Entry<String, Pageable> paginationDetails = pagination.getPaginationDetails(currency, sort, search, page);
        Page<Listing> listingsPage = listingService.getAll(paginationDetails, currency, search);
        int totalPages = listingsPage.getTotalPages();

        if (totalPages < page && totalPages > 0) {
            page = totalPages;
            paginationDetails = pagination.getPaginationDetails(currency, sort, search, page);
            listingsPage = listingService.getAll(paginationDetails, currency, search);
        }

        setListingsPaged(model, listingsPage, pagination, paginationDetails, pageTitle, page, pagePrefix, sort, search, currency);
    }

    private void setListingsPaged(Model model, Page<Listing> listingsPage, Pagination pagination, Map.Entry<String, Pageable> paginationDetails, String pageTitle, int page, String pagePrefix, String sort, String search, String currency) {
        int totalPages = listingsPage.getTotalPages();
        List<Listing> listings = listingsPage.getContent();
        model.addAttribute("listingList", listings);
        model.addAttribute("pageTitle", pageTitle);
        pagination.setPagination(model, totalPages, page, pagePrefix, paginationDetails.getKey(), sort, search, currency);
    }

    public CustomUserDetailsService getCustomUserDetailsService() {
        return customUserDetailsService;
    }
}
