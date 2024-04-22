package com.petbuddyz.petbuddyzapp.GroupChat;





import java.util.Date;
import java.util.Set;

import com.petbuddyz.petbuddyzapp.Message.Message;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class GroupChat  {

  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupChatId;

    @OneToMany(mappedBy = "groupChat")
    private Set<Message> messages;
    


    @Temporal(TemporalType.TIMESTAMP)
    private Date chatCreationDate;



    @PrePersist
    protected void onCreate() {
        Date date = new Date();
        chatCreationDate = date;
    }

}