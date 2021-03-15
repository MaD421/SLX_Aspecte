package com.project.SLX.model;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity(name = "user")
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String passwordHash;

    private String firstName;

    private String lastName;

    private String address;

    private String phoneNumber;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "roleId"))
    private Set<Role> roles;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Listing> listings;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_bookmark", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "listingId"))
    @Fetch(value = FetchMode.SUBSELECT)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<Listing> bookmarks;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user", orphanRemoval = true)
    @Fetch(value = FetchMode.JOIN)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<Comment> comments;

    @Column(nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean enableNotifications = true;

    public User() {
    }

    public User(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.passwordHash = user.getPasswordHash();
        this.firstName = user.getFirstName();
        this.lastName =user.getLastName();
        this.address = user.getAddress();
        this.phoneNumber = user.getPhoneNumber();
        this.roles = user.getRoles();
        this.listings = user.getListings();
        this.setBookmarks(user.getBookmarks());
        this.enableNotifications = user.isEnableNotifications();
    }

    public void setBookmarks(Set<Listing> bookmarks) {
        this.bookmarks = new HashSet<>(bookmarks);
    }

    public Set<Listing> getBookmarks() {
        return new HashSet<>(bookmarks);
    }

    public void addBookmark(Listing listing) {
        bookmarks.add(listing);
    }

    public void removeBookmark(Listing listing) {
        bookmarks.remove(listing);
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
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return userId != null && userId.equals(user.getUserId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
