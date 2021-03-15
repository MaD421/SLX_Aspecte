package com.project.SLX.controller;

import com.project.SLX.aspect.LogRequest;
import com.project.SLX.model.User;
import com.project.SLX.model.dto.UserCreateDTO;
import com.project.SLX.model.dto.UserUpdateDTO;
import com.project.SLX.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UserController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestParam (name = "error", required = false) String error, Model model, HttpServletResponse response) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        model.addAttribute("pageTitle", "Login");
        return "user/login";
    }

    @GetMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public String register(Model model){
        model.addAttribute("newUser",new UserCreateDTO());
        model.addAttribute("pageTitle", "Register");
        return "user/register";
    }

    @PostMapping("/register/process")
    @ResponseStatus(HttpStatus.CREATED)
    @LogRequest
    public String registerProcess(@Valid @ModelAttribute("newUser") UserCreateDTO newUser, BindingResult bindingResult, Model model, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("newUser",newUser);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "user/register";
        }

        User user = new User();
        user.setEmail(newUser.getEmail());
        user.setUsername(newUser.getUsername());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setPasswordHash(newUser.getPassword());
        user.setAddress(newUser.getAddress());
        user.setPhoneNumber(newUser.getPhoneNumber());

        try {
            Long userId = customUserDetailsService.create(user);
            log.info("User with id {} was created", userId);
        } catch (DataIntegrityViolationException e) {
            String b = e.getMessage();
            model.addAttribute("error", "Username already taken!");
            model.addAttribute("newUser", newUser);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return "user/register";
        }

        return "user/registerSuccess";
    }

    @GetMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public String editProfile(Model model) {
        UserUpdateDTO userDetails = customUserDetailsService.getUpdateDTO();
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("pageTitle", "Edit profile");
        return "user/editUser";
    }

    @PutMapping("/edit/process")
    @ResponseStatus(HttpStatus.OK)
    public String editProfileProcess(@Valid @ModelAttribute("userDetails") UserUpdateDTO userDetails, BindingResult bindingResult, Model model, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("userDetails", userDetails);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "user/editUser";
        }

        if (!customUserDetailsService.update(userDetails)) {
            model.addAttribute("error","User error!");
            return "info";
        }

        model.addAttribute("success","Profile updated!");
        model.addAttribute("userDetails", userDetails);

        return "user/editUser";
    }
}
