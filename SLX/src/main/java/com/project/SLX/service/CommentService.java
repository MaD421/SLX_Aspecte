package com.project.SLX.service;

import com.project.SLX.model.Comment;
import com.project.SLX.model.Listing;
import com.project.SLX.model.User;
import com.project.SLX.model.dto.CommentDTO;
import com.project.SLX.model.exception.ListingNotFoundException;
import com.project.SLX.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final ListingService listingService;

    @Autowired
    public CommentService(CommentRepository commentRepository, CustomUserDetailsService customUserDetailsService, ListingService listingService) {
        this.commentRepository = commentRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.listingService = listingService;
    }

    public Page<Comment> getAllPaged(Listing listing, int page, int commentsPerPage) {
        if (page < 1) {
            page = 1;
        }

        Pageable pagination = PageRequest.of(page - 1, commentsPerPage);

        return commentRepository.findAllByListingOrderByCreatedAtDesc(listing, pagination);
    }

    public Comment findById(Long id) {
        Optional<Comment> comment  = commentRepository.findById(id);
        comment.orElseThrow(ListingNotFoundException::new);

        return comment.get();
    }

    public boolean add(Long listingId, CommentDTO commentDTO) {
        User user = null;
        Listing listing = null;

        try {
            user = customUserDetailsService.getCurrentUser();
        } catch (Exception e) {
            log.warn("Unauthenticated request");
            return false;
        }

        if (listingService.isOwner(user, listingId)) {
            log.warn("Unauthorized request by user " + user.getUserId());
            return false;
        }

        try {
            listing = listingService.getById(listingId);
        } catch (Exception e) {
            log.warn("Comment by user " + user.getUserId() + " on not found image with id " + listingId);
            return false;
        }

        try {
            Comment comment = new Comment();
            comment.setText(commentDTO.getText());
            comment.setListing(listing);
            comment.setUser(user);
            user.addComment(comment);
            listing.addComment(comment);
            commentRepository.save(comment);
        } catch (Exception e) {
            log.warn("Fail to create comment");
            return false;
        }

        return true;
    }

    public boolean update(Long id, CommentDTO commentDTO) {
        User user = null;
        Comment comment = null;

        try {
            user = customUserDetailsService.getCurrentUser();
        } catch (Exception e) {
            log.warn("Unauthenticated request");
            return false;
        }

        try {
            comment = this.findById(id);
        } catch (Exception e) {
            log.warn("Comment with id " + id + " not found");
            return false;
        }

        if (!comment.getUser().equals(user)) {
            log.warn("Unauthorized request by user " + user.getUserId());
            return false;
        }

        try {
            comment.setText(commentDTO.getText());
            commentRepository.save(comment);
        } catch (Exception e) {
            log.warn("Failed to update comment " + comment.getId());
            return false;
        }

        return true;
    }

    public boolean delete(Long id) {
        User user = null;
        Comment comment = null;

        try {
            user = customUserDetailsService.getCurrentUser();
        } catch (Exception e) {
            log.warn("Unauthenticated request");
            return false;
        }

        try {
            comment = this.findById(id);
        } catch (Exception e) {
            log.warn("Comment with id " + id + " not found");
            return false;
        }

        if (!comment.getUser().equals(user)) {
            log.warn("Unauthorized request by user " + user.getUserId());
            return false;
        }

        try {
            commentRepository.delete(comment);
        } catch (Exception e) {
            log.warn("Failed to delete comment " + comment.getId());
            return false;
        }

        return true;
    }
}