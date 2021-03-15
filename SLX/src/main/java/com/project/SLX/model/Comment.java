package com.project.SLX.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Table(name = "comment")
@Entity(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @NotBlank(message = "Comment is empty!")
    @Column(nullable = false)
    private String text;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "listingId")
    private Listing listing;

    @PrePersist
    public void prePersist() {
        setCreatedAt(LocalDateTime.now());
        setUpdatedAt(LocalDateTime.now());
    }

    @PreUpdate
    public void preUpdate() {
        setUpdatedAt(LocalDateTime.now());
    }

    @PreRemove
    public void preRemove() {
        this.user.removeComment(this);
        this.listing.removeComment(this);
    }
}
