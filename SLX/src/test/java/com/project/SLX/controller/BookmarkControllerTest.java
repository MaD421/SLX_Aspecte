package com.project.SLX.controller;

import com.project.SLX.SlxApplication;
import com.project.SLX.model.Listing;
import com.project.SLX.model.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = SlxApplication.class)
public class BookmarkControllerTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    ListingService listingService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ListingRepository listingRepository;

    @Autowired
    CreateEntity createEntity;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    public void addBookmarkProcessTest() throws Exception {
        // Create the user
        User user = createEntity.createTestUser();
        user = userRepository.saveAndFlush(user);
        // Create the listing
        Listing listing = createEntity.createTestListing(user);
        listing = listingRepository.saveAndFlush(listing);
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(user.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Send request
        mockMvc.perform(put("/bookmark/add/process")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("listingId", listing.getListingId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("bookmark/addSuccess"));
    }

    @Test
    public void deleteBookmarkProcessTest() throws Exception {
        // Create the user
        User user = createEntity.createTestUser();
        user = userRepository.saveAndFlush(user);
        // Create the listing
        Listing listing = createEntity.createTestListing(user);
        listing = listingRepository.saveAndFlush(listing);
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(user.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Set bookmark
        mockMvc.perform(put("/bookmark/add/process")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("listingId", listing.getListingId().toString()));
        // Send request
        mockMvc.perform(delete("/bookmark/delete/process")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("listingId", listing.getListingId().toString()))
                .andExpect(status().isAccepted())
                .andExpect(view().name("bookmark/deleteSuccess"));
    }
}
