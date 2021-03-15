package com.project.SLX.service;

import com.project.SLX.configuration.ImageConfiguration;
import com.project.SLX.model.Image;
import com.project.SLX.model.Listing;
import com.project.SLX.model.User;
import com.project.SLX.model.dto.ListingUpdateDTO;
import com.project.SLX.model.exception.ListingNotFoundException;
import com.project.SLX.repository.ImageRepository;
import com.project.SLX.repository.ListingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class ListingService {
    private final List<String> acceptedFileExtensions = Arrays.asList("png", "jpg", "jpeg");
    private final ListingRepository listingRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final ImageConfiguration imageConfiguration;

    @Autowired
    public ListingService(ListingRepository listingRepository, ImageService imageService, ImageRepository imageRepository, ImageConfiguration imageConfiguration) {
        this.listingRepository = listingRepository;
        this.imageService = imageService;
        this.imageRepository = imageRepository;
        this.imageConfiguration = imageConfiguration;
    }

    public Listing getById(Long id) throws ListingNotFoundException{
        Optional<Listing> listing  = listingRepository.findByListingId(id);
        listing.orElseThrow(ListingNotFoundException::new);
        return listing.get();
    }

    public Long save(Listing listing) {
        Listing savedListing = listingRepository.save(listing);
        savedListing.getImages().forEach(imageRepository::save);

        return savedListing.getListingId();
    }

    public Page<Listing> getAll(Pageable pageable) { return listingRepository.findAll(pageable); }

    public Page<Listing> getAllCurrency(String currency, Pageable pageable) { return listingRepository.findAllByCurrency(currency, pageable); }

    public Page<Listing> getAllSearch(String search, Pageable pageable) { return listingRepository.findAllSearch(search, pageable); }

    public Page<Listing> getAllSearchCurrency(String currency, String search, Pageable pageable) { return listingRepository.findAllSearchCurrency(currency, search, pageable); }

    public Page<Listing> getAllBookmarked(Long userId, Pageable pageable) { return listingRepository.getAllBookmarked(userId, pageable); }

    public Page<Listing> getAllBookmarkedCurrency(Long userId, String currency, Pageable pageable) { return listingRepository.getAllBookmarkedCurrency(userId, currency, pageable); }

    public Page<Listing> getAllBookmarkedSearch(Long userId, String search, Pageable pageable) { return listingRepository.getAllBookmarkedSearch(userId, search, pageable); }

    public Page<Listing> getAllBookmarkedSearchCurrency(Long userId, String currency, String search, Pageable pageable) { return listingRepository.getAllBookmarkedSearchCurrency(userId, currency, search, pageable); }

    public Page<Listing> getAllByUserId(Long userId, Pageable pagination) { return listingRepository.findByUserId(userId, pagination); }

    public Page<Listing> getAllByUserIdCurrency(Long userId, String currency, Pageable pagination) { return listingRepository.findByUserIdCurrency(userId, currency, pagination); }

    public Page<Listing> getAllByUserIdSearch(Long userId, String search, Pageable pagination) { return listingRepository.findByUserIdSearch(userId, search, pagination); }

    public Page<Listing> getAllByUserIdSearchCurrency(Long userId, String currency, String search, Pageable pagination) { return listingRepository.findByUserIdSearchCurrency(userId, currency, search, pagination); }

    public void incrementViews(Long id) {
        Optional<Listing> listing  = listingRepository.findById(id);
        listing.orElseThrow(ListingNotFoundException::new);
        listingRepository.incrementViewsById(id);
    }

    public void deleteById(Long id) {
        Optional<Listing> listing  = listingRepository.findById(id);
        listing.orElseThrow(ListingNotFoundException::new);
        listingRepository.deleteById(id);
    }

    public  Page<Listing> getBookmarks(User user,  Map.Entry<String, Pageable> paginationDetails, String currency, String search) {
        return getBookmarksPaged(currency, search, paginationDetails, user);
    }

    public Page<Listing> getAll(Map.Entry<String, Pageable> paginationDetails, String currency, String search) {
        return getListingsPageAll(currency, search, paginationDetails);
    }

    public Page<Listing> getPagedUser(User user, Map.Entry<String, Pageable> paginationDetails, String currency, String search) {
        return getListingsPageUser(currency, search, paginationDetails, user);
    }

    public Long add(User user, Listing listing, MultipartFile[] images) {
        try {
            listing.setOwner(user);
        } catch (Exception e) {
            log.error(e.getMessage());

            return 0L;
        }

        try {
            int count = 0;

            if (images != null && images.length > 0) {
                listing.setImages(new ArrayList<>());
                String extension;

                for (MultipartFile image : images) {
                    if (count == imageConfiguration.getMaxNumber()) {
                        break;
                    }
                    Image newImage = new Image();
                    extension = Objects.requireNonNull(image.getOriginalFilename()).substring(image.getOriginalFilename().lastIndexOf('.') + 1);

                    if (this.acceptedFileExtensions.contains(extension) && image.getSize() / 1000000 <= imageConfiguration.getMaxSize()) {
                        newImage.setExtension(extension);
                        Byte[] byteObjects = new Byte[image.getBytes().length];

                        int i = 0;

                        for (byte b : image.getBytes()){
                            byteObjects[i++] = b;
                        }

                        newImage.setImage(byteObjects);
                        listing.addImage(newImage);
                    }

                    count++;
                }
            }

            Long listingId = this.save(listing);
            log.info("Listing with id " + listingId + " was created.");

            return listingId;
        } catch (Exception e) {
            log.error(e.getMessage());
            return 0L;
        }
    }

    // Helper methods
    private Page<Listing> getBookmarksPaged(String currency, String search, Map.Entry<String, Pageable> paginationDetails, User user) {
        Page<Listing> listingsPage;

        if (currency.equals("") || currency.toLowerCase().equals("all")) {
            listingsPage = search.equals("") ?
                    this.getAllBookmarked(user.getUserId(), paginationDetails.getValue()) :
                    this.getAllBookmarkedSearch(user.getUserId(), search, paginationDetails.getValue());
        } else {
            listingsPage = search.equals("") ?
                    this.getAllBookmarkedCurrency(user.getUserId(), currency, paginationDetails.getValue()) :
                    this.getAllBookmarkedSearchCurrency(user.getUserId(), currency, search, paginationDetails.getValue());
        }

        return listingsPage;
    }

    public boolean isOwner(User user, @RequestParam("id") Long id) {
        // If it is not owner, the method also sets all listings on the model object to show the on homepage
        Long listingOwnerId = this.getById(id).getOwner().getUserId();
        Long userId = user.getUserId();

        return listingOwnerId.equals(userId);
    }

    private Page<Listing> getListingsPageAll(String currency, String search, Map.Entry<String, Pageable> paginationDetails) {
        Page<Listing> listingsPage;

        if (currency.equals("") || currency.toLowerCase().equals("all")) {
            listingsPage = search.equals("") ?
                    this.getAll(paginationDetails.getValue()) :
                    this.getAllSearch(search, paginationDetails.getValue());
        } else {
            listingsPage = search.equals("") ?
                    this.getAllCurrency(currency, paginationDetails.getValue()) :
                    this.getAllSearchCurrency(currency, search, paginationDetails.getValue());
        }

        return listingsPage;
    }

    private Page<Listing> getListingsPageUser(String currency, String search, Map.Entry<String, Pageable> paginationDetails, User user) {
        Page<Listing> listingsPage;

        if (currency.equals("") || currency.toLowerCase().equals("all")) {
            listingsPage = search.equals("") ?
                    this.getAllByUserId(user.getUserId(), paginationDetails.getValue()) :
                    this.getAllByUserIdSearch(user.getUserId(), search, paginationDetails.getValue());
        } else {
            listingsPage = search.equals("") ?
                    this.getAllByUserIdCurrency(user.getUserId(), currency, paginationDetails.getValue()) :
                    this.getAllByUserIdSearchCurrency(user.getUserId(), currency, search, paginationDetails.getValue());
        }

        return listingsPage;
    }

    public ListingUpdateDTO getUpdateDTO(Listing listing) {
        ListingUpdateDTO listingDetails = new ListingUpdateDTO();

        listingDetails.setListingId(listing.getListingId());
        listingDetails.setAvailable(listing.isAvailable());
        listingDetails.setTitle(listing.getTitle());
        listingDetails.setDescription(listing.getDescription());
        listingDetails.setPrice(listing.getPrice());
        listingDetails.setCurrency(listing.getCurrency());
        listingDetails.setType(listing.getType());

        return listingDetails;
    }

    public Long update(Listing listing, Listing listingDetails, MultipartFile[] images) {
        listing.setAvailable(listingDetails.isAvailable());
        listing.setDescription(listingDetails.getDescription());
        listing.setTitle(listingDetails.getTitle());
        listing.setType(listingDetails.getType());
        listing.setPrice(listingDetails.getPrice());
        listing.setCurrency(listingDetails.getCurrency());

        int currentNrImages;

        if (listing.getImages() == null) {
            listing.setImages(new ArrayList<>());
            currentNrImages = 0;
        } else {
            currentNrImages = listing.getImages().size();
        }

        if (images != null && images.length > 0 && currentNrImages < imageConfiguration.getMaxNumber() && (currentNrImages + images.length <= imageConfiguration.getMaxNumber())) {
            String extension;

            for (MultipartFile image : images) {
                Image newImage = new Image();
                extension = Objects.requireNonNull(image.getOriginalFilename()).substring(image.getOriginalFilename().lastIndexOf('.') + 1);

                if (this.acceptedFileExtensions.contains(extension) && image.getSize() / 1000000 <= imageConfiguration.getMaxSize()) {
                    newImage.setExtension(extension);
                    newImage.setOwner(listing);

                    Byte[] byteObjects;

                    try {
                        byteObjects = new Byte[image.getBytes().length];
                    } catch (IOException e) {
                        continue;
                    }

                    int i = 0;

                    try {
                        for (byte b : image.getBytes()){
                            byteObjects[i++] = b;
                        }
                    } catch (IOException e) {
                        continue;
                    }

                    newImage.setImage(byteObjects);
                    listing.addImage(newImage);
                }
            }
        }

        return this.save(listing);
    }

    public boolean delete(Long id) {
        try {
            Listing currentListing = this.getById(id);

            for (Image image: currentListing.getImages()) {
                imageService.deleteById(image.getImageId());
            }

            this.deleteById(id);
            log.info("Listing with id {} was deleted!",id);
        } catch (ListingNotFoundException e) {
            return false;
        }

        return true;
    }
}
