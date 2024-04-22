package com.petbuddyz.petbuddyzapp.Pet;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.petbuddyz.petbuddyzapp.Community.Community;
import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Post.Post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pets")
public class Pet {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long petId;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    @JsonBackReference
    private Owner owner;

    @JsonProperty("ownerId")
    private Long getOwnerId() {
        return owner.getOwnerId();
    }

    @NotBlank
    @Size(max = 50)
    private String petName;

    private String petPicture;

    @Size(max = 500)
    private String petBio;

    @Size(max = 50)
    private String petBreed;

    @Size(max = 50)
    private String petAge;

    @Temporal(TemporalType.TIMESTAMP)
    private Date petProfileCreationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date petProfileLastUpdateDate;

    @OneToMany(mappedBy = "pet", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.REMOVE})
    @JsonBackReference
    private List<Post> petPostList;

    @ManyToMany(mappedBy = "petsList")
    @JsonBackReference
    private List<Community> communities;

    @PrePersist
    protected void onCreate() {
        Date date = new Date();
        petProfileCreationDate = date;
        petProfileLastUpdateDate = date;
    }

    @PreUpdate
    protected void onUpdate() {
        petProfileLastUpdateDate = new Date();
    }

    // TODO: Add more fields to your Pet entity and PetDTO to allow owners to
    // share more information about their pets, such as breed, age, favorite toys,
    // and photos.
}
