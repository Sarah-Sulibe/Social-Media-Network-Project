package com.petbuddyz.petbuddyzapp.Notification;

import java.util.Date;

import com.petbuddyz.petbuddyzapp.Owner.Owner;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a notification in the application.
 * Each notification has an owner, content, creation date, type, status, and status change date.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
public class Notification {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long notificationId;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    private Owner owner;

    @NotBlank
    @Size(min = 1, max = 255)
    private String content;

  


    @Temporal(TemporalType.TIMESTAMP)
    private Date notificationCreationDate;



    @NotNull
    private NotificationType type;

    public enum NotificationType {
        POST_LIKE,
        CHAT_MESSAGE,
        GROUP_JOIN
        ,COMMENT_LIKE
    }

    private Status status;

    public enum Status {
        READ, UNREAD
    }

    @Temporal(TemporalType.TIMESTAMP)
    private Date statusChangeDate;

    @PrePersist
    protected void onCreate() {
        Date date = new Date();
        notificationCreationDate = date;
        statusChangeDate = date;
    }
}