package com.project.SLX.controller;

import com.project.SLX.SlxApplication;
import com.project.SLX.model.User;
import com.project.SLX.repository.UserRepository;
import com.project.SLX.service.CustomUserDetailsService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = SlxApplication.class)
public class UserControllerTests {
	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Autowired
	CreateEntity createEntity;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CustomUserDetailsService customUserDetailsService;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}

	@Test
	public void registerUserTest() throws Exception {
		mockMvc.perform(post("/user/register/process")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("address", "Adresa de test si iar test.")
				.param("username", "MaD442")
				.param("firstName", "Andrei")
				.param("lastName", "Marinoiu")
				.param("password", "parola1234*")
				.param("email", "andrei.marinoiutest@gmail.com")
				.param("phoneNumber", "0749413902"))
				.andExpect(status().isCreated())
				.andExpect(view().name("user/registerSuccess"));
	}

	@Test
	public void editUserTest() throws Exception {
		// Create the user
		User user = createEntity.createTestUser();
		user = userRepository.saveAndFlush(user);
		// Set the authentication context
		Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetailsService.loadUserByUsername(user.getUsername())
				,"parola1234*", AuthorityUtils.createAuthorityList("ROLE_USER"));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		mockMvc.perform(put("/user/edit/process")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("address", "Adresa de test si iar test Edit.")
				.param("firstName", "Andrei")
				.param("lastName", "Marinoiu")
				.param("password", "parola1234*")
				.param("email", "andrei.marinoiu2@gmail.com")
				.param("phoneNumber", "0749413902"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("success", is("Profile updated!")));
	}
}
