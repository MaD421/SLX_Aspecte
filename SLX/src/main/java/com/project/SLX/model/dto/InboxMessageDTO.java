package com.project.SLX.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InboxMessageDTO {
    private String body;
    private LocalDateTime createdAt;
    private Boolean own;
    private Long partnerId;
    private String partnerUsername;
}

