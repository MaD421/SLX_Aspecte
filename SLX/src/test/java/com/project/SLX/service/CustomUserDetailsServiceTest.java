package com.project.SLX.service;

import com.project.SLX.SlxApplication;
import com.project.SLX.model.User;
import com.project.SLX.model.dto.UserUpdateDTO;
import com.project.SLX.model.exception.UserNotAuthenticatedException;
import com.project.SLX.model.exception.UsernameNotFoundException;
import com.project.SLX.repository.UserRepository;
import com.project.SLX.utils.CreateEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = SlxApplication.class)
public class CustomUserDetailsServiceTest {
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CreateEntity createEntity;

    public static User user;

    /**
     * We need only one user for the tests
     * So we create them here to avoid duplicated code in every test method
     */
    @Before
    public void setup() {
        // Create the listing owner
        User user = createEntity.createTestUser();
        CustomUserDetailsServiceTest.user = userRepository.saveAndFlush(user);
    }

    @Test
    public void loadByUsername() {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void loadByUsernameNotFound() {
        assertThatThrownBy(() -> {
            customUserDetailsService.loadUserByUsername(user.getUsername() + "loadByUsernameNotFound");
        }).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    public void getCurrent() {
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(user.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User userDb = customUserDetailsService.getCurrentUser();
        assertThat(userDb.getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    public void getCurrentUnauthenticated() {
        assertThatThrownBy(() -> {
            customUserDetailsService.getCurrentUser();
        }).isInstanceOf(UserNotAuthenticatedException.class);
    }

    @Test
    public void getUpdateDTO() {
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(user.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserUpdateDTO userUpdateDTO = customUserDetailsService.getUpdateDTO();
        assertThat(userUpdateDTO.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void getUpdateDTOUnauthenticated() {
        UserUpdateDTO userUpdateDTO = customUserDetailsService.getUpdateDTO();
        assertThat(userUpdateDTO.getEmail()).isEqualTo(null);
    }

    @Test
    public void update() {
        String newEmail = "test@test.com";
        // Set the authentication context
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(user.getUsername())
                ,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setEmail(newEmail);
        customUserDetailsService.update(userUpdateDTO);
        User currentUser = customUserDetailsService.getCurrentUser();
        assertThat(currentUser.getEmail()).isEqualTo(newEmail);
    }

    @Test
    public void updateUnauthenticated() {
        String newEmail = "test@test.com";
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setEmail(newEmail);
        Boolean result = customUserDetailsService.update(userUpdateDTO);
        assertThat(result).isEqualTo(false);
    }
}
