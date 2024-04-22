package com.petbuddyz.petbuddyzapp.Group;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.petbuddyz.petbuddyzapp.Chat.Chat;
import com.petbuddyz.petbuddyzapp.GroupChat.GroupChat;
import com.petbuddyz.petbuddyzapp.Message.Message;
import com.petbuddyz.petbuddyzapp.Owner.Owner;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
    * Represents a group in the application.
    */
    
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @ManyToMany
    private Set<Owner> members;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "groupChatId")
    private GroupChat groupChat;

    @ManyToOne
    @JoinColumn(name = "group_maker_id", nullable = false)
    private Owner groupMaker;
    
    
    @NotBlank
    @Size(min = 1, max = 255)
    private String groupName;

     @Temporal(TemporalType.TIMESTAMP)
    private Date groupCreationDate;
 
     @PrePersist
    protected void onCreate() {
        Date date = new Date();
        groupCreationDate = date;
    }
    
}