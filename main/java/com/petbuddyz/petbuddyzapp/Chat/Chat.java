package com.petbuddyz.petbuddyzapp.Chat;

import java.util.Date;
import java.util.Set;

import com.petbuddyz.petbuddyzapp.Group.Group;
import com.petbuddyz.petbuddyzapp.Message.Message;
import com.petbuddyz.petbuddyzapp.Owner.Owner;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a chat between two owners or within a group.
 */
@Entity
@Table(name = "chat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @ManyToOne
    @JoinColumn(name = "senderId")
    private Owner sender;

    @ManyToOne
    @JoinColumn(name = "receiverId")
    private Owner receiver;


    @Temporal(TemporalType.TIMESTAMP)
    private Date chatCreationDate;

    @OneToMany(mappedBy = "chat")
    private Set<Message> messages;

    @PrePersist
    protected void onCreate() {
        Date date = new Date();
        chatCreationDate = date;
    }

}