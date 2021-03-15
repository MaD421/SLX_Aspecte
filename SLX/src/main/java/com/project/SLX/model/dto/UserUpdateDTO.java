package com.project.SLX.model.dto;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserUpdateDTO {
    @NotBlank(message = "Email is empty!")
    @Email(message = "Email is not valid!")
    private String email;

    @NotBlank(message = "First name is empty!")
    @Size(min = 5,max = 25,message = "First name must be between 5 and 25 characters long!")
    private String firstName;

    @NotBlank(message = "Last name is empty!")
    @Size(min = 5,max = 25,message = "Last name must be between 5 and 25 characters long!")
    private String lastName;

    @NotBlank(message = "Address is empty!")
    @Size(min = 10,max = 255,message = "Address name must be between 5 and 255 characters long!")
    private String address;

    @NotBlank(message = "Phone number is empty!")
    @Size(min = 6,max = 10,message = "Phone number must be between 6 and 10 characters long!")
    @Pattern(regexp = "[0-9]+", message = "Phone number is invalid!")
    private String phoneNumber;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean enableNotifications = true;
}
