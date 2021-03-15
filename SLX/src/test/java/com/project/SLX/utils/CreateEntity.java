package com.project.SLX.utils;

import com.project.SLX.model.Comment;
import com.project.SLX.model.Listing;
import com.project.SLX.model.User;
import com.project.SLX.model.dto.CommentDTO;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class CreateEntity {
    public static int userCnt = 0;
    public static int listingCnt = 0;

    public User createTestUser() {
        int cnt = userCnt;
        userCnt += 1;
        User user = new User();
        user.setPhoneNumber("parola1234*");
        user.setAddress("Adresa de test si iar test.");
        user.setUsername("MaD42"+cnt);
        user.setFirstName("Andrei");
        user.setLastName("Marinoiu");
        user.setEmail("andrei.marinoiutest"+cnt+"@gmail.com");
        user.setPasswordHash(BCrypt.hashpw("parola1234*", BCrypt.gensalt()));
        return user;
    }

    public Listing createTestListing(User owner) {
        int cnt = listingCnt;
        listingCnt += 1;
        Listing listing = new Listing();
        listing.setOwner(owner);
        listing.setAvailable(true);
        listing.setCurrency("RON");
        listing.setTitle("Anunt de test " + cnt);
        listing.setDescription("Descriere de test pentru anunt.");
        listing.setPrice(300f);
        listing.setType("Tip test");
        return listing;
    }

    public Comment createTestComment(User user, Listing listing, String text) {
        Comment comment = new Comment();
        comment.setListing(listing);
        comment.setUser(user);
        comment.setText(text);

        return comment;
    }

    public CommentDTO createTestCommentDTO(String text) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText(text);

        return commentDTO;
    }
}
