package com.project.SLX.model.dto;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ListingUpdateDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long listingId;

    @NotBlank(message = "Title is empty!")
    private String title;

    @Lob
    @NotBlank(message = "Description is empty!")
    private String description;

    @NotBlank(message = "Type is empty!")
    private String type;

    @Column(nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean available = true;

    @NotNull(message= "Price is empty")
    @Range(min = 1, message = "Price should be minimum 1!")
    private Float price;

    @NotBlank(message = "Currency is empty!")
    private String currency;
}
