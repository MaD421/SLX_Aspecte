package com.project.SLX.model.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;

@Data
public class CommentDTO {
    @Lob
    @NotBlank(message = "Comment is empty!")
    @Column(nullable = false)
    private String text;
}
