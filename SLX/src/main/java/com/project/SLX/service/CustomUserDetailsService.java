package com.project.SLX.service;

import com.project.SLX.model.CustomUserDetails;
import com.project.SLX.model.Listing;
import com.project.SLX.model.User;
import com.project.SLX.model.dto.UserUpdateDTO;
import com.project.SLX.model.exception.ListingNotFoundException;
import com.project.SLX.model.exception.UserNotAuthenticatedException;
import com.project.SLX.model.exception.UsernameNotFoundException;
import com.project.SLX.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ListingService listingService;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, ListingService listingService) {
        this.userRepository = userRepository;
        this.listingService = listingService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        user.orElseThrow(UsernameNotFoundException::new);
        return user.map(CustomUserDetails::new).get();
    }

    public Long create(User user) {
        // Generate the hash for the password and replace the plain text password with the hash
        String hash = BCrypt.hashpw(user.getPasswordHash(), BCrypt.gensalt());
        user.setPasswordHash(hash);

        return userRepository.saveAndFlush(user).getUserId();
    }

    public Long save(User user) {
        return userRepository.saveAndFlush(user).getUserId();
    }

    public Iterable<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) { return userRepository.findById(id); }

    public User getCurrentUser() {
        Object principal;

        try {
            principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (NullPointerException error) {
            throw new UserNotAuthenticatedException();
        }

        if (principal instanceof CustomUserDetails) {
            Optional<User> user = userRepository.findById(((CustomUserDetails) principal).getUserId());
            user.orElseThrow(UsernameNotFoundException::new);
            return user.get();
        } else {
            throw new UserNotAuthenticatedException();
        }
    }

    public UserUpdateDTO getUpdateDTO() {
        UserUpdateDTO userDetails = new UserUpdateDTO();

        try {
            User user = this.getCurrentUser();
            userDetails.setAddress(user.getAddress());
            userDetails.setEmail(user.getEmail());
            userDetails.setFirstName(user.getFirstName());
            userDetails.setLastName(user.getLastName());
            userDetails.setPhoneNumber(user.getPhoneNumber());
            userDetails.setEnableNotifications(user.isEnableNotifications());
        } catch (Exception e) {
            log.error(e.getMessage());

            return new UserUpdateDTO();
        }

        return userDetails;
    }

    public boolean update(UserUpdateDTO userDetails) {
        User user;

        try {
            user = this.getCurrentUser();
            user.setAddress(userDetails.getAddress());
            user.setEmail(userDetails.getEmail());
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setPhoneNumber(userDetails.getPhoneNumber());
            user.setEnableNotifications(userDetails.isEnableNotifications());
            userRepository.saveAndFlush(user);
        } catch (Exception e) {
            log.error(e.getMessage());

            return false;
        }

        return true;
    }

    public boolean addBookmark(Long listingId) {
        Listing listing;

        try {
            listing = listingService.getById(listingId);
        } catch (ListingNotFoundException e) {
            log.info(e.getMessage());

            return false;
        }

        User user = this.getCurrentUser();

        // If the listing is already bookmarked, simply return success
        if (user.getBookmarks().contains(listing)) {
            return true;
        }

        user.addBookmark(listing);
        this.save(user);

        return true;
    }

    public boolean deleteBookmark(Long listingId) {
        Listing listing;

        try {
            listing = listingService.getById(listingId);
        } catch (ListingNotFoundException e) {
            log.info(e.getMessage());

            return false;
        }

        User user = this.getCurrentUser();
        user.removeBookmark(listing);
        this.save(user);

        return true;
    }

    public boolean isBookmarked(User user, Listing listing) {
        return user.getBookmarks().contains(listing);
    }

    public boolean isOwner(User user, Listing listing) {
        return listing.getOwner().equals(user);
    }
}
