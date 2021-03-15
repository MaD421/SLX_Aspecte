package com.project.SLX.repository;

import com.project.SLX.model.Comment;
import com.project.SLX.model.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByListingOrderByCreatedAtDesc(Listing listing, Pageable pageable);
}
