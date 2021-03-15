package com.project.SLX.service;

import com.project.SLX.SlxApplication;
import com.project.SLX.model.Listing;
import com.project.SLX.model.exception.ListingNotFoundException;
import com.project.SLX.repository.ListingRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = SlxApplication.class)
public class ListingServiceTest {
    @Autowired
    ListingRepository listingRepository;

    @Autowired
    ListingService listingService;

    @Test
    public void getById_Test(){
        // Crate entity and save it to database
        Listing listing = new Listing();
        listing.setType("Test type");
        listing.setPrice(300f);
        listing.setTitle("Title test 4");
        listing.setDescription("Description test");
        listing.setCurrency("Test currency");
        listing.setOwner(null);
        Listing savedListing = listingRepository.save(listing);
        // Use service to save entity
        Listing getListing = listingService.getById(savedListing.getListingId());
        // Test result
        assertThat(getListing.getTitle()).isEqualTo(listing.getTitle());
        assertThat(getListing.getPrice()).isEqualTo(listing.getPrice());
    }

    @Test
    public void save_Test(){
        // Crate entity
        Listing listing = new Listing();
        listing.setType("Test type2");
        listing.setPrice(300f);
        listing.setTitle("Title test 5");
        listing.setDescription("Description test2");
        listing.setCurrency("Test currency2");
        listing.setOwner(null);
        // Test result
        assertThat(listingService.save(listing)).isInstanceOf(Long.class);
    }

    @Test
    public void add_Test(){
        // Crate entity
        Listing listing = new Listing();
        listing.setType("Test type2");
        listing.setPrice(300f);
        listing.setTitle("Title test 5");
        listing.setDescription("Description test2");
        listing.setCurrency("Test currency2");
        listing.setOwner(null);
        // Test result
        assertThat(listingService.add(null,listing,null)).isInstanceOf(Long.class);
    }

    @Test
    public void delete_Test(){
        // Crate entity and save it to database
        Listing listing = new Listing();
        listing.setType("Test type");
        listing.setPrice(300f);
        listing.setTitle("Title test 4");
        listing.setDescription("Description test");
        listing.setCurrency("Test currency");
        listing.setOwner(null);
        Listing savedListing = listingRepository.save(listing);
        // Use service to save entity
        Listing getListing = listingService.getById(savedListing.getListingId());
        Long id = getListing.getListingId();
        listingService.delete(id);
        // Test result
        assertThatThrownBy(() -> {
            listingService.getById(id);
        }).isInstanceOf(ListingNotFoundException.class);
    }
}
