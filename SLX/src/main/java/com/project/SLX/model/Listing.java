package com.project.SLX.model;

import lombok.AccessLevel;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Table(name = "listing")
@Entity(name = "listing")
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long listingId;

    @NotBlank(message = "Title is empty!")
    private String title;

    @Lob
    @NotBlank(message = "Description is empty!")
    private String description;

    @NotBlank(message = "Type is empty!")
    private String type;

    @NotNull(message= "Price is empty")
    @Range(min = 1, message = "Price should be minimum 1!")
    private Float price;

    @NotBlank(message = "Currency is empty!")
    private String currency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User owner;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "bookmarks", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<User> users;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "listing", orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.JOIN)
    @Setter(AccessLevel.NONE)
    private Set<Comment> comments;

    private int views;

    @Column(nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean available = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.MERGE}, mappedBy = "owner", orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Image> images;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "listOwn", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Log> listingLogs;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void addImage(Image image) {
        images.add(image);
    }

    public void removeImage(Image image) {
        images.remove(image);
    }

    @PostPersist
    public void postPersist() {
        if (images != null && images.size() > 0) {
            images.forEach(image -> {
                image.setOwner(this);
            });
        }
    }

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
        for (User user: this.getUsers()) {
            user.removeBookmark(this);
        }
    }

    public Set<User> getUsers() {
        return new HashSet<>(users);
    }

    public void setUsers(Set<User> users) {
        this.users = new HashSet<>(users);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Listing)) return false;
        Listing listing = (Listing) o;
        return listingId != null && listingId.equals(listing.getListingId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}