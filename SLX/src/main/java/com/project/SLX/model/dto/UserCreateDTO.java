package com.project.SLX.model.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserCreateDTO {
    @NotBlank(message = "Email is empty!")
    @Email(message = "Email is not valid!")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Username is empty!")
    @Size(min = 5,max = 25, message = "Username must be between 5 and 25 characters long!")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Password is empty!")
    @Size(min = 8,max = 2000,message = "Password must have at least 8 characters!")
    private String password;

    @NotBlank(message = "First name is empty!")
    @Size(min = 5,max = 25,message = "First name must be between 5 and 25 characters long!")
    private String firstName;

    @NotBlank(message = "Last name is empty!")
    @Size(min = 5,max = 25,message = "Last name must be between 5 and 25 characters long!")
    private String lastName;

    @NotBlank(message = "Address is empty!")
    @Size(min = 10,max = 255,message = "Address name must be between 10 and 255 characters long!")
    private String address;

    @NotBlank(message = "Phone number is empty!")
    @Size(min = 6,max = 10,message = "Phone number must be between 6 and 10 characters long!")
    @Pattern(regexp = "[0-9]+", message = "Phone number is invalid!")
    private String phoneNumber;
}
