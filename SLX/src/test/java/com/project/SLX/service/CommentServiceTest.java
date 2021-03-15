package com.project.SLX.service;

import com.project.SLX.SlxApplication;
import com.project.SLX.model.Comment;
import com.project.SLX.model.Listing;
import com.project.SLX.model.User;
import com.project.SLX.model.dto.CommentDTO;
import com.project.SLX.repository.CommentRepository;
import com.project.SLX.repository.ListingRepository;
import com.project.SLX.repository.UserRepository;
import com.project.SLX.utils.CreateEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = SlxApplication.class)
public class CommentServiceTest {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ListingRepository listingRepository;

    @Autowired
    CreateEntity createEntity;

    public static User owner;
    public static User userWhoComments;
    public static Listing listing;

    /**
     * We need only one listing owner and one owner for the tests
     * So we create them here to avoid duplicated code in every test method
     */
    @Before
    public void setup() {
        // Create the listing owner
        User owner = createEntity.createTestUser();
        CommentServiceTest.owner = userRepository.saveAndFlush(owner);
        // Create the user who comments
        User userWhoComments = createEntity.createTestUser();
        CommentServiceTest.userWhoComments = userRepository.saveAndFlush(userWhoComments);
        // Create the listing
        Listing listing = createEntity.createTestListing(owner);
        CommentServiceTest.listing = listingRepository.saveAndFlush(listing);
    }

    @Test
    public void findById() {
        // Crate entity and save it to database
        Comment comment = createEntity.createTestComment(userWhoComments, listing, "test");
        Comment savedComment = commentRepository.saveAndFlush(comment);
        // Use service to retrieve entity
        Comment foundComment = commentService.findById(savedComment.getId());
        // Test result
        assertThat(foundComment.getText()).isEqualTo(savedComment.getText());
        assertThat(foundComment.getUser().getUserId()).isEqualTo(savedComment.getUser().getUserId());
        assertThat(foundComment.getListing().getListingId()).isEqualTo(savedComment.getListing().getListingId());
    }

    @Test
    public void getAllPaged() {
        Comment comment1 = createEntity.createTestComment(userWhoComments, listing, "test1");
        Comment comment2 = createEntity.createTestComment(userWhoComments, listing, "test2");
        commentRepository.saveAndFlush(comment1);
        commentRepository.saveAndFlush(comment2);

        Page<Comment> firstPage = commentService.getAllPaged(listing, 1, 1);
        List<Comment> comments = firstPage.getContent();
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.hasNext()).isEqualTo(true);
        assertThat(firstPage.hasPrevious()).isEqualTo(false);
        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.get(0).getText()).isEqualTo(comment2.getText());
    }

    @Test
    public void add() {
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(userWhoComments.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CommentDTO commentDTO = createEntity.createTestCommentDTO("test");
        Boolean result = commentService.add(listing.getListingId(), commentDTO);
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void addOwner() {
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(owner.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CommentDTO commentDTO = createEntity.createTestCommentDTO("test");
        Boolean result = commentService.add(listing.getListingId(), commentDTO);
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void addUnauthenticated() {
        CommentDTO commentDTO = createEntity.createTestCommentDTO("test");
        Boolean result = commentService.add(listing.getListingId(), commentDTO);
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void addNotFoundListing() {
        CommentDTO commentDTO = createEntity.createTestCommentDTO("test");
        Boolean result = commentService.add(listing.getListingId() + 1, commentDTO);
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void update() {
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(userWhoComments.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Comment comment1 = createEntity.createTestComment(userWhoComments, listing, "test1");
        CommentDTO commentDTO = createEntity.createTestCommentDTO("test2");
        comment1 = commentRepository.saveAndFlush(comment1);
        commentService.update(comment1.getId(), commentDTO);
        comment1 = commentService.findById(comment1.getId());
        assertThat(comment1.getText()).isEqualTo(commentDTO.getText());
    }

    @Test
    public void updateUnauthenticated() {
        Comment comment1 = createEntity.createTestComment(userWhoComments, listing, "test1");
        CommentDTO commentDTO = createEntity.createTestCommentDTO("test2");
        comment1 = commentRepository.saveAndFlush(comment1);
        Boolean result = commentService.update(comment1.getId(), commentDTO);
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void updateUnauthorized() {
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(owner.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Comment comment1 = createEntity.createTestComment(userWhoComments, listing, "test1");
        CommentDTO commentDTO = createEntity.createTestCommentDTO("test2");
        comment1 = commentRepository.saveAndFlush(comment1);
        Boolean result = commentService.update(comment1.getId(), commentDTO);
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void updateNotFoundListing() {
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(userWhoComments.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Comment comment1 = createEntity.createTestComment(userWhoComments, listing, "test1");
        CommentDTO commentDTO = createEntity.createTestCommentDTO("test2");
        comment1 = commentRepository.saveAndFlush(comment1);
        Boolean result = commentService.update(comment1.getId() + 1, commentDTO);
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void delete() {
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(userWhoComments.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Comment comment1 = createEntity.createTestComment(userWhoComments, listing, "test1");
        comment1 = commentRepository.saveAndFlush(comment1);
        Boolean result = commentService.delete(comment1.getId());
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void deleteUnauthenticated() {
        Comment comment1 = createEntity.createTestComment(userWhoComments, listing, "test1");
        comment1 = commentRepository.saveAndFlush(comment1);
        Boolean result = commentService.delete(comment1.getId());
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void deleteUnauthorized() {
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(owner.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Comment comment1 = createEntity.createTestComment(userWhoComments, listing, "test1");
        comment1 = commentRepository.saveAndFlush(comment1);
        Boolean result = commentService.delete(comment1.getId());
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void deleteNotFoundListing() {
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(userWhoComments.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Comment comment1 = createEntity.createTestComment(userWhoComments, listing, "test1");
        comment1 = commentRepository.saveAndFlush(comment1);
        Boolean result = commentService.delete(comment1.getId() + 1);
        assertThat(result).isEqualTo(false);
    }
}
