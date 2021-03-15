package com.project.SLX.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private String body;

    private LocalDateTime createdAt;

    private Boolean own;
}
