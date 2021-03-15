package com.project.SLX.service;

import com.project.SLX.SlxApplication;
import com.project.SLX.model.Listing;
import com.project.SLX.model.Log;
import com.project.SLX.model.enums.LogTypeEnum;
import com.project.SLX.repository.ListingRepository;
import com.project.SLX.repository.LogRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = SlxApplication.class)
public class LogServiceTest {
    @Autowired
    LogService logService;

    @Autowired
    LogRepository logRepository;

    @Autowired
    ListingRepository listingRepository;

    @Test
    public void addTest() {
        // Crate entity and save it to database
        Listing listing = new Listing();
        listing.setType("Test type");
        listing.setPrice(300f);
        listing.setTitle("Title test 4");
        listing.setDescription("Description test");
        listing.setCurrency("Test currency");
        listing.setOwner(null);
        Listing savedListing = listingRepository.save(listing);
        Log log = logService.addLog(savedListing.getListingId(), LogTypeEnum.CREATE);
        assertThat(log.getListOwn().getListingId()).isEqualTo(savedListing.getListingId());
        assertThat(log.getEvent()).isEqualTo("New listing was created!");
    }
}
