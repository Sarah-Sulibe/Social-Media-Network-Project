package com.petbuddyz.petbuddyzapp.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.petbuddyz.petbuddyzapp.Chat.Chat;
import com.petbuddyz.petbuddyzapp.Comment.Comment;
import com.petbuddyz.petbuddyzapp.Community.Community;
import com.petbuddyz.petbuddyzapp.Like.Like;
import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Pet.Pet;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "post")
public class Post {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long postId;
    
    @NotBlank
    @Size(max = 255)
    private String postContent;
    
    private String postMedia;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    @JsonBackReference
    private Owner owner;

    @JsonProperty("ownerId")
    private Long getOwnerId() {
        return owner.getOwnerId();
    }

    @ManyToOne
    @JoinColumn(name = "petId")
    @JsonBackReference
    private Pet pet;

    @JsonProperty("petId")
    private Long getPetId() {
        return pet.getPetId();
    }

    @Temporal(TemporalType.TIMESTAMP)
    private Date postCreationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date postUpdateDate;

    @ManyToOne
    @JoinColumn(name = "communityId")
    @JsonBackReference
    private Community community;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();


    // TODO : configure the many to many relationship
    @ManyToMany
    private List<Like> postLikes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        postCreationDate = new Date();
        postUpdateDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        postUpdateDate = new Date();
    }

    
}