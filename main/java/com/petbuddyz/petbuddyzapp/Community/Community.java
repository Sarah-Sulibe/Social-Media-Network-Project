package com.petbuddyz.petbuddyzapp.Community;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Pet.Pet;
import com.petbuddyz.petbuddyzapp.Post.Post;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "community")
public class Community {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long communityId;

    @NotBlank
    @Size(min = 2, max = 50)
    private String communityName;

    @NotBlank
    @Size(min = 2, max = 100)
    private String communityDescription;

    @OneToOne
    private Owner communityOwner;

    @ManyToMany
    private List<Pet> petsList;

    @ManyToMany(mappedBy = "communities")
    private List<Owner> communityMembers;

    @OneToMany(mappedBy = "community")
    @JsonManagedReference
    private List<Post> postsList;

    @Temporal(TemporalType.TIMESTAMP)
    private Date communityProfileCreationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date communityProfileLastUpdateDate;

    @PrePersist
    protected void onCreate() {
        communityProfileCreationDate = new Date();
        communityProfileLastUpdateDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        communityProfileLastUpdateDate = new Date();
    }
}