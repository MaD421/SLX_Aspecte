package com.project.SLX.controller;

import com.project.SLX.model.Listing;
import com.project.SLX.model.User;
import com.project.SLX.service.CustomUserDetailsService;
import com.project.SLX.service.ListingService;
import com.project.SLX.utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bookmark")
public class BookmarkController {
    private final CustomUserDetailsService customUserDetailsService;
    private final ListingService listingService;

    @Autowired
    public BookmarkController(CustomUserDetailsService customUserDetailsService, ListingService listingService) {
        this.customUserDetailsService = customUserDetailsService;
        this.listingService = listingService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/my")
    public String viewBookmarks(Model model, @RequestParam(value = "sort", required = false, defaultValue = "") String sort, @RequestParam(value = "search", required = false, defaultValue = "") String search, @RequestParam(value = "currency", required = false, defaultValue = "all") String currency) {
        int page = 1;
        handleBookmarks(model, search, currency, sort, page, "my/pg/");

        return "bookmark/my";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/my/pg/{page}")
    public String viewBookmarksPaged(Model model, @PathVariable int page, @RequestParam(value = "sort", required = false, defaultValue = "") String sort, @RequestParam(value = "search", required = false, defaultValue = "") String search, @RequestParam(value = "currency", required = false, defaultValue = "all") String currency) {
        handleBookmarks(model, search, currency, sort, page, "");

        return "bookmark/my";
    }

    @PutMapping("/add/process")
    public String addBookmarkProcess(@RequestParam("listingId") Long listingId, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);

        if (!customUserDetailsService.addBookmark(listingId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            model.addAttribute("error", "Listing not found!");

            return "info";
        }

        model.addAttribute("listingId", listingId);

        return  "bookmark/addSuccess";
    }

    @DeleteMapping("/delete/process")
    public String deleteBookmarkProcess(@RequestParam("listingId") Long listingId, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_ACCEPTED);

        if (!customUserDetailsService.deleteBookmark(listingId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            model.addAttribute("error", "Listing not found!");

            return "info";
        }

        model.addAttribute("listingId", listingId);

        return  "bookmark/deleteSuccess";
    }

    private void handleBookmarks(Model model, String search, String currency, String sort, int page, String pagePrefix) {
        if (page < 1) {
            page = 1;
        }

        User user = customUserDetailsService.getCurrentUser();
        Pagination pagination = new Pagination();

        Map.Entry<String, Pageable> paginationDetails = pagination.getPaginationDetails(currency, sort, search, page);
        Page<Listing> listingsPage = listingService.getBookmarks(user, paginationDetails, currency, search);
        int totalPages = listingsPage.getTotalPages();

        if (totalPages < page && totalPages > 0) {
            page = totalPages;
            paginationDetails = pagination.getPaginationDetails(currency, sort, search, page);
            listingsPage = listingService.getBookmarks(user, paginationDetails, currency, search);
        }

        List<Listing> listings = listingsPage.getContent();

        model.addAttribute("bookmarkList",listings);
        model.addAttribute("pageTitle", "Bookmarks");
        pagination.setPagination(model, totalPages, page, pagePrefix, paginationDetails.getKey(), sort, search, currency);
        model.addAttribute("cssConfig", "listings");

    }
}
