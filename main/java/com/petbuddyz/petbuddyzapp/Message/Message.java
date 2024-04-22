package com.petbuddyz.petbuddyzapp.Message;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.petbuddyz.petbuddyzapp.Chat.Chat;
import com.petbuddyz.petbuddyzapp.GroupChat.GroupChat;
import com.petbuddyz.petbuddyzapp.Post.Post;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a message in the application.
 * Each message has a unique identifier, content, media, creation date, status, and status change date.
 * Messages can be associated with a chat and a post.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message")
@Component
@Getter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "chatId")
    private Chat chat;


    @ManyToOne
    @JoinColumn(name = "groupChatId")
    private GroupChat groupChat;



    private String content;

    private String media;

    @OneToOne
    @JoinColumn(name = "postId")
    private Post post;

    @Temporal(TemporalType.TIMESTAMP)
    private Date messageCreationDate;

 

    private Status status;

    public enum Status {
        READ, UNREAD
    }

    @Temporal(TemporalType.TIMESTAMP)
    private Date statusChangeDate;

    @PrePersist
    protected void onCreate() {
        Date date = new Date();
        messageCreationDate = date;
        statusChangeDate = date; // Set statusChangeDate to messageCreationDate
    }
    
}