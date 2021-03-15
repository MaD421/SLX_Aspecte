package com.project.SLX.controller;

import com.project.SLX.SlxApplication;
import com.project.SLX.model.Comment;
import com.project.SLX.model.Listing;
import com.project.SLX.model.User;
import com.project.SLX.repository.CommentRepository;
import com.project.SLX.repository.ListingRepository;
import com.project.SLX.repository.UserRepository;
import com.project.SLX.service.CustomUserDetailsService;
import com.project.SLX.service.ListingService;
import com.project.SLX.utils.CreateEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = SlxApplication.class)
public class CommentControllerTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    ListingService listingService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ListingRepository listingRepository;

    @Autowired
    CreateEntity createEntity;

    private MockMvc mockMvc;
    public static User owner;
    public static Listing listing;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        // We need only one listing owner and one owner for the tests
        // So we create them here to avoid duplicated code in every test method
        // Create the listing owner
        User owner = createEntity.createTestUser();
        CommentControllerTest.owner = userRepository.saveAndFlush(owner);
        // Create the listing
        Listing listing = createEntity.createTestListing(owner);
        CommentControllerTest.listing = listingRepository.saveAndFlush(listing);
    }

    /**
     * This should return "success" because users can comment on listings
     * that are not theirs
     *
     * @throws Exception Throws exception when there is an error while performing the mockMvc
     */
    @Test
    public void addTest() throws Exception {
        // Create user that will comment
        User userWhoComments = createEntity.createTestUser();
        userWhoComments = userRepository.saveAndFlush(userWhoComments);
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(userWhoComments.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Create comment JSON
        String commentJSON = "{\"text\":\"test comment\"}";
        // Send request
        mockMvc.perform(post("/viewListing/{id}/comment", listing.getListingId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentJSON))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status", is("success")));
    }

    /**
     * This should return error as listing owners cannot comment on
     * their own listings
     *
     * @throws Exception Throws exception when there is an error while performing the mockMvc
     */
    @Test
    public void addOwnerTest() throws Exception {
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(owner.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Create comment JSON
        String commentJSON = "{\"text\":\"test comment\"}";
        // Send request
        mockMvc.perform(post("/viewListing/{id}/comment", listing.getListingId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentJSON))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status", is("error")));
    }

    /**
     * This should return success because an user can edit
     * it's own comments
     *
     * @throws Exception Throws exception when there is an error while performing the mockMvc
     */
    @Test
    public void updateTest() throws Exception {
        // Create the user who comments
        User userWhoComments = createEntity.createTestUser();
        userWhoComments = userRepository.saveAndFlush(userWhoComments);
        // Create the comment
        Comment comment = createEntity.createTestComment(userWhoComments, listing, "test");
        comment = commentRepository.saveAndFlush(comment);
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(userWhoComments.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Updated comment JSON
        String commentJSON = "{\"text\":\"test2\"}";
        // Send request
        mockMvc.perform(put("/viewListing/{listingId}/comment/{id}", listing.getListingId(), comment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentJSON))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status", is("success")));
    }

    /**
     * This should return error because an user cannot edit another
     * user's comment
     *
     * @throws Exception Throws exception when there is an error while performing the mockMvc
     */
    @Test
    public void updateOtherTest() throws Exception {
        // Create the user who creates the comment
        User userWhoComments = createEntity.createTestUser();
        userWhoComments = userRepository.saveAndFlush(userWhoComments);
        // Create the comment
        Comment comment = createEntity.createTestComment(userWhoComments, listing, "test");
        comment = commentRepository.saveAndFlush(comment);
        // Create the user who edits the comment
        User userWhoEdits = createEntity.createTestUser();
        userWhoEdits = userRepository.saveAndFlush(userWhoEdits);
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(userWhoEdits.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Updated comment JSON
        String commentJSON = "{\"text\":\"test2\"}";
        // Send request
        mockMvc.perform(put("/viewListing/{listingId}/comment/{id}", listing.getListingId(), comment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentJSON))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status", is("error")));
    }

    /**
     * This should return success because an user can delete
     * it's own comments
     *
     * @throws Exception Throws exception when there is an error while performing the mockMvc
     */
    @Test
    public void deleteTest() throws Exception {
        // Create the user who comments
        User userWhoComments = createEntity.createTestUser();
        userWhoComments = userRepository.saveAndFlush(userWhoComments);
        // Create the comment
        Comment comment = createEntity.createTestComment(userWhoComments, listing, "test");
        comment = commentRepository.saveAndFlush(comment);
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(userWhoComments.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Send request
        mockMvc.perform(delete("/viewListing/{listingId}/comment/{id}", listing.getListingId(), comment.getId()))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status", is("success")));
    }

    /**
     * This should return error because an user cannot delete another
     * user's comment
     *
     * @throws Exception Throws exception when there is an error while performing the mockMvc
     */
    @Test
    public void deleteOtherTest() throws Exception {
        // Create the user who creates the comment
        User userWhoComments = createEntity.createTestUser();
        userWhoComments = userRepository.saveAndFlush(userWhoComments);
        // Create the comment
        Comment comment = createEntity.createTestComment(userWhoComments, listing, "test");
        comment = commentRepository.saveAndFlush(comment);
        // Create the user who deletes the comment
        User userWhoEdits = createEntity.createTestUser();
        userWhoEdits = userRepository.saveAndFlush(userWhoEdits);
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(userWhoEdits.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Send request
        mockMvc.perform(delete("/viewListing/{listingId}/comment/{id}", listing.getListingId(), comment.getId()))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status", is("error")));
    }
}
