package com.project.SLX.utils;

import com.project.SLX.model.Listing;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import java.util.Map;

public class Pagination {
    public void setPagination(Model model, int totalPages, int currentPage, String pagePrefix, String pageSuffix, String sort, String search, String currency) {
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pagePrefix", pagePrefix);
        model.addAttribute("pageSuffix", pageSuffix);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("currency", currency);
    }

    public SortCriteria computeSortCriteria(String sort) {
        String[] sortSplit = sort.split("_");
        String sortProperty = sortSplit[0];
        String sortDirection = sortSplit[1];

        if (!sortDirection.equals("ASC") && !sortDirection.equals("DESC")) {
            sortDirection = "ASC";
        }

        try {
            Listing.class.getDeclaredField(sortProperty);
        } catch (NoSuchFieldException e) {
            sortProperty = "updatedAt";
        }

        return new SortCriteria(sortDirection.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, sortProperty);
    }

    public String computePaginationSuffix(String currency, String sort, String search) {
        String paginationSuffix = "";

        if (sort != null && !sort.equals("")) {
            paginationSuffix += "?sort=" + sort;
        }

        if (search != null && !search.equals("")) {
            if (paginationSuffix.equals("")) {
                paginationSuffix += "?search=" + search;
            } else {
                paginationSuffix += "&search=" + search;
            }
        }

        if (currency != null && !currency.toLowerCase().equals("all") && !currency.toLowerCase().equals("")) {
            if (paginationSuffix.equals("")) {
                paginationSuffix += "?currency=" + currency;
            } else {
                paginationSuffix += "&currency=" + currency;
            }
        }

        return paginationSuffix;
    }

    public Map.Entry<String, Pageable> getPaginationDetails(String currency, String sort, String search, int page) {
        Pageable pagination;

        if (!sort.equals("")) {
            SortCriteria sortCriteria = computeSortCriteria(sort);
            sort = sortCriteria.getSortProperty() + "_" + (sortCriteria.getSortDirection() == Sort.Direction.ASC ? "ASC" : "DESC");
           pagination = PageRequest.of(page - 1, 3, Sort.by(sortCriteria.getSortDirection(), sortCriteria.getSortProperty()));
        } else {
           pagination = PageRequest.of(page - 1, 3, Sort.by(Sort.Direction.DESC, "updatedAt"));
        }

        String pageSuffix = computePaginationSuffix(currency, sort, search);

        return Map.entry(pageSuffix, pagination);
    }

    // Helper classes
    private static class SortCriteria {
        private Sort.Direction sortDirection;
        private String sortProperty;

        public SortCriteria(Sort.Direction sortDirection, String sortProperty) {
            this.sortDirection = sortDirection;
            this.sortProperty = sortProperty;
        }

        public Sort.Direction getSortDirection() {
            return sortDirection;
        }

        public String getSortProperty() {
            return sortProperty;
        }
    }
}
