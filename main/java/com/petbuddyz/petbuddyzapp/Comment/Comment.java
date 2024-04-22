package com.petbuddyz.petbuddyzapp.Comment;

import java.util.Date;
import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.petbuddyz.petbuddyzapp.Like.Like;
import com.petbuddyz.petbuddyzapp.Owner.Owner;
import com.petbuddyz.petbuddyzapp.Post.Post;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
public class Comment {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    @JsonBackReference
    private Post post;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    @JsonBackReference
    private Owner owner;

    @NotBlank
    @Size(min = 1, max = 255)
    private String content;

    @ManyToMany
    private List<Owner> commentLikers;

    @Temporal(TemporalType.TIMESTAMP)
    private Date commentCreationDate;

    @PrePersist
    void commentCreationDate() {
        this.commentCreationDate = new Date();
    }

}
