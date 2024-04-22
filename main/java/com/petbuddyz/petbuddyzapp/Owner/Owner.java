package com.petbuddyz.petbuddyzapp.Owner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.petbuddyz.petbuddyzapp.Chat.Chat;
import com.petbuddyz.petbuddyzapp.Comment.Comment;
import com.petbuddyz.petbuddyzapp.Community.Community;
import com.petbuddyz.petbuddyzapp.Group.Group;
import com.petbuddyz.petbuddyzapp.Notification.Notification;
import com.petbuddyz.petbuddyzapp.Pet.Pet;
import com.petbuddyz.petbuddyzapp.Post.Post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "owner")
public class Owner {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long ownerId;

    @NotBlank
    @Size(min = 2, max = 50)
    private String ownerName;

    @NotBlank
    @Size(min = 2, max = 50)
    private String ownerEmail;

    @NotBlank
    @Size(min = 8, max = 100)
    @JsonIgnore
    private String ownerPassword;

    @Size(min = 10, max = 15)
    private String ownerPhone;

    private String ownerPicture;

    private String ownerBio;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ownerProfileCreationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ownerProfileLastUpdateDate;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Pet> ownerPets = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Post> ownerPosts = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Chat> sentChats = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    private List<Chat> receivedChats = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    @ManyToMany(mappedBy = "members", cascade = CascadeType.ALL)
    private List<Group> groups = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "owner_community", joinColumns = @JoinColumn(name = "owner_id"), inverseJoinColumns = @JoinColumn(name = "community_id"))
    @JsonManagedReference
    private List<Community> communities = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "follower_followed", joinColumns = @JoinColumn(name = "follower_id"), inverseJoinColumns = @JoinColumn(name = "followed_id"))
    private Set<Owner> following = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "following")
    private Set<Owner> followers = new HashSet<>();

    public Owner(String ownerName, String ownerEmail, String ownerPassword) {
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.ownerPassword = ownerPassword;
    }

    @PrePersist
    protected void onCreate() {
        Date date = new Date();
        ownerProfileCreationDate = date;
        ownerProfileLastUpdateDate = date;
    }

    @PreUpdate
    protected void onUpdate() {
        ownerProfileLastUpdateDate = new Date();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId); // Or use any unique identifier for the owner
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Owner otherOwner = (Owner) obj;
        return Objects.equals(ownerId, otherOwner.ownerId);
    }
}