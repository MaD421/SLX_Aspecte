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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = SlxApplication.class)
public class ListingControllerTests {
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
    public void viewListingTest() throws Exception {
        // Create the owner
        User owner = createEntity.createTestUser();
        owner = userRepository.save(owner);
        // Create the listing
        int listingCnt = CreateEntity.listingCnt;
        Listing listing = createEntity.createTestListing(owner);
        listing = listingRepository.save(listing);
        // Send request
        mockMvc.perform(get("/viewListing/{id}",listing.getListingId()))
                .andExpect(view().name("listing/viewListing"))
                .andExpect(model().attribute("listing", hasProperty("listingId", is(listing.getListingId()))))
                .andExpect(model().attribute("listing", hasProperty("title", is("Anunt de test " + listingCnt))))
                .andExpect(model().attribute("listing", hasProperty("price", is(300f))))
                .andExpect(model().attribute("listing", hasProperty("currency", is("RON"))))
                .andExpect(model().attribute("listing", hasProperty("type", is("Tip test"))))
                .andExpect(model().attribute("listing", hasProperty("description", is("Descriere de test pentru anunt."))));
    }

    @Test
    public void getUsersListingsTest() throws Exception{
        // Create the owner
        User owner = createEntity.createTestUser();
        owner = userRepository.save(owner);
        // Create the listing
        int listingCnt = CreateEntity.listingCnt;
        Listing listing = createEntity.createTestListing(owner);
        listing = listingRepository.save(listing);
        // Send request
        mockMvc.perform(get("/user/{userId}/listings/pg/{page}", owner.getUserId(), 1))
                .andExpect(view().name("listing/usersListings"))
                .andExpect(model().attribute("listingList", hasSize(1)));
    }


    @Test
    public void addListingTest() throws Exception {
        // Create the user
        User user = createEntity.createTestUser();
        user = userRepository.saveAndFlush(user);
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(user.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Send request
        mockMvc.perform(post("/addListing/process")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("currency", "RON")
                .param("title", "Anunt de test")
                .param("description", "Descriere de test pentru anunt.")
                .param("price", "300")
                .param("type", "Tip test"))
                .andExpect(status().isCreated())
                .andExpect(view().name("listing/addSuccess"));
    }

    @Test
    public void editListingTest() throws Exception {
        // Create the owner
        User owner = createEntity.createTestUser();
        owner = userRepository.saveAndFlush(owner);
        // Create the listing
        Listing listing = createEntity.createTestListing(owner);
        listing = listingRepository.saveAndFlush(listing);
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(owner.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Send request
        mockMvc.perform(put("/editListing/process")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("listingId", listing.getListingId().toString())
                .param("title", "Anunt de test edit")
                .param("description", "Descriere de test pentru anunt edit.")
                .param("price", "400")
                .param("type", "Tip test edit")
                .param("currency", "Euro"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/editListing?id=" + listing.getListingId().toString()));
    }

    @Test
    public void deleteListingProcessTest() throws Exception {
        // Create the owner
        User owner = createEntity.createTestUser();
        owner = userRepository.saveAndFlush(owner);
        // Create the listing
        Listing listing = createEntity.createTestListing(owner);
        listing = listingRepository.saveAndFlush(listing);
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(owner.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Send request
        mockMvc.perform(delete("/deleteListing/process")
                .param("id", listing.getListingId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("info"));
        
    }
}
