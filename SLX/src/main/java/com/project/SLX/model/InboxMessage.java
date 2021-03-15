package com.project.SLX.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Table(name = "inbox_message")
@Entity(name = "inbox_message")
public class InboxMessage {
    @Lob
    private String body;

    @ManyToOne(cascade = {CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private User firstUser;

    @ManyToOne(cascade = {CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private User secondUser;

    @Id
    private String combinedIds;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        combinedIds = getCombinedIds(firstUser, secondUser);
    }

    public static String getCombinedIds(User firstUser, User secondUser) {
        String combinedIds;

        if (firstUser.getUserId() > secondUser.getUserId()) {
            combinedIds = firstUser.getUserId().toString() + '-' + secondUser.getUserId().toString();
        } else {
            combinedIds = secondUser.getUserId().toString() + '-' + firstUser.getUserId().toString();
        }

        return combinedIds;
    }
}
